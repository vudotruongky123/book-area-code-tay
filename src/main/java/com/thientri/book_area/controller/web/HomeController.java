package com.thientri.book_area.controller.web;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.thientri.book_area.dto.response.catalog.AuthorResponse;
import com.thientri.book_area.dto.response.catalog.BookResponse;
import com.thientri.book_area.dto.response.catalog.CategoryResponse;
import com.thientri.book_area.service.catalog.AuthorService;
import com.thientri.book_area.service.catalog.BookService;
import com.thientri.book_area.service.catalog.CategoryService;

@Controller
public class HomeController {

    private static final Locale VIETNAMESE = Locale.forLanguageTag("vi-VN");

    private final BookService bookService;
    private final CategoryService categoryService;
    private final AuthorService authorService;

    public HomeController(BookService bookService, CategoryService categoryService, AuthorService authorService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.authorService = authorService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<ShelfBookView> books = buildBooks(bookService.getAllBooks());
        List<ReadingPathView> readingPaths = buildReadingPaths(categoryService.getAllCategories());
        List<AuthorSpotlightView> authors = buildAuthors(authorService.getAllAuthors());

        model.addAttribute("featuredBook", books.get(0));
        model.addAttribute("shelfBooks", books.subList(1, Math.min(books.size(), 5)));
        model.addAttribute("readingPaths", readingPaths);
        model.addAttribute("authors", authors);
        model.addAttribute("heroPills", List.of(
                "Thu vien mo den khuya, cho nhung dem muon doc them vai trang",
                "Audiobook de nghe khi di chuyen, doc tiep khi da ve nha",
                "Kham pha sach theo tam trang, khong bi day vao mot cho online vo cam"));
        return "home";
    }

    private List<ShelfBookView> buildBooks(List<BookResponse> responses) {
        List<ShelfBookView> books = new ArrayList<>();
        int index = 0;
        for (BookResponse response : responses) {
            String publisher = response.getPublisherName() == null || response.getPublisherName().isBlank()
                    ? "Book Area Selections"
                    : response.getPublisherName();
            books.add(new ShelfBookView(
                    index == 0 ? "Duoc chon cho toi nay" : "Ke sach dang duoc xem",
                    response.getTitle(),
                    publisher,
                    formatPrice(response.getPrice()),
                    stockLabel(response.getStock()),
                    buildExcerpt(response)));
            index++;
            if (books.size() == 5) {
                return books;
            }
        }

        List<ShelfBookView> fallback = List.of(
                new ShelfBookView("Duoc chon cho toi nay", "Dem nay doc gi truoc khi ngu?", "Book Area Editions",
                        "149.000 đ", "Ban doc moi dang ghe", "Mot goi y mo dau nhe, dan ban vao khong khi am va cham cua viec doc dem."),
                new ShelfBookView("Ke sach dang duoc xem", "Tieu thuyet de doc cham", "Nha sach noi bat",
                        "128.000 đ", "Con san", "Nhung cuon sach de o lai lau hon tren tay ban, khong can vot vao mot plot twist."),
                new ShelfBookView("Ke sach dang duoc xem", "Sach tam ly de tu quay ve minh", "Nha xuat ban tre",
                        "172.000 đ", "Moi len ke", "Nhiep dieu doc nhe, de ban bat dau chi sau vai giay phan van."),
                new ShelfBookView("Ke sach dang duoc xem", "Essays cho nhung ngay tri oc qua day", "Doc lap Studio",
                        "116.000 đ", "Nghe cung hop", "Nhung bai viet ngan va sau, hop cho chuyen tau ve nha hoac ca phe sang."),
                new ShelfBookView("Ke sach dang duoc xem", "Fantasy cho cuoi tuan dai", "North Wind Press",
                        "194.000 đ", "Sap het", "Mot the gioi rong, thich hop cho nhung nguoi muon o lai that lau cung mot cau chuyen."));

        books.addAll(fallback.subList(books.size(), fallback.size()));
        return books;
    }

    private List<ReadingPathView> buildReadingPaths(List<CategoryResponse> responses) {
        List<ReadingPathView> readingPaths = new ArrayList<>();
        for (CategoryResponse response : responses) {
            readingPaths.add(new ReadingPathView(
                    response.getName(),
                    categorySummary(response.getName()),
                    categoryCue(response.getName())));
            if (readingPaths.size() == 4) {
                return readingPaths;
            }
        }

        List<ReadingPathView> fallback = List.of(
                new ReadingPathView("Tieu thuyet", "Cho nhung dem can mot the gioi khac de rut khoi nhiep song qua nhanh.", "Doc cham, nga nguoi sau lung ghe"),
                new ReadingPathView("Tam ly", "Nhung cuon sach go tung lop suy nghi, hop cho luc muon hieu minh ro hon.", "Mo ra trong mot buoi chieu yen"),
                new ReadingPathView("Kinh doanh", "Y tuong thuc te, khong thuyet giang dai dong, de dem ngay mai vao viec tot hon.", "Danh cho buoi sang truoc gio lam"),
                new ReadingPathView("Audiobook", "Keo dai mach doc cua ban ngay ca khi dang di duong, tap the duc hoac don dep.", "Bat dau bang tai nghe va 10 phut trong"));

        readingPaths.addAll(fallback.subList(readingPaths.size(), fallback.size()));
        return readingPaths;
    }

    private List<AuthorSpotlightView> buildAuthors(List<AuthorResponse> responses) {
        List<AuthorSpotlightView> authors = new ArrayList<>();
        for (AuthorResponse response : responses) {
            authors.add(new AuthorSpotlightView(
                    response.getName(),
                    initialsOf(response.getName()),
                    "Tac gia dang co tren ke sach cua Book Area, de ban theo doi mot giong viet that hop minh."));
            if (authors.size() == 4) {
                return authors;
            }
        }

        List<AuthorSpotlightView> fallback = List.of(
                new AuthorSpotlightView("Nguyen Nhat Anh", "NA", "Nhung trang sach mem, trong, va du suc giu chan nguoi doc tre."),
                new AuthorSpotlightView("To Hoai", "TH", "Mot giong ke chuyen quen thuoc, hop cho ca doc lai lan dau va lan thu hai."),
                new AuthorSpotlightView("Agatha Christie", "AC", "Cho nhung toi muon len mot chuot nho trong dau ma van doc rat cuon."),
                new AuthorSpotlightView("Haruki Murakami", "HM", "Nhip van thong dong, lang, va co du khoang trong de ban tu di tiep."));

        authors.addAll(fallback.subList(authors.size(), fallback.size()));
        return authors;
    }

    private String buildExcerpt(BookResponse response) {
        String publisher = response.getPublisherName() == null || response.getPublisherName().isBlank()
                ? "mot tuyen chon moi"
                : response.getPublisherName();
        return "An pham tu " + publisher + ", phu hop de bat dau ngay khi ban muon tim mot cuon sach vua tay.";
    }

    private String stockLabel(Integer stock) {
        if (stock == null) {
            return "Dang cap nhat";
        }
        if (stock <= 3) {
            return "Sap het";
        }
        if (stock <= 10) {
            return "Dang duoc chon";
        }
        return "Con san";
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "Dang cap nhat";
        }
        NumberFormat formatter = NumberFormat.getNumberInstance(VIETNAMESE);
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);
        return formatter.format(price) + " đ";
    }

    private String categorySummary(String categoryName) {
        String normalized = categoryName == null ? "" : categoryName.toLowerCase(VIETNAMESE);
        if (normalized.contains("tieu thuyet") || normalized.contains("van hoc")) {
            return "Nhung cuon sach de doc cham, de o lai lau hon mot chut sau khi gap cuoi trang.";
        }
        if (normalized.contains("tam ly") || normalized.contains("ky nang") || normalized.contains("self")) {
            return "Goi y phu hop cho luc ban can quay lai voi minh va sap xep tri oc.";
        }
        if (normalized.contains("kinh doanh") || normalized.contains("tai chinh")) {
            return "Y tuong co the mang thang vao cong viec, nhung van du de doc nhe nguoi.";
        }
        if (normalized.contains("lich su") || normalized.contains("truyen ky")) {
            return "Mot duong doc sau hon, ro hon, cho nguoi thich boi canh va chieu dai cua cau chuyen.";
        }
        if (normalized.contains("audio")) {
            return "Nghe tiep mach doc khi ban can roi khoi man hinh nhung chua muon roi cuon sach.";
        }
        return "Mot loi vao duoc chon cho nhung luc ban chua biet nen doc gi, nhung chac chan muon doc mot thu gi do hay.";
    }

    private String categoryCue(String categoryName) {
        String normalized = categoryName == null ? "" : categoryName.toLowerCase(VIETNAMESE);
        if (normalized.contains("audio")) {
            return "Tai nghe, di bo, tiep tuc";
        }
        if (normalized.contains("kinh doanh") || normalized.contains("tai chinh")) {
            return "Mang vao ngay mai di lam";
        }
        if (normalized.contains("tam ly") || normalized.contains("ky nang")) {
            return "Doc de nhe dau hon";
        }
        return "Bat dau tu section nay";
    }

    private String initialsOf(String name) {
        if (name == null || name.isBlank()) {
            return "BA";
        }

        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(parts[0].length(), 2)).toUpperCase(VIETNAMESE);
        }

        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(VIETNAMESE);
    }

    public record ShelfBookView(String eyebrow, String title, String publisher, String priceLabel, String availability,
            String excerpt) {
    }

    public record ReadingPathView(String title, String summary, String cue) {
    }

    public record AuthorSpotlightView(String name, String initials, String description) {
    }
}
