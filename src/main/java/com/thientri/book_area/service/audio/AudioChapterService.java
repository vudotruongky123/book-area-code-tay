package com.thientri.book_area.service.audio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thientri.book_area.dto.request.audio.AudioChapterRequest;
import com.thientri.book_area.dto.response.audio.AudioChapterResponse;
import com.thientri.book_area.model.audio.AudioChapter;
import com.thientri.book_area.model.audio.Audiobook;
import com.thientri.book_area.repository.audio.AudioChapterRepository;
import com.thientri.book_area.repository.audio.AudiobookRepository;

@Service
public class AudioChapterService {

    private final AudioChapterRepository audioChapterRepository;
    private final AudiobookRepository audiobookRepository;

    public AudioChapterService(AudioChapterRepository audioChapterRepository, AudiobookRepository audiobookRepository) {
        this.audioChapterRepository = audioChapterRepository;
        this.audiobookRepository = audiobookRepository;
    }

    public List<AudioChapterResponse> getAllAudioChapters() {
        List<AudioChapterResponse> list = new ArrayList<>();
        for (AudioChapter audioChapter : audioChapterRepository.findAll()) {
            list.add(mapToResponse(audioChapter));
        }
        return list;
    }

    private AudioChapterResponse mapToResponse(AudioChapter audioChapter) {
        return AudioChapterResponse.builder()
                .id(audioChapter.getId())
                .audiobookId(audioChapter.getAudiobook().getId())
                .title(audioChapter.getTitle())
                .audioUrl(audioChapter.getAudioUrl())
                .duration(audioChapter.getDuration())
                .build();
    }

    @Transactional
    public AudioChapterResponse createAudioChapter(AudioChapterRequest audioChapterRequest) {
        Audiobook updatedAudiobook = audiobookRepository.findById(audioChapterRequest.getAudiobookId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach noi de them chuong moi!"));

        AudioChapter newAudioChapter = new AudioChapter();

        newAudioChapter.setAudiobook(updatedAudiobook);
        newAudioChapter.setTitle(audioChapterRequest.getTitle());
        newAudioChapter.setAudioUrl(audioChapterRequest.getAudioUrl());
        newAudioChapter.setDuration(audioChapterRequest.getDuration());
        updatedAudiobook.setTotalDuration(updatedAudiobook.getTotalDuration() + audioChapterRequest.getDuration());

        AudioChapter audioChapter = audioChapterRepository.save(newAudioChapter);

        audiobookRepository.save(updatedAudiobook);
        return mapToResponse(audioChapter);
    }

    @Transactional
    public AudioChapterResponse updateAudioChapter(Long id, AudioChapterRequest audioChapterRequest) {
        AudioChapter audioChapter = audioChapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay chuong sach noi de cap nhat!"));

        Audiobook bookInAudioChapter = audiobookRepository.findById(audioChapter.getAudiobook().getId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach co trong chuong!"));

        Audiobook updatedAudiobook = audiobookRepository.findById(audioChapterRequest.getAudiobookId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach noi cua chuong sach de cap nhat!"));

        if (bookInAudioChapter.getId().equals(updatedAudiobook.getId())) {
            audioChapter.setAudiobook(updatedAudiobook);
            audioChapter.setTitle(audioChapterRequest.getTitle());
            audioChapter.setAudioUrl(audioChapterRequest.getAudioUrl());
            updatedAudiobook.setTotalDuration(updatedAudiobook.getTotalDuration() - audioChapter.getDuration() + audioChapterRequest.getDuration());
            audioChapter.setDuration(audioChapterRequest.getDuration());

            AudioChapter updatedAudioChapter = audioChapterRepository.save(audioChapter);

            audiobookRepository.save(updatedAudiobook);
            return mapToResponse(updatedAudioChapter);
        } else {
            audioChapter.setAudiobook(updatedAudiobook);
            audioChapter.setTitle(audioChapterRequest.getTitle());
            audioChapter.setAudioUrl(audioChapterRequest.getAudioUrl());
            bookInAudioChapter.setTotalDuration(bookInAudioChapter.getTotalDuration() - audioChapter.getDuration());
            updatedAudiobook.setTotalDuration(updatedAudiobook.getTotalDuration() + audioChapterRequest.getDuration());
            audioChapter.setDuration(audioChapterRequest.getDuration());

            AudioChapter updatedAudioChapter = audioChapterRepository.save(audioChapter);

            audiobookRepository.save(bookInAudioChapter);
            audiobookRepository.save(updatedAudiobook);
            return mapToResponse(updatedAudioChapter);
        }

    }

    @Transactional
    public AudioChapterResponse deleteAudioChapter(Long id) {
        AudioChapter deletedAudioChapter = audioChapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay chuong cua sach noi de thuc hien xoa!"));

        Audiobook updatedAudiobook = audiobookRepository.findById(deletedAudioChapter.getAudiobook().getId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach noi cua chuong bi xoa der cap nhat!"));

        updatedAudiobook.setTotalDuration(updatedAudiobook.getTotalDuration() - deletedAudioChapter.getDuration());
        audiobookRepository.save(updatedAudiobook);
        audioChapterRepository.delete(deletedAudioChapter);
        return mapToResponse(deletedAudioChapter);
    }
}
