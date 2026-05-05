package com.thientri.book_area.dto.response.catalog;

import java.math.BigDecimal;

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
public class BookResponse {
    private Long id;
    private String title;
    private String fileName;
    private String bookUrl;
    private Integer pageNumber;
    private BigDecimal price;
    private Integer stock;
    private String publisherName;
}
