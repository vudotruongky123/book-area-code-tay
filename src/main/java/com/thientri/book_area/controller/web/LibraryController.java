package com.thientri.book_area.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.model.catalog.Book;
import com.thientri.book_area.model.catalog.Category;
import com.thientri.book_area.repository.catalog.BookRepository;
import com.thientri.book_area.repository.catalog.CategoryRepository;

@RestController
@RequestMapping("/data")
@CrossOrigin("*")
public class LibraryController {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    

    @GetMapping("/book")
    public List<Book> allBook() {
        return bookRepository.findAllFull();
    }

    @GetMapping("/category")
    public List<Category> allCategory() {
        return categoryRepository.findAll();
    }
    
    @GetMapping("/bookdetails/{id}")
    public Book bookdetail(@PathVariable long id) {
        return bookRepository.findByIdBook(id).get();
    }
}
