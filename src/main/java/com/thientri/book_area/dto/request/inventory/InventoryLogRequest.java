package com.thientri.book_area.dto.request.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryLogRequest {
    private Long bookId;
    private Integer changeAmount;
    private String reason;
}
