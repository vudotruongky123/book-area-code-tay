package com.thientri.book_area.repository.admin;

import java.math.BigDecimal;

public interface MonthlyRevenueProjection {
    Integer getYearValue();
    Integer getMonthValue();
    BigDecimal getTotalRevenue();
}
