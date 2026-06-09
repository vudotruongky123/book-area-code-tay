package com.thientri.book_area.dto.request.catalog;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchRequest {
    private String keyword; // Tìm theo tên sách
    private List<Long> categoryIds; // Lọc theo nhiều danh mục (VD: Chọn cả Kinh tế & CNTT)
    private List<Long> authorIds; // Lọc theo nhiều tác giả
    private Long publisherId; // Lọc theo nhà xuất bản
    private BigDecimal minPrice; // Giá từ
    private BigDecimal maxPrice; // Giá đến
}