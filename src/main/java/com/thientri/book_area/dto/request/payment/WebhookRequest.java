package com.thientri.book_area.dto.request.payment;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookRequest {
    private String transactionId; // ID giao dịch từ của ngân hàng
    private BigDecimal amountIn; // Số tiền đã thanh toán
    private String transactionContent; // Nội dung giao dịch
}
