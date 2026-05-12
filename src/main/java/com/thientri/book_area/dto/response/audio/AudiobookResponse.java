package com.thientri.book_area.dto.response.audio;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AudiobookResponse {
    private Long id;
    private Long bookId;
    private Integer totalDuration;

    @Builder.Default
    private Set<String> narratorNames = new HashSet<>();
}
