package com.thientri.book_area.repository.payment;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thientri.book_area.model.payment.Payment;
import com.thientri.book_area.repository.admin.MonthlyRevenueProjection;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            select coalesce(sum(p.amount), 0)
            from Payment p
            where upper(coalesce(p.status, '')) in ('PAID', 'COMPLETED', 'SUCCESS')
            """)
    BigDecimal sumPaidRevenue();

    @Query("""
            select
                year(p.paidAt) as yearValue,
                month(p.paidAt) as monthValue,
                coalesce(sum(p.amount), 0) as totalRevenue
            from Payment p
            where p.paidAt is not null
              and upper(coalesce(p.status, '')) in ('PAID', 'COMPLETED', 'SUCCESS')
            group by year(p.paidAt), month(p.paidAt)
            order by year(p.paidAt), month(p.paidAt)
            """)
    List<MonthlyRevenueProjection> sumPaidRevenueByMonth();
}
