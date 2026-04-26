package com.thientri.book_area.dto.response.catalog;

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
public class BookImageResponse {
    private Long id;
    private Long bookId;
    private String imageUrl;
}
