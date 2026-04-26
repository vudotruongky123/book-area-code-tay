package com.thientri.book_area.repository.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thientri.book_area.model.catalog.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

}
