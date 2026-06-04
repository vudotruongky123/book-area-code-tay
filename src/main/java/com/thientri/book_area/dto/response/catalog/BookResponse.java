package com.thientri.book_area.dto.response.catalog;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String publisherName;
    private String pdfObjectName;
    private String coverObjectName;
    private String pdfUrl;
    private String coverUrl;
    private LocalDateTime createdAt;
}
