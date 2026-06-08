package com.thientri.book_area.dto.request.order;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private List<Long> selectedCartItemIds; // Danh sách ID của các CartItem được chọn để đặt hàng
    private Long addressId;
    private Long paymentMethodId;
}
