package com.thientri.book_area.service.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thientri.book_area.dto.request.order.CartItemRequest;
import com.thientri.book_area.model.catalog.Book;
import com.thientri.book_area.model.order.Cart;
import com.thientri.book_area.model.user.User;
import com.thientri.book_area.repository.catalog.BookRepository;
import com.thientri.book_area.repository.order.CartRepository;
import com.thientri.book_area.repository.user.UserRepository;

@Service
public class CartService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;

    public CartService(UserRepository userRepository, BookRepository bookRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public void addToCart(String email, CartItemRequest request) {
        // 1. Tìm chủ nhân của chiếc thẻ (User)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // 2. Lấy giỏ hàng của người đó (Hoặc tạo mới nếu chưa có để đề phòng rủi ro)
        Cart cart = user.getCart();
        if (cart == null) {
            user.initCart();
            cart = user.getCart();
        }

        // 3. Tìm cuốn sách mà khách muốn thêm
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Sách không tồn tại!"));

        // 4. KIỂM TRA SÁCH ĐÃ CÓ TRONG GIỎ HAY CHƯA
        

        // 5. Lưu lại giỏ hàng
        cartRepository.save(cart);
    }
}
