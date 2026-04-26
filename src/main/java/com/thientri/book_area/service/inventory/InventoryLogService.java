package com.thientri.book_area.service.inventory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thientri.book_area.dto.request.inventory.InventoryLogRequest;
import com.thientri.book_area.dto.response.inventory.InventoryLogResponse;
import com.thientri.book_area.model.catalog.Book;
import com.thientri.book_area.model.inventory.InventoryLog;
import com.thientri.book_area.repository.catalog.BookRepository;
import com.thientri.book_area.repository.inventory.InventoryLogRepository;

@Service
public class InventoryLogService {

    private final InventoryLogRepository inventoryLogRepository;
    private final BookRepository bookRepository;

    public InventoryLogService(InventoryLogRepository inventoryLogRepository, BookRepository bookRepository) {
        this.inventoryLogRepository = inventoryLogRepository;
        this.bookRepository = bookRepository;
    }

    public List<InventoryLogResponse> getAllInventoryLogs() {
        List<InventoryLogResponse> list = new ArrayList<>();
        for (InventoryLog inventoryLog : inventoryLogRepository.findAll()) {
            list.add(mapToResponse(inventoryLog));
        }
        return list;
    }

    private InventoryLogResponse mapToResponse(InventoryLog inventoryLog) {
        return InventoryLogResponse.builder()
                .id(inventoryLog.getId())
                .bookId(inventoryLog.getBook().getId())
                .changeAmount(inventoryLog.getChangeAmount())
                .reason(inventoryLog.getReason())
                .createdAt(inventoryLog.getCreatedAt())
                .build();
    }

    @Transactional
    public InventoryLogResponse createInventoryLog(InventoryLogRequest inventoryLogRequest) {
        Book book = bookRepository.findById(inventoryLogRequest.getBookId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach de cap nhat so luong!"));
        InventoryLog newInventoryLog = new InventoryLog();
        newInventoryLog.setBook(book);
        newInventoryLog.setChangeAmount(inventoryLogRequest.getChangeAmount());
        book.setStock(book.getStock() + inventoryLogRequest.getChangeAmount());
        newInventoryLog.setReason(inventoryLogRequest.getReason());
        bookRepository.save(book);
        return mapToResponse(inventoryLogRepository.save(newInventoryLog));
    }
}
