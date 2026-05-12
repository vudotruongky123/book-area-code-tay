USE master;
GO

IF DB_ID('book_area1') IS NOT NULL
BEGIN
    ALTER DATABASE book_area1 SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE book_area1;
END
GO

CREATE DATABASE book_area1;
GO

USE book_area1;
GO

-- =========================
-- 1. BẢNG PHÂN QUYỀN
-- =========================
CREATE TABLE roles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE
);

-- =========================
-- 2. BẢNG USERS
-- =========================
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(255),
    phone NVARCHAR(20) UNIQUE,
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    status NVARCHAR(20) DEFAULT 'ACTIVE'
);

-- =========================
-- 3. NHÀ XUẤT BẢN
-- =========================
CREATE TABLE publishers (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    created_at DATETIME2 DEFAULT SYSDATETIME()
);

-- =========================
-- 4. TÁC GIẢ
-- =========================
CREATE TABLE authors (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255)
);

-- =========================
-- 5. ĐỊA CHỈ USER
-- =========================
CREATE TABLE addresses (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_line NVARCHAR(MAX),
    city NVARCHAR(100),
    country NVARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================
-- 6. USER - ROLE N-N
-- =========================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- =========================
-- 7. SÁCH
-- =========================
CREATE TABLE books (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    price DECIMAL(10, 2) NOT NULL,
    stock INT,
    publisher_id BIGINT,
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    pdf_object_name NVARCHAR(500) NULL,
    cover_object_name NVARCHAR(500) NULL,
    FOREIGN KEY (publisher_id) REFERENCES publishers(id)
);

-- Thêm trường dữ liệu để có thể lưu file và đường dẫn sách

-- Ảnh sách
-- =========================
-- 8. ẢNH SÁCH
-- =========================
CREATE TABLE book_images (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    book_id BIGINT NOT NULL,
    image_file_name NVARCHAR(MAX),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Đổi tên cột để lấy ảnh trên MinIO

-- Bảng liên kết giữa (books & authors) n-n
-- =========================
-- 9. BOOK - AUTHOR N-N
-- =========================
CREATE TABLE book_authors (
    book_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(id)
);

-- =========================
-- 10. DANH MỤC
-- =========================
CREATE TABLE categories (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255)
);

-- =========================
-- 11. BOOK - CATEGORY N-N
-- =========================
CREATE TABLE book_categories (
    book_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, category_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- =========================
-- 12. SÁCH NÓI
-- =========================
CREATE TABLE audiobooks (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    book_id BIGINT UNIQUE,
    total_duration INT,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- =========================
-- 13. CHƯƠNG SÁCH NÓI
-- =========================
CREATE TABLE audio_chapters (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    audiobook_id BIGINT NOT NULL,
    title NVARCHAR(255),
    audio_file_name NVARCHAR(MAX),
    duration INT,
    FOREIGN KEY (audiobook_id) REFERENCES audiobooks(id) ON DELETE CASCADE
);

-- Đổi tên trường dữ liệu để lấy file âm thanh của MinIO

-- Người đọc
-- =========================
-- 14. NGƯỜI ĐỌC
-- =========================
CREATE TABLE narrators (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255)
);

-- =========================
-- 15. AUDIOBOOK - NARRATOR N-N
-- =========================
CREATE TABLE audiobook_narrators (
    audiobook_id BIGINT NOT NULL,
    narrator_id BIGINT NOT NULL,
    PRIMARY KEY (audiobook_id, narrator_id),
    FOREIGN KEY (audiobook_id) REFERENCES audiobooks(id) ON DELETE CASCADE,
    FOREIGN KEY (narrator_id) REFERENCES narrators(id)
);

-- =========================
-- 16. TIẾN ĐỘ NGHE
-- =========================
CREATE TABLE audio_progress (
    user_id BIGINT NOT NULL,
    audiobook_id BIGINT NOT NULL,
    progress INT,
    PRIMARY KEY (user_id, audiobook_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (audiobook_id) REFERENCES audiobooks(id)
);

-- =========================
-- 17. TRẠNG THÁI ĐƠN HÀNG
-- =========================
CREATE TABLE order_status (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE
);

-- =========================
-- 18. ĐƠN HÀNG
-- =========================
CREATE TABLE orders (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    status_id BIGINT,
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id),
    FOREIGN KEY (status_id) REFERENCES order_status(id)
);

-- =========================
-- 19. CHI TIẾT ĐƠN HÀNG
-- =========================
CREATE TABLE order_items (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    book_id BIGINT NULL,
    audiobook_id BIGINT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (audiobook_id) REFERENCES audiobooks(id),
    CHECK (
        (book_id IS NOT NULL AND audiobook_id IS NULL)
        OR
        (book_id IS NULL AND audiobook_id IS NOT NULL)
    )
);

-- =========================
-- 20. LỊCH SỬ TRẠNG THÁI ĐƠN HÀNG
-- =========================
CREATE TABLE order_status_history (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status NVARCHAR(50),
    changed_at DATETIME2 DEFAULT SYSDATETIME(),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- =========================
-- 21. PHƯƠNG THỨC THANH TOÁN
-- =========================
CREATE TABLE payment_methods (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) UNIQUE,
    description NVARCHAR(MAX)
);

-- =========================
-- 22. THANH TOÁN
-- =========================
CREATE TABLE payments (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    payment_method_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status NVARCHAR(50),
    paid_at DATETIME2 DEFAULT SYSDATETIME(),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id)
);

-- =========================
-- 23. GIỎ HÀNG
-- =========================
CREATE TABLE carts (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================
-- 24. CHI TIẾT GIỎ HÀNG
-- =========================
CREATE TABLE cart_items (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- =========================
-- 25. ĐÁNH GIÁ
-- =========================
CREATE TABLE reviews (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    rating INT,
    comment NVARCHAR(MAX),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    UNIQUE (user_id, book_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CHECK (rating BETWEEN 1 AND 5)
);

-- =========================
-- 26. DANH SÁCH YÊU THÍCH
-- =========================
CREATE TABLE wishlists (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================
-- 27. CHI TIẾT WISHLIST
-- =========================
CREATE TABLE wishlist_items (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    wishlist_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    FOREIGN KEY (wishlist_id) REFERENCES wishlists(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- =========================
-- 28. MÃ GIẢM GIÁ
-- =========================
CREATE TABLE coupons (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    code NVARCHAR(50) UNIQUE,
    discount_type NVARCHAR(20),
    discount_value DECIMAL(10, 2),
    max_discount DECIMAL(10, 2),
    expiry_date DATETIME2
);

-- =========================
-- 29. USER - COUPON N-N
-- =========================
CREATE TABLE user_coupons (
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    used BIT DEFAULT 0,
    PRIMARY KEY (user_id, coupon_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (coupon_id) REFERENCES coupons(id) ON DELETE CASCADE
);

-- =========================
-- 30. LỊCH SỬ KHO
-- =========================
CREATE TABLE inventory_logs (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    book_id BIGINT NOT NULL,
    change_amount INT,
    reason NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    FOREIGN KEY (book_id) REFERENCES books(id)
);


-- Tạo bảng lưu Refresh Token (Có tác dụng lưu một chuỗi được tạo ra khi người dùng đăng nhập thành công, và có thời hạn sử dụng lâu hơn Access Token, thường là 7 ngày. Khi Access Token hết hạn, client có thể gửi Refresh Token này lên server để yêu cầu cấp mới Access Token mà không cần phải đăng nhập lại.)
-- =========================
-- 31. REFRESH TOKEN
-- =========================
CREATE TABLE refresh_tokens (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    expiry_date DATETIME2,
    user_id BIGINT NOT NULL,
    refresh_token NVARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================
-- INDEX
-- =========================
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_books_publisher_id ON books(publisher_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_reviews_book_id ON reviews(book_id);
GO
























-- ============================ Đang sử dung ==================================
-- Dữ liẹu mẫu 2
-- =========================
-- DỮ LIỆU MẪU AN TOÀN
-- =========================

INSERT INTO roles (name) VALUES
('ADMIN'),
('CUSTOMER'),
('USER'),
('MANAGER'),
('STAFF'),
('EDITOR'),
('GUEST'),
('VIP'),
('MODERATOR'),
('SUPPORT');

INSERT INTO users (email, password, full_name, phone) VALUES
('admin@bookarea.com', '123456', N'Nguyễn Quản Trị', '0901234567'),
('khachhang@gmail.com', '123456', N'Trần Khách Hàng', '0987654321'),
('user1@gmail.com', '123', N'User 1', '0900000001'),
('user2@gmail.com', '123', N'User 2', '0900000002'),
('user3@gmail.com', '123', N'User 3', '0900000003'),
('user4@gmail.com', '123', N'User 4', '0900000004'),
('user5@gmail.com', '123', N'User 5', '0900000005'),
('user6@gmail.com', '123', N'User 6', '0900000006'),
('user7@gmail.com', '123', N'User 7', '0900000007'),
('user8@gmail.com', '123', N'User 8', '0900000008');

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 3),
(5, 3),
(6, 4),
(7, 3),
(8, 3),
(9, 3),
(10, 3);

INSERT INTO addresses (user_id, address_line, city, country) VALUES
(1, N'Address 1', N'HCM', N'VN'),
(2, N'Address 2', N'HCM', N'VN'),
(3, N'Address 3', N'HN', N'VN'),
(4, N'Address 4', N'DN', N'VN'),
(5, N'Address 5', N'CT', N'VN'),
(6, N'Address 6', N'HP', N'VN'),
(7, N'Address 7', N'HCM', N'VN'),
(8, N'Address 8', N'HN', N'VN'),
(9, N'Address 9', N'DN', N'VN'),
(10, N'Address 10', N'CT', N'VN');

INSERT INTO publishers (name, description) VALUES
(N'NXB Trẻ', N'Nhà xuất bản dành cho giới trẻ'),
(N'Nhã Nam', N'Bởi vì sách là thế giới'),
(N'NXB Kim Đồng', N'Nhà xuất bản sách thiếu nhi'),
(N'NXB Tổng hợp TP.HCM', N'Nhà xuất bản tổng hợp'),
(N'NXB Lao Động', N'Sách tri thức và kỹ năng'),
(N'NXB Giáo Dục', N'Sách giáo dục'),
(N'NXB Văn Học', N'Sách văn học'),
(N'NXB Hội Nhà Văn', N'Tác phẩm văn học'),
(N'NXB Thế Giới', N'Sách quốc tế'),
(N'NXB Dân Trí', N'Sách phổ thông');

INSERT INTO authors (name) VALUES
(N'J.K. Rowling'),
(N'Nguyễn Nhật Ánh'),
(N'Paulo Coelho'),
(N'Dale Carnegie'),
(N'James Clear'),
(N'Yuval Noah Harari'),
(N'Viktor E. Frankl'),
(N'Rosie Nguyễn'),
(N'Haruki Murakami'),
(N'George Orwell');

INSERT INTO categories (name) VALUES
(N'Tiểu thuyết viễn tưởng'),
(N'Truyện dài'),
(N'Kỹ năng sống'),
(N'Kinh doanh'),
(N'Tâm lý học'),
(N'Lịch sử'),
(N'Thiếu nhi'),
(N'Văn học nước ngoài'),
(N'Phát triển bản thân'),
(N'Sách nói');

INSERT INTO books (title, description, price, stock, publisher_id, pdf_object_name, cover_object_name) VALUES
(N'Harry Potter và Hòn đá Phù thủy', N'Tập 1 của series Harry Potter', 150000.00, 50, 1, NULL, NULL),
(N'Mắt Biếc', N'Truyện tình buồn của Ngạn và Hà Lan', 95000.00, 100, 1, NULL, NULL),
(N'Cây Chuối Non Đi Giày Xanh', N'Ký ức tuổi thơ', 110000.00, 30, 2, NULL, NULL),
(N'Nhà Giả Kim', N'Hành trình đi tìm kho báu và ý nghĩa cuộc sống', 89000.00, 80, 2, NULL, NULL),
(N'Đắc Nhân Tâm', N'Nghệ thuật giao tiếp và ứng xử', 120000.00, 60, 5, NULL, NULL),
(N'Atomic Habits', N'Thay đổi nhỏ tạo kết quả lớn', 180000.00, 70, 5, NULL, NULL),
(N'Sapiens', N'Lược sử loài người', 220000.00, 40, 9, NULL, NULL),
(N'Đi Tìm Lẽ Sống', N'Câu chuyện về ý nghĩa cuộc đời', 130000.00, 45, 7, NULL, NULL),
(N'Tuổi Trẻ Đáng Giá Bao Nhiêu', N'Sách truyền cảm hứng cho người trẻ', 100000.00, 90, 1, NULL, NULL),
(N'Rừng Na Uy', N'Tiểu thuyết nổi tiếng của Haruki Murakami', 160000.00, 35, 8, NULL, NULL);

INSERT INTO book_images (book_id, image_file_name) VALUES
(1, N'img1'),
(2, N'img2'),
(3, N'img3'),
(4, N'img4'),
(5, N'img5'),
(6, N'img6'),
(7, N'img7'),
(8, N'img8'),
(9, N'img9'),
(10, N'img10');

INSERT INTO book_authors (book_id, author_id) VALUES
(1, 1),
(2, 2),
(3, 2),
(4, 3),
(5, 4),
(6, 5),
(7, 6),
(8, 7),
(9, 8),
(10, 9);

INSERT INTO book_categories (book_id, category_id) VALUES
(1, 1),
(2, 2),
(3, 2),
(4, 8),
(5, 3),
(6, 9),
(7, 6),
(8, 5),
(9, 9),
(10, 8);

INSERT INTO audiobooks (book_id, total_duration) VALUES
(1, 300),
(2, 320),
(3, 310),
(4, 305),
(5, 330),
(6, 340),
(7, 350),
(8, 360),
(9, 370),
(10, 380);

INSERT INTO audio_chapters (audiobook_id, title, audio_file_name, duration) VALUES
(1, N'Chương 1', N'url1', 30),
(2, N'Chương 1', N'url2', 30),
(3, N'Chương 1', N'url3', 30),
(4, N'Chương 1', N'url4', 30),
(5, N'Chương 1', N'url5', 30),
(6, N'Chương 1', N'url6', 30),
(7, N'Chương 1', N'url7', 30),
(8, N'Chương 1', N'url8', 30),
(9, N'Chương 1', N'url9', 30),
(10, N'Chương 1', N'url10', 30);

INSERT INTO narrators (name) VALUES
(N'Người đọc 1'),
(N'Người đọc 2'),
(N'Người đọc 3'),
(N'Người đọc 4'),
(N'Người đọc 5'),
(N'Người đọc 6'),
(N'Người đọc 7'),
(N'Người đọc 8'),
(N'Người đọc 9'),
(N'Người đọc 10');

INSERT INTO audiobook_narrators (audiobook_id, narrator_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10);

INSERT INTO audio_progress (user_id, audiobook_id, progress) VALUES
(1, 1, 10),
(2, 2, 20),
(3, 3, 30),
(4, 4, 40),
(5, 5, 50),
(6, 6, 60),
(7, 7, 70),
(8, 8, 80),
(9, 9, 90),
(10, 10, 100);

INSERT INTO order_status (name) VALUES
('PENDING'),
('CONFIRMED'),
('SHIPPING'),
('DELIVERED'),
('CANCELLED'),
('REFUNDED'),
('FAILED'),
('RETURNED'),
('PROCESSING'),
('COMPLETED');

INSERT INTO orders (user_id, address_id, status_id, total_amount) VALUES
(1, 1, 1, 100000),
(2, 2, 2, 120000),
(3, 3, 3, 130000),
(4, 4, 4, 140000),
(5, 5, 5, 150000),
(6, 6, 6, 160000),
(7, 7, 7, 170000),
(8, 8, 8, 180000),
(9, 9, 9, 190000),
(10, 10, 10, 200000);

INSERT INTO order_items (order_id, book_id, audiobook_id, quantity, price) VALUES
(1, 1, NULL, 1, 100000),
(2, 2, NULL, 1, 120000),
(3, 3, NULL, 1, 130000),
(4, 4, NULL, 1, 140000),
(5, 5, NULL, 1, 150000),
(6, 6, NULL, 1, 160000),
(7, 7, NULL, 1, 170000),
(8, 8, NULL, 1, 180000),
(9, 9, NULL, 1, 190000),
(10, 10, NULL, 1, 200000);

INSERT INTO order_status_history (order_id, status) VALUES
(1, 'PENDING'),
(2, 'CONFIRMED'),
(3, 'SHIPPING'),
(4, 'DELIVERED'),
(5, 'CANCELLED'),
(6, 'REFUNDED'),
(7, 'FAILED'),
(8, 'RETURNED'),
(9, 'PROCESSING'),
(10, 'COMPLETED');

INSERT INTO payment_methods (name, description) VALUES
('COD', N'Thanh toán khi nhận hàng'),
('BANK', N'Chuyển khoản ngân hàng'),
('MOMO', N'Ví MoMo'),
('ZALO', N'ZaloPay'),
('VNPAY', N'VNPay'),
('PAYPAL', N'PayPal'),
('CARD', N'Thẻ ngân hàng'),
('APPLEPAY', N'Apple Pay'),
('GOOGLEPAY', N'Google Pay'),
('CRYPTO', N'Tiền mã hóa');

INSERT INTO payments (order_id, payment_method_id, amount, status) VALUES
(1, 1, 100000, 'PAID'),
(2, 2, 120000, 'PAID'),
(3, 3, 130000, 'PAID'),
(4, 4, 140000, 'PAID'),
(5, 5, 150000, 'PAID'),
(6, 6, 160000, 'PAID'),
(7, 7, 170000, 'PAID'),
(8, 8, 180000, 'PAID'),
(9, 9, 190000, 'PAID'),
(10, 10, 200000, 'PAID');

INSERT INTO carts (user_id) VALUES
(1),
(2),
(3),
(4),
(5),
(6),
(7),
(8),
(9),
(10);

INSERT INTO cart_items (cart_id, book_id, quantity) VALUES
(1, 1, 1),
(2, 2, 1),
(3, 3, 1),
(4, 4, 1),
(5, 5, 1),
(6, 6, 1),
(7, 7, 1),
(8, 8, 1),
(9, 9, 1),
(10, 10, 1);

INSERT INTO reviews (user_id, book_id, rating, comment) VALUES
(1, 1, 5, N'Good'),
(2, 2, 4, N'Nice'),
(3, 3, 3, N'OK'),
(4, 4, 5, N'Great'),
(5, 5, 4, N'Nice'),
(6, 6, 5, N'Good'),
(7, 7, 3, N'OK'),
(8, 8, 4, N'Nice'),
(9, 9, 5, N'Good'),
(10, 10, 4, N'Nice');

INSERT INTO wishlists (user_id) VALUES
(1),
(2),
(3),
(4),
(5),
(6),
(7),
(8),
(9),
(10);

INSERT INTO wishlist_items (wishlist_id, book_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10);

INSERT INTO coupons (code, discount_type, discount_value, max_discount, expiry_date) VALUES
('C1', 'PERCENT', 10, 50000, DATEADD(DAY, 30, SYSDATETIME())),
('C2', 'PERCENT', 10, 50000, DATEADD(DAY, 30, SYSDATETIME())),
('C3', 'PERCENT', 10, 50000, DATEADD(DAY, 30, SYSDATETIME())),
('C4', 'PERCENT', 10, 50000, DATEADD(DAY, 30, SYSDATETIME())),
('C5', 'PERCENT', 10, 50000, DATEADD(DAY, 30, SYSDATETIME())),
('C6', 'PERCENT', 10, 50000, DATEADD(DAY, 30, SYSDATETIME())),
('C7', 'PERCENT', 10, 50000, DATEADD(DAY, 30, SYSDATETIME())),
('C8', 'PERCENT', 10, 50000, DATEADD(DAY, 30, SYSDATETIME())),
('C9', 'PERCENT', 10, 50000, DATEADD(DAY, 30, SYSDATETIME())),
('C10', 'PERCENT', 10, 50000, DATEADD(DAY, 30, SYSDATETIME()));

INSERT INTO user_coupons (user_id, coupon_id, used) VALUES
(1, 1, 1),
(2, 2, 0),
(3, 3, 0),
(4, 4, 1),
(5, 5, 0),
(6, 6, 1),
(7, 7, 0),
(8, 8, 0),
(9, 9, 1),
(10, 10, 0);

INSERT INTO inventory_logs (book_id, change_amount, reason) VALUES
(1, 10, 'IMPORT'),
(2, 10, 'IMPORT'),
(3, 10, 'IMPORT'),
(4, 10, 'IMPORT'),
(5, 10, 'IMPORT'),
(6, 10, 'IMPORT'),
(7, 10, 'IMPORT'),
(8, 10, 'IMPORT'),
(9, 10, 'IMPORT'),
(10, 10, 'IMPORT');










-- ================================= Truy vấn thử ===================================
-- SELECT Truy vấn dữ liệu
-- =========================
-- TEST QUERY
-- =========================
SELECT * FROM books;

SELECT * FROM books
WHERE price > 100000;

SELECT b.title, p.name AS publisher_name
FROM books b
JOIN publishers p ON p.id = b.publisher_id;

SELECT p.name, COUNT(b.id) AS total_books
FROM books b
JOIN publishers p ON p.id = b.publisher_id
GROUP BY p.name;

SELECT u.full_name, SUM(o.total_amount) AS total_spent
FROM users u
JOIN orders o ON u.id = o.user_id
GROUP BY u.full_name
ORDER BY total_spent DESC;

-- AVG(): Tính trung bình cộng.








-- ================================ Không sử dụng =================================


-- Dữ liệu mẫu 1
-- 1. Thêm Phân quyền
-- CẬP NHẬT SAU NÀY || Trong Spring Security, người ta thường có quy ước đặt tên quyền bắt đầu bằng chữ ROLE_ (ví dụ: ROLE_USER, ROLE_ADMIN). Mặc dù lưu là USER vẫn hoạt động, nhưng dùng ROLE_USER sẽ giúp cấu hình ở Bước 3 dễ thở hơn rất nhiều.
INSERT INTO roles (name) VALUES 
('ADMIN'), 
('CUSTOMER');

-- 2. Thêm Người dùng (Lưu ý: Trong thực tế Spring Boot, mật khẩu sẽ được băm (hash), ở đây ta để tạm chữ thường)
INSERT INTO users (email, password, full_name, phone) VALUES
('admin@bookarea.com', '123456', 'Nguyễn Quản Trị', '0901234567'),
('khachhang@gmail.com', '123456', 'Trần Khách Hàng', '0987654321');

-- 3. Cấp quyền cho người dùng (user_id = 1 làm ADMIN, user_id = 2 làm CUSTOMER)
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1), 
(2, 2);

-- 4. Thêm Nhà xuất bản
INSERT INTO publishers (name, description) VALUES
('NXB Trẻ', 'Nhà xuất bản dành cho giới trẻ'),
('Nhã Nam', 'Bởi vì sách là thế giới');

-- 5. Thêm Tác giả & Danh mục
INSERT INTO authors (name) VALUES 
('J.K. Rowling'), 
('Nguyễn Nhật Ánh');

INSERT INTO categories (name) VALUES 
('Tiểu thuyết viễn tưởng'), 
('Truyện dài');

-- 6. Thêm Sách (Lưu ý: Cột ID sẽ tự động tăng là 1, 2, 3)
INSERT INTO books (title, description, price, stock, publisher_id) VALUES
('Harry Potter và Hòn đá Phù thủy', 'Tập 1 của series', 150000.00, 50, 1),
('Mắt Biếc', 'Truyện tình buồn của Ngạn và Hà Lan', 95000.00, 100, 1),
('Cây Chuối Non Đi Giày Xanh', 'Ký ức tuổi thơ', 110000.00, 30, 2);

-- 7. Liên kết Sách với Tác giả và Danh mục (Bảng trung gian N-N)
INSERT INTO book_authors (book_id, author_id) VALUES 
(1, 1), -- Harry Potter (1) - J.K. Rowling (1)
(2, 2), -- Mắt Biếc (2) - Nguyễn Nhật Ánh (2)
(3, 2); -- Cây Chuối Non (3) - Nguyễn Nhật Ánh (2)

INSERT INTO book_categories (book_id, category_id) VALUES 
(1, 1), 
(2, 2), 
(3, 2);

-- 8. Thêm Trạng thái đơn hàng
INSERT INTO order_status (name) VALUES 
('PENDING'), 
('SHIPPING'), 
('COMPLETED'), 
('CANCELLED');
GO

