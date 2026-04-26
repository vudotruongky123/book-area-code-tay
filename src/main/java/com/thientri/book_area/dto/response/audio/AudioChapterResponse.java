package com.thientri.book_area.dto.response.audio;

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
public class AudioChapterResponse {
    private Long id;
    private Long audiobookId;
    private String title;
    private String audioUrl;
    private Integer duration;
}
