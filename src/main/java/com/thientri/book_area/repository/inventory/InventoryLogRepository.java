package com.thientri.book_area.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thientri.book_area.model.inventory.InventoryLog;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

}
