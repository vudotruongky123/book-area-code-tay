package com.thientri.book_area.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.response.admin.AdminDashboardSummaryResponse;
import com.thientri.book_area.dto.response.admin.AdminMonthlyStatsResponse;
import com.thientri.book_area.service.admin.AdminDashboardService;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/summary")
    public AdminDashboardSummaryResponse getSummary() {
        return adminDashboardService.getSummary();
    }

    @GetMapping("/monthly-stats")
    public List<AdminMonthlyStatsResponse> getMonthlyStats() {
        return adminDashboardService.getMonthlyStats();
    }
}
