package com.thientri.book_area.dto.request.audio;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Bỏ thuộc tính duration đi
public class AudioChapterRequest {
    private Long audiobookId;
    private String title;
    private MultipartFile fileAudio;
}
