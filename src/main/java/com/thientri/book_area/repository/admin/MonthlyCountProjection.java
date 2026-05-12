package com.thientri.book_area.repository.admin;

public interface MonthlyCountProjection {
    Integer getYearValue();
    Integer getMonthValue();
    Long getTotalValue();
}
