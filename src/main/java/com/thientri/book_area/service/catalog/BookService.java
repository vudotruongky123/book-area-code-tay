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

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;

    public BookService(BookRepository bookRepository, PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
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
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
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
        newBook.setDescription(bookRequest.getDescription());
        newBook.setPrice(bookRequest.getPrice());
        newBook.setStock(bookRequest.getStock());
        newBook.setPublisher(publisher);

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
        book.setDescription(bookRequest.getDescription());
        book.setPrice(bookRequest.getPrice());
        book.setStock(bookRequest.getStock());
        book.setPublisher(p);
        return mapToResponse(bookRepository.save(book));
    }

    // Xoa sach theo ma
    public BookResponse deleteBook(Long id) {
        Book deletedBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach de xoa!"));
        bookRepository.deleteById(id);
        return mapToResponse(deletedBook);
    }
}
