package com.thientri.book_area.dto.request.audio;

import java.util.ArrayList;
import java.util.List;

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
public class AudiobookRequest {
    private Long bookId;
    private List<Long> narratorIds = new ArrayList<>();
}
