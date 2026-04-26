package com.thientri.book_area.repository.audio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thientri.book_area.model.audio.AudioChapter;

@Repository
public interface AudioChapterRepository extends JpaRepository<AudioChapter, Long> {

}
