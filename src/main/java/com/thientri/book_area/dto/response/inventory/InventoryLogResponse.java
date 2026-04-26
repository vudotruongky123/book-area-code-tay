package com.thientri.book_area.dto.response.inventory;

import java.time.LocalDateTime;

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
public class InventoryLogResponse {
    private Long id;
    private Long bookId;
    private Integer changeAmount;
    private String reason;
    private LocalDateTime createdAt;
}
