package com.thientri.book_area.controller.web;

import com.thientri.book_area.service.catalog.BookService;
import com.thientri.book_area.service.catalog.CategoryService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.request.catalog.BookSearchRequest;
import com.thientri.book_area.dto.response.catalog.BookResponse;
import com.thientri.book_area.dto.response.catalog.CategoryResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public")
@CrossOrigin("*")
@RequiredArgsConstructor
public class LibraryController {
    private final BookService bookService;
    private final CategoryService categoryService;

    @GetMapping("/books")
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books/search")
    public Page<BookResponse> bookFilter(@ModelAttribute BookSearchRequest searchRequest, Pageable pageable) {
        return bookService.searchBooks(searchRequest, pageable);
    }

    @GetMapping("/books/{id}")
    public BookResponse getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
