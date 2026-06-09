package com.thientri.book_area.service.catalog;

import java.util.ArrayList;
import java.util.List;

import com.thientri.book_area.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thientri.book_area.dto.request.catalog.BookRequest;
import com.thientri.book_area.dto.request.catalog.BookSearchRequest;
import com.thientri.book_area.dto.response.catalog.BookResponse;
import com.thientri.book_area.exception.ResourceNotFoundException;
import com.thientri.book_area.model.catalog.Book;
import com.thientri.book_area.model.catalog.Publisher;
import com.thientri.book_area.repository.catalog.BookRepository;
import com.thientri.book_area.repository.catalog.BookSpecification;
import com.thientri.book_area.repository.catalog.PublisherRepository;
import com.thientri.book_area.service.minio.MinioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final MinioService minioService;

    public List<BookResponse> getAllBooks() {
        List<BookResponse> list = new ArrayList<>();
        for (Book book : bookRepository.findAll()) {
            list.add(mapToResponse(book));
        }
        return list;
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay sach co ID: " + id));
        return mapToResponse(book);
    }

    private BookResponse mapToResponse(Book book) {
        String publisherName = book.getPublisher() == null ? null : book.getPublisher().getName();

        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .price(book.getPrice())
                .stock(book.getStock())
                .publisherName(publisherName)
                .pdfObjectName(book.getPdfObjectName())
                .coverObjectName(book.getCoverObjectName())
                .pdfUrl(getPresignedUrl(book.getPdfObjectName()))
                .coverUrl(getPresignedUrl(book.getCoverObjectName()))
                .createdAt(book.getCreatedAt())
                .build();
    }

    private String getPresignedUrl(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            return null;
        }

        try {
            return minioService.getUrl(objectName);
        } catch (Exception e) {
            System.err.println("Không thể tạo presigned URL cho object " + objectName + ": " + e.getMessage());
            return null;
        }
    }

    // ... (Giữ nguyên các hàm cũ ở trên)

    public BookResponse createBook(BookRequest bookRequest) {
        Publisher publisher = publisherRepository.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà xuất bản!")); // Sửa Exception

        Book newBook = new Book();
        newBook.setTitle(bookRequest.getTitle());
        newBook.setPageNumber(bookRequest.getPageNumber());
        newBook.setDescription(bookRequest.getDescription());
        newBook.setPrice(bookRequest.getPrice());
        newBook.setStock(bookRequest.getStock());
        newBook.setPublisher(publisher);

        try {
            // Upload PDF (Nếu có)
            if (bookRequest.getFileBook() != null && !bookRequest.getFileBook().isEmpty()) {
                String pdfObjectName = minioService.uploadFile(bookRequest.getFileBook());
                newBook.setPdfObjectName(pdfObjectName);
            }

            // Upload Ảnh bìa (Nếu có)
            if (bookRequest.getCoverImage() != null && !bookRequest.getCoverImage().isEmpty()) {
                String coverObjectName = minioService.uploadFile(bookRequest.getCoverImage());
                newBook.setCoverObjectName(coverObjectName);
            }
        } catch (Exception e) {
            throw new BadRequestException("Lỗi upload file lên hệ thống: " + e.getMessage()); // Sửa Exception
        }

        return mapToResponse(bookRepository.save(newBook));
    }

    public BookResponse updateBook(Long id, BookRequest bookRequest) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách để cập nhật!")); // Sửa Exception

        Publisher publisher = publisherRepository.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà xuất bản để cập nhật sách!")); // Sửa
                                                                                                                    // Exception

        book.setTitle(bookRequest.getTitle());
        book.setPageNumber(bookRequest.getPageNumber());
        book.setDescription(bookRequest.getDescription());
        book.setPrice(bookRequest.getPrice());
        book.setStock(bookRequest.getStock());
        book.setPublisher(publisher);

        try {
            // Cập nhật PDF
            if (bookRequest.getFileBook() != null && !bookRequest.getFileBook().isEmpty()) {
                if (book.getPdfObjectName() != null) {
                    minioService.deleteFile(book.getPdfObjectName());
                }
                book.setPdfObjectName(minioService.uploadFile(bookRequest.getFileBook()));
            }

            // Cập nhật Ảnh bìa
            if (bookRequest.getCoverImage() != null && !bookRequest.getCoverImage().isEmpty()) {
                if (book.getCoverObjectName() != null) {
                    minioService.deleteFile(book.getCoverObjectName());
                }
                book.setCoverObjectName(minioService.uploadFile(bookRequest.getCoverImage()));
            }
        } catch (Exception e) {
            throw new BadRequestException("Lỗi cập nhật file: " + e.getMessage()); // Sửa Exception
        }

        return mapToResponse(bookRepository.save(book));
    }

    public BookResponse deleteBook(Long id) {
        Book deletedBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách để xóa!")); // Sửa Exception

        try {
            // Xóa file PDF
            if (deletedBook.getPdfObjectName() != null) {
                minioService.deleteFile(deletedBook.getPdfObjectName());
            }
            // Xóa file Ảnh bìa
            if (deletedBook.getCoverObjectName() != null) {
                minioService.deleteFile(deletedBook.getCoverObjectName());
            }
        } catch (Exception e) {
            System.err.println("Không thể xóa file trên MinIO: " + e.getMessage());
        }

        bookRepository.deleteById(id);
        return mapToResponse(deletedBook);
    }

    // Tìm kiếm sách đa điều kiện
    public Page<BookResponse> searchBooks(BookSearchRequest searchRequest, Pageable pageable) {
        // Tạo bộ lọc
        Specification<Book> spec = BookSpecification.filterBooks(searchRequest);

        // Gọi Repository chạy truy vấn
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);

        // Chuyển thành DTO (Page của Spring tự động hỗ trợ map())
        return bookPage.map(this::mapToResponse);
    }
}
