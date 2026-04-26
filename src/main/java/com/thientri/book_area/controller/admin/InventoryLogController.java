package com.thientri.book_area.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.request.inventory.InventoryLogRequest;
import com.thientri.book_area.dto.response.inventory.InventoryLogResponse;
import com.thientri.book_area.service.inventory.InventoryLogService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/inventory-logs")
public class InventoryLogController {

    private final InventoryLogService inventoryLogService;

    public InventoryLogController(InventoryLogService inventoryLogService) {
        this.inventoryLogService = inventoryLogService;
    }

    @GetMapping
    public List<InventoryLogResponse> getInventoryLogs() {
        return inventoryLogService.getAllInventoryLogs();
    }

    @PostMapping
    public InventoryLogResponse addNewInventoryLog(@RequestBody InventoryLogRequest inventoryLogRequest) {
        return inventoryLogService.createInventoryLog(inventoryLogRequest);
    }

}
