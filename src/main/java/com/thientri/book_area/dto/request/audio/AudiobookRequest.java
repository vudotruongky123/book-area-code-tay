package com.thientri.book_area.dto.request.audio;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AudiobookRequest {
    private Long bookId;
    @Builder.Default
    private List<Long> narratorIds = new ArrayList<>();
}
