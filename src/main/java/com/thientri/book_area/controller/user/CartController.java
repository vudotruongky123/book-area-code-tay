package com.thientri.book_area.controller.user;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.request.order.CartItemRequest;
import com.thientri.book_area.dto.response.order.CartItemResponse;
import com.thientri.book_area.service.order.CartService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public List<CartItemResponse> getCartItems(Principal principal) {
        // Lấy email trực tiếp từ thẻ đã được chứng thực
        String email = principal.getName();
        return cartService.getCart(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addToCart(Principal principal, @RequestBody CartItemRequest request) {
        String email = principal.getName();
        cartService.addToCart(email, request);
    }

    @PutMapping("/{cartItemId}")
    public CartItemResponse updateCartItem(Principal principal, @PathVariable Long cartItemId,
            @RequestParam Integer newQuantity) {
        String email = principal.getName();
        return cartService.updateQuantity(email, cartItemId, newQuantity);
    }

    @DeleteMapping("/{cartItemId}")
    public CartItemResponse deleteCartItem(Principal principal, @PathVariable Long cartItemId) {
        String email = principal.getName();
        return cartService.deleteCartItem(email, cartItemId);
    }
}
