package com.thientri.book_area.service.catalog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thientri.book_area.dto.request.catalog.BookRequest;
import com.thientri.book_area.dto.response.catalog.BookResponse;
import com.thientri.book_area.model.catalog.Book;
import com.thientri.book_area.model.catalog.Publisher;
import com.thientri.book_area.repository.catalog.BookRepository;
import com.thientri.book_area.repository.catalog.PublisherRepository;
import com.thientri.book_area.service.minio.MinioService;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final MinioService minioService;

    public BookService(BookRepository bookRepository, PublisherRepository publisherRepository,
            MinioService minioService) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.minioService = minioService;
    }

    public List<BookResponse> getAllBooks() {
        List<BookResponse> list = new ArrayList<>();
        for (Book book : bookRepository.findAll()) {
            list.add(mapToResponse(book));
        }
        return list;
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

    public BookResponse createBook(BookRequest bookRequest) {
        Publisher publisher = publisherRepository.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà xuất bản!"));

        Book newBook = new Book();
        newBook.setTitle(bookRequest.getTitle());
        newBook.setPageNumber(bookRequest.getPageNumber());
        newBook.setDescription(bookRequest.getDescription());
        newBook.setPrice(bookRequest.getPrice());
        newBook.setStock(bookRequest.getStock());
        newBook.setPublisher(publisher);

        if (bookRequest.getFileBook() != null && !bookRequest.getFileBook().isEmpty()) {
            try {
                String uploadedObjectName = minioService.uploadFile(bookRequest.getFileBook());
                newBook.setPdfObjectName(uploadedObjectName);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi upload file: " + e.getMessage());
            }
        }

        Book savedBook = bookRepository.save(newBook);
        return mapToResponse(savedBook);
    }

    public BookResponse updateBook(Long id, BookRequest bookRequest) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách để cập nhật!"));

        Publisher publisher = publisherRepository.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà xuất bản để cập nhật sách!"));

        book.setTitle(bookRequest.getTitle());
        book.setPageNumber(bookRequest.getPageNumber());
        book.setDescription(bookRequest.getDescription());
        book.setPrice(bookRequest.getPrice());
        book.setStock(bookRequest.getStock());
        book.setPublisher(publisher);

        if (bookRequest.getFileBook() != null && !bookRequest.getFileBook().isEmpty()) {
            try {
                if (book.getPdfObjectName() != null) {
                    minioService.deleteFile(book.getPdfObjectName());
                }
                String newObjectName = minioService.uploadFile(bookRequest.getFileBook());
                book.setPdfObjectName(newObjectName);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi cập nhật file: " + e.getMessage());
            }
        }

        return mapToResponse(bookRepository.save(book));
    }

    public BookResponse deleteBook(Long id) {
        Book deletedBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách để xóa!"));

        if (deletedBook.getPdfObjectName() != null) {
            try {
                minioService.deleteFile(deletedBook.getPdfObjectName());
            } catch (Exception e) {
                System.err.println("Không thể xóa file trên MinIO: " + e.getMessage());
            }
        }

        bookRepository.deleteById(id);
        return mapToResponse(deletedBook);
    }
}
