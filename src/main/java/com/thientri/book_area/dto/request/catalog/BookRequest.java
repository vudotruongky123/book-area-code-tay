package com.thientri.book_area.dto.request.catalog;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

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
public class BookRequest {
    private String title;
    private MultipartFile fileBook;
    private Integer pageNumber;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long publisherId;
}
