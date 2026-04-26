package com.thientri.book_area.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thientri.book_area.model.order.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

}
