package com.thientri.book_area.service.audio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.thientri.book_area.dto.request.audio.AudiobookRequest;
import com.thientri.book_area.dto.response.audio.AudiobookResponse;
import com.thientri.book_area.model.audio.Audiobook;
import com.thientri.book_area.model.audio.Narrator;
import com.thientri.book_area.model.catalog.Book;
import com.thientri.book_area.repository.audio.AudiobookRepository;
import com.thientri.book_area.repository.audio.NarratorRepository;
import com.thientri.book_area.repository.catalog.BookRepository;

@Service
public class AudiobookService {

    private final AudiobookRepository audiobookRepository;
    private final BookRepository bookRepository;
    private final NarratorRepository narratorRepository;

    public AudiobookService(AudiobookRepository audiobookRepository, BookRepository bookRepository,
            NarratorRepository narratorRepository) {
        this.audiobookRepository = audiobookRepository;
        this.bookRepository = bookRepository;
        this.narratorRepository = narratorRepository;
    }

    public List<AudiobookResponse> getAllAudiobooks() {
        List<AudiobookResponse> list = new ArrayList<>();
        for (Audiobook audiobook : audiobookRepository.findAll()) {
            list.add(mapToResponse(audiobook));
        }
        return list;
    }

    private AudiobookResponse mapToResponse(Audiobook audiobook) {
        Set<String> narratorNames = new HashSet<>();
        for (Narrator narrator : audiobook.getNarrators()) {
            narratorNames.add(narrator.getName());
        }
        return AudiobookResponse.builder()
                .id(audiobook.getId())
                .bookId(audiobook.getBook().getId())
                .totalDuration(audiobook.getTotalDuration())
                .narratorNames(narratorNames)
                .build();
    }

    public AudiobookResponse createAudiobook(AudiobookRequest audiobookRequest) {
        Audiobook newAudiobook = new Audiobook();
        Book book = bookRepository.findById(audiobookRequest.getBookId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach de them audio!"));
        Set<Narrator> narrators = new HashSet<>();
        for (Narrator narrator : narratorRepository.findAllById(audiobookRequest.getNarratorIds())) {
            narrators.add(narrator);
        }
        newAudiobook.setBook(book);
        newAudiobook.setTotalDuration(0);
        newAudiobook.setNarrators(narrators);
        return mapToResponse(audiobookRepository.save(newAudiobook));
    }

    public AudiobookResponse updateAudiobook(Long id, AudiobookRequest audiobookRequest) {
        Audiobook audiobook = audiobookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach noi de cap nhat"));
        audiobook.setBook(bookRepository.findById(audiobookRequest.getBookId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach vat ly cua sach noi!")));
        audiobook.setNarrators(new HashSet<>(narratorRepository.findAllById(audiobookRequest.getNarratorIds())));
        return mapToResponse(audiobookRepository.save(audiobook));
    }

    public AudiobookResponse deleteAudiobook(Long id) {
        Audiobook deletedAudiobook = audiobookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach noi de xoa!"));
        audiobookRepository.deleteById(id);
        return mapToResponse(deletedAudiobook);
    }
}
