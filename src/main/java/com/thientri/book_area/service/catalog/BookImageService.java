package com.thientri.book_area.service.catalog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thientri.book_area.dto.request.catalog.BookImageRequest;
import com.thientri.book_area.dto.response.catalog.BookImageResponse;
import com.thientri.book_area.model.catalog.Book;
import com.thientri.book_area.model.catalog.BookImage;
import com.thientri.book_area.repository.catalog.BookImageRepository;
import com.thientri.book_area.repository.catalog.BookRepository;

@Service
public class BookImageService {

    private final BookImageRepository bookImageRepository;
    private final BookRepository bookRepository;

    public BookImageService(BookImageRepository bookImageRepository, BookRepository bookRepository) {
        this.bookImageRepository = bookImageRepository;
        this.bookRepository = bookRepository;
    }

    public List<BookImageResponse> getAllBookImages() {
        List<BookImageResponse> list = new ArrayList<>();
        for (BookImage bookImage : bookImageRepository.findAll()) {
            list.add(mapToResponse(bookImage));
        }
        return list;
    }

    private BookImageResponse mapToResponse(BookImage bookImage) {
        return BookImageResponse.builder()
                .id(bookImage.getId())
                .bookId(bookImage.getBook().getId())
                .imageUrl(bookImage.getImageUrl())
                .build();
    }

    public BookImageResponse createBookImage(BookImageRequest bookImageRequest) {
        Book book = bookRepository.findById(bookImageRequest.getBookId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach de them moi image cho sach!"));
        BookImage newBookImage = new BookImage();
        newBookImage.setBook(book);
        newBookImage.setImageUrl(bookImageRequest.getImageUrl());
        return mapToResponse(bookImageRepository.save(newBookImage));
    }

    public BookImageResponse updateBookImage(Long id, BookImageRequest bookImageRequest) {
        BookImage bookImage = bookImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay anh de cap nhat!"));
        Book book = bookRepository.findById(bookImageRequest.getBookId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach de cap nhat anh!"));
        bookImage.setBook(book);
        bookImage.setImageUrl(bookImageRequest.getImageUrl());
        return mapToResponse(bookImageRepository.save(bookImage));
    }

    public BookImageResponse deleteBookImage(Long id) {
        BookImage deletedBookImage = bookImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay anh de xoa!"));
        bookImageRepository.deleteById(id);
        return mapToResponse(deletedBookImage);
    }
}
