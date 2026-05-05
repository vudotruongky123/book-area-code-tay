package com.thientri.book_area.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.request.catalog.BookRequest;
import com.thientri.book_area.dto.response.catalog.BookResponse;
import com.thientri.book_area.service.catalog.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookResponse> getBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping(consumes = "multipart/form-data") // Nhận cả FILE và dữ liệu JSON
    public BookResponse addNewBook(@ModelAttribute BookRequest bookRequest) {
        return bookService.createBook(bookRequest);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public BookResponse updateBook(@PathVariable Long id, @ModelAttribute BookRequest bookRequest) {
        return bookService.updateBook(id, bookRequest);
    }

    @DeleteMapping(value = "/{id}")
    public BookResponse deleteBook(@PathVariable Long id) {
        return bookService.deleteBook(id);
    }
}
