package com.thientri.book_area.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.request.audio.AudiobookRequest;
import com.thientri.book_area.dto.response.audio.AudiobookResponse;
import com.thientri.book_area.service.audio.AudiobookService;

@RestController
@RequestMapping("/api/audiobooks")
public class AudiobookController {
    private final AudiobookService audiobookService;

    public AudiobookController(AudiobookService audiobookService) {
        this.audiobookService = audiobookService;
    }

    @GetMapping
    public List<AudiobookResponse> getAudiobooks() {
        return audiobookService.getAllAudiobooks();
    }

    @PostMapping
    public AudiobookResponse addNewAudiobook(@RequestBody AudiobookRequest audiobookRequest) {
        return audiobookService.createAudiobook(audiobookRequest);
    }

    @PutMapping("/{id}")
    public AudiobookResponse updateAudiobook(@PathVariable Long id, @RequestBody AudiobookRequest audiobookRequest) {
        return audiobookService.updateAudiobook(id, audiobookRequest);
    }

    @DeleteMapping("/{id}")
    public AudiobookResponse deleteAudiobook(@PathVariable Long id) {
        return audiobookService.deleteAudiobook(id);
    }
}
