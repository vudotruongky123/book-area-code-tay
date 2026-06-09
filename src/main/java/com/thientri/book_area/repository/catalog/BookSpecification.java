package com.thientri.book_area.repository.catalog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.thientri.book_area.dto.request.catalog.BookSearchRequest;
import com.thientri.book_area.model.catalog.Author;
import com.thientri.book_area.model.catalog.Book;
import com.thientri.book_area.model.catalog.Category;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class BookSpecification {

    public static Specification<Book> filterBooks(BookSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo tên sách
            if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + request.getKeyword().toLowerCase() + "%"));
            }

            // 2. Lọc theo khoảng giá
            if (request.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }

            // 3. Lọc theo NXB
            if (request.getPublisherId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("publisher").get("id"), request.getPublisherId()));
            }

            // 4. Lọc theo danh mục (nhiều danh mục) N-N
            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                Join<Book, Category> categoryJoin = root.join("categories");
                predicates.add(categoryJoin.get("id").in(request.getCategoryIds()));
            }

            // 5. Lọc theo tác giả (nhiều tác giả) N-N
            if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
                Join<Book, Author> authorJoin = root.join("authors");
                predicates.add(authorJoin.get("id").in(request.getAuthorIds()));
            }

            // Quan trọng: Tránh duplicate dữ liệu khi JOIN với các bảng N-N (authors,
            // categories)
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
