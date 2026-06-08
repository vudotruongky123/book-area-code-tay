package com.thientri.book_area.controller.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.request.payment.WebhookRequest;
import com.thientri.book_area.service.order.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {
    private final OrderService orderService;

    // Ngân hàng sẽ gọi vào đây(qua cổng trung gian)
    @PostMapping("/bank-transfer")
    @ResponseStatus(HttpStatus.OK) // Trả về 200 OK để xác nhận đã nhận được Webhook
    public String handleBankTransferWebhook(@RequestBody WebhookRequest request) {
        orderService.handlePaymentWebhook(request);
        return "Webhook processed successfully";
    }
}