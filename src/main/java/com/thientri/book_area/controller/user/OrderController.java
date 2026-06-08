package com.thientri.book_area.controller.user;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.request.order.OrderRequest;
import com.thientri.book_area.dto.response.order.OrderResponse;
import com.thientri.book_area.service.order.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // BỔ SUNG: Mã 201 Created
    public OrderResponse placeOrder(Principal principal, @RequestBody OrderRequest orderRequest) {
        String email = principal.getName();
        return orderService.placeOrder(email, orderRequest);
    }
}
