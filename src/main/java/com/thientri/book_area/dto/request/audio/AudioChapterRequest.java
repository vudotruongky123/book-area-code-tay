package com.thientri.book_area.dto.request.audio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AudioChapterRequest {
    private Long audiobookId;
    private String title;
    private String audioUrl;
    private Integer duration;
}
