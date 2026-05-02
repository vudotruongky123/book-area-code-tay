package com.thientri.book_area.service.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thientri.book_area.dto.request.order.CartItemRequest;
import com.thientri.book_area.dto.response.catalog.BookResponse;
import com.thientri.book_area.dto.response.order.CartItemResponse;
import com.thientri.book_area.model.catalog.Book;
import com.thientri.book_area.model.order.Cart;
import com.thientri.book_area.model.order.CartItem;
import com.thientri.book_area.model.user.User;
import com.thientri.book_area.repository.catalog.BookRepository;
import com.thientri.book_area.repository.order.CartItemRepository;
import com.thientri.book_area.repository.order.CartRepository;
import com.thientri.book_area.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private BookResponse mapBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .price(book.getPrice())
                .stock(book.getStock())
                .publisherName(book.getPublisher().getName())
                .build();
    }

    private CartItemResponse mapToResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .cartId(cartItem.getCart().getId())
                .bookResponse(mapBookResponse(cartItem.getBook()))
                .quantity(cartItem.getQuantity())
                .build();
    }

    // Kiểm tra người dùng trước khi thao tác giỏ hàng
    private User checkUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
    }

    // Thêm sản phẩm vào giỏ hàng
    @Transactional
    public void addToCart(String email, CartItemRequest request) {
        // 1. Tìm chủ nhân của chiếc thẻ (User)
        // 2. Lấy giỏ hàng của người đó (Hoặc tạo mới nếu chưa có để đề phòng rủi ro)
        User user = checkUser(email);
        Cart cart = user.getCart();
        if (cart == null) {
            user.initCart();
            cart = user.getCart();
        }

        // 3. Tìm cuốn sách mà khách muốn thêm
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Sách không tồn tại!"));

        // 4. KIỂM TRA SÁCH ĐÃ CÓ TRONG GIỎ HAY CHƯA
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId())).findFirst();
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            if (cartItem.getQuantity() + request.getQuantity() <= book.getStock()) {
                cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            } else {
                throw new RuntimeException("Sách đã hết hàng, quý khách vui lòng chờ nhập kho!");
            }
        } else {
            if (book.getStock() - request.getQuantity() >= 0) {
                CartItem newCartItem = CartItem.builder()
                        .cart(cart)
                        .book(book)
                        .quantity(request.getQuantity())
                        .build();
                cart.getCartItems().add(newCartItem);
            } else {
                throw new RuntimeException("Sách đã hết hàng, quý khách vui lòng chờ nhập kho!");
            }
        }

        // 5. Lưu lại giỏ hàng
        cartRepository.save(cart);
    }

    // Lấy ra giỏ hàng của người dùng
    public List<CartItemResponse> getCart(String email) {
        Cart cart = checkUser(email).getCart();
        if (cart == null) {
            return new ArrayList<>();// Trả về danh sách rỗng
        }
        return cart.getCartItems().stream().map(this::mapToResponse).toList();
    }

    // Xóa sách khỏi giỏ hàng
    public CartItemResponse deleteCartItem(String email, Long cartItemId) {
        Cart cart = checkUser(email).getCart();

        CartItem deletedCartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach trong gio de xoa!"));

        if (cart.getId().equals(deletedCartItem.getCart().getId())) {
            cartItemRepository.deleteById(cartItemId);
        } else {
            throw new RuntimeException(
                    "Id gio hang cua sach trong gio hang khong trung voi Id gio hang cua nguoi dung!");
        }
        return mapToResponse(deletedCartItem);
    }

    // Cập nhật số lượng sách trong kho
    public CartItemResponse updateQuantity(String email, Long cartItemId, Integer newQuantity) {
        Cart cart = checkUser(email).getCart();

        CartItem updatedCartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach trong gio de cap nhat!"));

        Book book = updatedCartItem.getBook();

        if (cart.getId().equals(updatedCartItem.getCart().getId())) {
            if (newQuantity <= book.getStock() && newQuantity > 0) {
                updatedCartItem.setQuantity(newQuantity);
            } else {
                throw new RuntimeException(
                        "Số lượng cập nhật không hợp lệ hoặc vượt quá tồn kho!");
            }
            return mapToResponse(cartItemRepository.save(updatedCartItem));
        } else {
            throw new RuntimeException(
                    "Id gio hang cua sach trong gio hang khong trung voi Id gio hang cua nguoi dung!");
        }
    }
}
