package com.thientri.book_area.model.order;

import java.math.BigDecimal;

import com.thientri.book_area.model.audio.Audiobook;
import com.thientri.book_area.model.catalog.Book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    
    @ManyToOne
    @JoinColumn(name = "audiobook_id")
    private Audiobook audiobook;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
}
