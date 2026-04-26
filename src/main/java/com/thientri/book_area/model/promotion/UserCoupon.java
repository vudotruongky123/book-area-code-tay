package com.thientri.book_area.model.promotion;

import com.thientri.book_area.model.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_coupons")
public class UserCoupon {
    @EmbeddedId
    private UserCouponId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("couponId")
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "used", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean used = false;
}
