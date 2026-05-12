package com.thientri.book_area.service.admin;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.thientri.book_area.dto.response.admin.AdminDashboardSummaryResponse;
import com.thientri.book_area.dto.response.admin.AdminMonthlyStatsResponse;
import com.thientri.book_area.repository.admin.MonthlyCountProjection;
import com.thientri.book_area.repository.admin.MonthlyRevenueProjection;
import com.thientri.book_area.repository.audio.AudioChapterRepository;
import com.thientri.book_area.repository.catalog.AuthorRepository;
import com.thientri.book_area.repository.catalog.BookRepository;
import com.thientri.book_area.repository.catalog.CategoryRepository;
import com.thientri.book_area.repository.engagement.ReviewRepository;
import com.thientri.book_area.repository.engagement.WishlistItemRepository;
import com.thientri.book_area.repository.payment.PaymentRepository;
import com.thientri.book_area.repository.user.UserRepository;

@Service
public class AdminDashboardService {
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final AudioChapterRepository audioChapterRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            AuthorRepository authorRepository,
            AudioChapterRepository audioChapterRepository,
            WishlistItemRepository wishlistItemRepository,
            ReviewRepository reviewRepository,
            PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.audioChapterRepository = audioChapterRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.reviewRepository = reviewRepository;
        this.paymentRepository = paymentRepository;
    }

    public AdminDashboardSummaryResponse getSummary() {
        BigDecimal totalRevenue = paymentRepository.sumPaidRevenue();

        return AdminDashboardSummaryResponse.builder()
                .totalUsers(userRepository.count())
                .totalBooks(bookRepository.count())
                .totalCategories(categoryRepository.count())
                .totalAuthors(authorRepository.count())
                .totalChapters(audioChapterRepository.count())
                .totalViews(0L)
                .totalFavorites(wishlistItemRepository.count())
                .totalComments(reviewRepository.count())
                .totalReports(0L)
                .pendingReports(0L)
                .totalRevenue(totalRevenue == null ? BigDecimal.ZERO : totalRevenue)
                .build();
    }

    public List<AdminMonthlyStatsResponse> getMonthlyStats() {
        List<MonthlyCountProjection> userCounts = userRepository.countCreatedByMonth();
        List<MonthlyCountProjection> bookCounts = bookRepository.countCreatedByMonth();
        List<MonthlyCountProjection> commentCounts = reviewRepository.countCreatedByMonth();
        List<MonthlyRevenueProjection> revenues = paymentRepository.sumPaidRevenueByMonth();

        YearMonth endMonth = resolveEndMonth(userCounts, bookCounts, commentCounts, revenues);
        Map<YearMonth, AdminMonthlyStatsResponse.AdminMonthlyStatsResponseBuilder> buckets = initBuckets(endMonth, 12);

        applyCounts(buckets, userCounts, MonthlyMetric.NEW_USERS);
        applyCounts(buckets, bookCounts, MonthlyMetric.NEW_BOOKS);
        applyCounts(buckets, commentCounts, MonthlyMetric.COMMENTS);
        applyRevenue(buckets, revenues);

        return buckets.entrySet().stream()
                .map(entry -> entry.getValue()
                        .month(entry.getKey().format(MONTH_FORMATTER))
                        .newChapters(0L)
                        .views(0L)
                        .build())
                .toList();
    }

    private Map<YearMonth, AdminMonthlyStatsResponse.AdminMonthlyStatsResponseBuilder> initBuckets(
            YearMonth endMonth,
            int months) {
        Map<YearMonth, AdminMonthlyStatsResponse.AdminMonthlyStatsResponseBuilder> buckets = new LinkedHashMap<>();
        YearMonth startMonth = endMonth.minusMonths(months - 1L);

        for (int i = 0; i < months; i++) {
            YearMonth month = startMonth.plusMonths(i);
            buckets.put(month, AdminMonthlyStatsResponse.builder()
                    .month(month.format(MONTH_FORMATTER))
                    .newUsers(0L)
                    .newBooks(0L)
                    .newChapters(0L)
                    .views(0L)
                    .comments(0L)
                    .revenue(BigDecimal.ZERO));
        }

        return buckets;
    }

    private YearMonth resolveEndMonth(
            List<MonthlyCountProjection> userCounts,
            List<MonthlyCountProjection> bookCounts,
            List<MonthlyCountProjection> commentCounts,
            List<MonthlyRevenueProjection> revenues) {
        YearMonth latestDataMonth = YearMonth.now();

        for (MonthlyCountProjection point : userCounts) {
            latestDataMonth = maxMonth(latestDataMonth, point.getYearValue(), point.getMonthValue());
        }
        for (MonthlyCountProjection point : bookCounts) {
            latestDataMonth = maxMonth(latestDataMonth, point.getYearValue(), point.getMonthValue());
        }
        for (MonthlyCountProjection point : commentCounts) {
            latestDataMonth = maxMonth(latestDataMonth, point.getYearValue(), point.getMonthValue());
        }
        for (MonthlyRevenueProjection point : revenues) {
            latestDataMonth = maxMonth(latestDataMonth, point.getYearValue(), point.getMonthValue());
        }

        return latestDataMonth;
    }

    private YearMonth maxMonth(YearMonth current, Integer year, Integer month) {
        if (year == null || month == null) {
            return current;
        }

        YearMonth candidate = YearMonth.of(year, month);
        return candidate.isAfter(current) ? candidate : current;
    }

    private void applyCounts(
            Map<YearMonth, AdminMonthlyStatsResponse.AdminMonthlyStatsResponseBuilder> buckets,
            List<MonthlyCountProjection> counts,
            MonthlyMetric metric) {
        for (MonthlyCountProjection point : counts) {
            if (point.getYearValue() == null || point.getMonthValue() == null) {
                continue;
            }

            YearMonth month = YearMonth.of(point.getYearValue(), point.getMonthValue());
            AdminMonthlyStatsResponse.AdminMonthlyStatsResponseBuilder bucket = buckets.get(month);
            if (bucket == null) {
                continue;
            }

            long value = point.getTotalValue() == null ? 0L : point.getTotalValue();
            switch (metric) {
                case NEW_USERS -> bucket.newUsers(value);
                case NEW_BOOKS -> bucket.newBooks(value);
                case COMMENTS -> bucket.comments(value);
            }
        }
    }

    private void applyRevenue(
            Map<YearMonth, AdminMonthlyStatsResponse.AdminMonthlyStatsResponseBuilder> buckets,
            List<MonthlyRevenueProjection> revenues) {
        for (MonthlyRevenueProjection point : revenues) {
            if (point.getYearValue() == null || point.getMonthValue() == null) {
                continue;
            }

            YearMonth month = YearMonth.of(point.getYearValue(), point.getMonthValue());
            AdminMonthlyStatsResponse.AdminMonthlyStatsResponseBuilder bucket = buckets.get(month);
            if (bucket == null) {
                continue;
            }

            bucket.revenue(point.getTotalRevenue() == null ? BigDecimal.ZERO : point.getTotalRevenue());
        }
    }

    private enum MonthlyMetric {
        NEW_USERS,
        NEW_BOOKS,
        COMMENTS
    }
}
