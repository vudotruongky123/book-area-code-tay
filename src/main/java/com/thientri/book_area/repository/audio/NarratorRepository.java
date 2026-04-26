package com.thientri.book_area.repository.audio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thientri.book_area.model.audio.Narrator;

@Repository
public interface NarratorRepository extends JpaRepository<Narrator, Long> {

}
