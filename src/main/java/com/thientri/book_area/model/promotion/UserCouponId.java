package com.thientri.book_area.model.promotion;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserCouponId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "coupon_id")
    private Long couponId;

    // Bắt buộc phải có để JPA nhận diện tính duy nhất của khóa kép
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserCouponId that = (UserCouponId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(couponId, that.couponId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, couponId);
    }
}
