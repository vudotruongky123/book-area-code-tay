package com.thientri.book_area.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thientri.book_area.model.user.User;
import com.thientri.book_area.repository.admin.MonthlyCountProjection;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = { "roles", "addresses" })
    Optional<User> findDetailedByEmail(String email);

    @Query("""
            select
                year(u.createdAt) as yearValue,
                month(u.createdAt) as monthValue,
                count(u) as totalValue
            from User u
            where u.createdAt is not null
            group by year(u.createdAt), month(u.createdAt)
            order by year(u.createdAt), month(u.createdAt)
            """)
    java.util.List<MonthlyCountProjection> countCreatedByMonth();
}
