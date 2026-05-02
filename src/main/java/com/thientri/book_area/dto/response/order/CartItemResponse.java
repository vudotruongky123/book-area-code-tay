package com.thientri.book_area.dto.response.order;

import com.thientri.book_area.dto.response.catalog.BookResponse;

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
public class CartItemResponse {
    private Long id;
    private Long cartId;
    private BookResponse bookResponse;
    private Integer quantity;
}
