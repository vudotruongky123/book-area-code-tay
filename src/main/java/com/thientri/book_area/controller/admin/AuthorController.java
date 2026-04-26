package com.thientri.book_area.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.request.catalog.AuthorRequest;
import com.thientri.book_area.dto.response.catalog.AuthorResponse;
import com.thientri.book_area.service.catalog.AuthorService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public List<AuthorResponse> getAuthors() {
        return authorService.getAllAuthors();
    }

    @PostMapping
    public AuthorResponse addNewAuthor(@RequestBody AuthorRequest authorRequest) {
        return authorService.createAuthor(authorRequest);
    }

    @PutMapping("/{id}")
    public AuthorResponse updateAuthor(@PathVariable Long id, @RequestBody AuthorRequest authorRequest) {
        return authorService.updateAuthor(id, authorRequest);
    }

    @DeleteMapping("/{id}")
    public AuthorResponse deleteAuthor(@PathVariable Long id) {
        return authorService.deleteAuthor(id);
    }
}
