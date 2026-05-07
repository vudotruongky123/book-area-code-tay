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

import com.thientri.book_area.dto.request.catalog.BookImageRequest;
import com.thientri.book_area.dto.response.catalog.BookImageResponse;
import com.thientri.book_area.service.catalog.BookImageService;

@RestController
@RequestMapping("/api/book-images")
public class BookImageController {

    private final BookImageService bookImageService;

    public BookImageController(BookImageService bookImageService) {
        this.bookImageService = bookImageService;
    }

    @GetMapping
    public List<BookImageResponse> getBookImages() {
        return bookImageService.getAllBookImages();
    }

    @PostMapping(consumes = "multipart/form-data") // Khai báo nhận định dạng có chứa File
    public BookImageResponse addNewBookImage(@ModelAttribute BookImageRequest bookImageRequest) { // Đổi thành @ModelAttribute
        return bookImageService.createBookImage(bookImageRequest);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data") // Khai báo nhận định dạng có chứa File
    public BookImageResponse updateBookImage(@PathVariable Long id, @ModelAttribute BookImageRequest bookImageRequest) { // Đổi thành @ModelAttribute
        return bookImageService.updateBookImage(id, bookImageRequest);
    }

    @DeleteMapping("/{id}")
    public BookImageResponse deleteBookImage(@PathVariable Long id) {
        return bookImageService.deleteBookImage(id);
    }
}