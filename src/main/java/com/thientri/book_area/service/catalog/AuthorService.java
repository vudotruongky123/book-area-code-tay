package com.thientri.book_area.service.catalog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thientri.book_area.dto.request.catalog.AuthorRequest;
import com.thientri.book_area.dto.response.catalog.AuthorResponse;
import com.thientri.book_area.model.catalog.Author;
import com.thientri.book_area.repository.catalog.AuthorRepository;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorResponse> getAllAuthors() {
        List<AuthorResponse> list = new ArrayList<>();
        for (Author auth : authorRepository.findAll()) {
            list.add(mapToResponse(auth));
        }
        return list;
    }

    private AuthorResponse mapToResponse(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .build();
    }

    // Them tac gia moi
    public AuthorResponse createAuthor(AuthorRequest authorRequest) {
        Author newAuthor = new Author();
        newAuthor.setName(authorRequest.getName());
        return mapToResponse(authorRepository.save(newAuthor));
    }

    // Cap nhat tac gia
    public AuthorResponse updateAuthor(Long id, AuthorRequest authorRequest) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay tac gia de cap nhat"));
        author.setName(authorRequest.getName());
        return mapToResponse(authorRepository.save(author));
    }

    public AuthorResponse deleteAuthor(Long id) {
        Author deletedAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay tac gia de xoa!"));
        authorRepository.deleteById(id);
        return mapToResponse(deletedAuthor);
    }
}
