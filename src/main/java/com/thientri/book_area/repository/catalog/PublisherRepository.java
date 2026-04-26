package com.thientri.book_area.repository.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thientri.book_area.model.catalog.Publisher;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

}
