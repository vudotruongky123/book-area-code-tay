package com.thientri.book_area.dto.response.order;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {
    private Long orderId;
    private String orderCode;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String paymentMethod;
    private String qrCodeUrl;
}
