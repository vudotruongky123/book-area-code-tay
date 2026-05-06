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

    // Chuyển đổi dữ liệu sách để trả ra giao diện
    private BookResponse mapToResponse(Book book) {
        // Kiểm tên File và lấy đường dẫn File sách
        String url = null;
        if (book.getFileName() != null) {
            try {
                url = minioService.getUrl(book.getFileName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .fileName(book.getFileName())
                .bookUrl(url)
                .pageNumber(book.getPageNumber())
                .price(book.getPrice())
                .stock(book.getStock())
                .publisherName(book.getPublisher().getName())
                .build();
    }

    // Them sach moi
    public BookResponse createBook(BookRequest bookRequest) {
        // Tim nha xuat ban
        Publisher publisher = publisherRepository.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay nha xuat ban!"));

        // Tao book moi va do du lieu tu BookRequest vao
        Book newBook = new Book();
        newBook.setTitle(bookRequest.getTitle());
        newBook.setPageNumber(bookRequest.getPageNumber());
        newBook.setDescription(bookRequest.getDescription());
        newBook.setPrice(bookRequest.getPrice());
        newBook.setStock(bookRequest.getStock());
        newBook.setPublisher(publisher);

        if (bookRequest.getFileBook() != null && !bookRequest.getFileBook().isEmpty()) {
            try {
                String uploadedFileName = minioService.uploadFile(bookRequest.getFileBook());
                newBook.setFileName(uploadedFileName);
            } catch (Exception e) {
                throw new RuntimeException("Loi upload file: " + e.getMessage());
            }
        }

        Book savedBook = bookRepository.save(newBook);
        return mapToResponse(savedBook);
    }

    // Cap nhat sach, neu chua co thi them moi
    public BookResponse updateBook(Long id, BookRequest bookRequest) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach de cap nhat!"));

        Publisher p = publisherRepository.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay nha xuat ban de cap nhat sach!"));

        book.setTitle(bookRequest.getTitle());
        book.setPageNumber(bookRequest.getPageNumber());
        book.setDescription(bookRequest.getDescription());
        book.setPrice(bookRequest.getPrice());
        book.setStock(bookRequest.getStock());
        book.setPublisher(p);

        // Sử lý cập nhật FILE nếu ADMIN tải lên FILE mới
        if (bookRequest.getFileBook() != null && !bookRequest.getFileBook().isEmpty()) {
            try {
                // Xóa file cũ trên MinIO nếu có
                if (book.getFileName() != null) {
                    minioService.deleteFile(book.getFileName());
                }
                // Đổi tên FILE sang tên FILE mới
                String newFileName = minioService.uploadFile(bookRequest.getFileBook());
                book.setFileName(newFileName);
            } catch (Exception e) {
                throw new RuntimeException("Loi cap nhat File: " + e.getMessage());
            }
        }

        return mapToResponse(bookRepository.save(book));
    }

    // Xoa sach theo ma
    public BookResponse deleteBook(Long id) {
        Book deletedBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach de xoa!"));

        if (deletedBook.getFileName() != null) {
            try {
                minioService.deleteFile(deletedBook.getFileName());
            } catch (Exception e) {
                System.err.println("Khong the xoa FILE tren MinIO: " + e.getMessage());
            }
        }

        bookRepository.deleteById(id);
        return mapToResponse(deletedBook);
    }
}
