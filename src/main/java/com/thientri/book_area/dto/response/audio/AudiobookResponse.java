package com.thientri.book_area.dto.response.audio;

import java.util.HashSet;
import java.util.Set;

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
public class AudiobookResponse {
    private Long id;
    private Long bookId;
    private Integer totalDuration;
    private Set<String> narratorNames = new HashSet<>();
}
