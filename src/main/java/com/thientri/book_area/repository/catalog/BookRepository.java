package com.thientri.book_area.repository.catalog;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thientri.book_area.model.catalog.Book;
import com.thientri.book_area.model.catalog.Publisher;
import com.thientri.book_area.repository.admin.MonthlyCountProjection;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContaining(String keyword);     // Tìm sách theo tên

    List<Book> findByPriceLessThan(BigDecimal price);   // Tim sach nho hon mot muc gia duoc chi dinh

    List<Book> findByPublisherAndPriceGreaterThan(Publisher p, BigDecimal price);    // Ket hop dieu kien cua 2 cot lai voi nhau

    @Query("""
            select
                year(b.createdAt) as yearValue,
                month(b.createdAt) as monthValue,
                count(b) as totalValue
            from Book b
            where b.createdAt is not null
            group by year(b.createdAt), month(b.createdAt)
            order by year(b.createdAt), month(b.createdAt)
            """)
    List<MonthlyCountProjection> countCreatedByMonth();
}
