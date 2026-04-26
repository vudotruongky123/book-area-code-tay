package com.thientri.book_area.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.request.audio.AudioChapterRequest;
import com.thientri.book_area.dto.response.audio.AudioChapterResponse;
import com.thientri.book_area.service.audio.AudioChapterService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/audio-chapters")
public class AudioChapterController {

    private final AudioChapterService audioChapterService;

    public AudioChapterController(AudioChapterService audioChapterService) {
        this.audioChapterService = audioChapterService;
    }

    @GetMapping
    public List<AudioChapterResponse> getAudioChapters() {
        return audioChapterService.getAllAudioChapters();
    }

    @PostMapping
    public AudioChapterResponse addNewAudioChapter(@RequestBody AudioChapterRequest audioChapterRequest) {
        return audioChapterService.createAudioChapter(audioChapterRequest);
    }

    @PutMapping("/{id}")
    public AudioChapterResponse updateAudioChapter(@PathVariable Long id,
            @RequestBody AudioChapterRequest audioChapterRequest) {
        return audioChapterService.updateAudioChapter(id, audioChapterRequest);
    }

    @DeleteMapping("/{id}")
    public AudioChapterResponse deleteAudioChapter(@PathVariable Long id) {
        return audioChapterService.deleteAudioChapter(id);
    }
}
