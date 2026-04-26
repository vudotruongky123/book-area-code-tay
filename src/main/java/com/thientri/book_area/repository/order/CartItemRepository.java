package com.thientri.book_area.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thientri.book_area.model.order.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
