package com.thientri.book_area.repository.engagement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thientri.book_area.model.engagement.Review;
import com.thientri.book_area.repository.admin.MonthlyCountProjection;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
            select
                year(r.createdAt) as yearValue,
                month(r.createdAt) as monthValue,
                count(r) as totalValue
            from Review r
            where r.createdAt is not null
            group by year(r.createdAt), month(r.createdAt)
            order by year(r.createdAt), month(r.createdAt)
            """)
    List<MonthlyCountProjection> countCreatedByMonth();
}
