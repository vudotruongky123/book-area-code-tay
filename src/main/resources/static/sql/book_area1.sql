CREATE DATABASE book_area1;

DROP DATABASE IF EXISTS book_area1;

USE book_area1;

-- 🧱 Giai đoạn 1: Nền tảng (Các bảng độc lập). Học cách tạo database và các bảng không phụ thuộc vào bảng khác (như users, roles, publishers), làm quen với khóa chính (PRIMARY KEY) và kiểu dữ liệu.

-- 🔗 Giai đoạn 2: Xây dựng cấu trúc (Khóa ngoại và Mối quan hệ). Tìm hiểu FOREIGN KEY để tạo quan hệ 1-Nhiều và Nhiều-Nhiều (ví dụ: Liên kết users và roles qua user_roles).

-- ⚙️ Giai đoạn 3: Ràng buộc nâng cao và Tối ưu (Logic). Áp dụng CHECK, UNIQUE, và INDEX để đảm bảo tính toàn vẹn dữ liệu và tăng tốc độ truy vấn.

-- Quyền users
CREATE TABLE roles(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Thông tin users
CREATE TABLE users(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(20) UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- Nhà xuất bản
CREATE TABLE publishers(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tác giả
CREATE TABLE authors(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

-- Địa chỉ user
CREATE TABLE addresses(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_line TEXT,
    city VARCHAR(100),
    country VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Bảng liên kết giữa (users & roles) n-n
CREATE TABLE user_roles(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Sách
CREATE TABLE books(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT,
    publisher_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (publisher_id) REFERENCES publishers(id)
);

-- Thêm trường dữ liệu để có thể lưu file và đường dẫn sách
ALTER TABLE books
ADD COLUMN file_name VARCHAR(255) UNIQUE AFTER title,
ADD COLUMN page_number INT AFTER file_name;

ALTER TABLE books
DROP COLUMN file_name,
DROP COLUMN page_number;

-- Ảnh sách
CREATE TABLE book_images(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    image_url TEXT,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Bảng liên kết giữa (books & authors) n-n
CREATE TABLE book_authors(
    book_id BIGINT,
    author_id BIGINT,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(id)
);

-- Danh mục
CREATE TABLE categories(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

-- Bảng liên kết giữa (categories & books) n-n
CREATE TABLE book_categories(
    book_id BIGINT,
    category_id BIGINT,
    PRIMARY KEY (book_id, category_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Sách nói quan hệ 1-1 với books
CREATE TABLE audiobooks(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT UNIQUE,  -- Một sách chỉ có duy nhất một phiên bản sách nói
    total_duration INT,     -- Thời lượng sách nói
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Chương của sách nói (audiobooks)
CREATE TABLE audio_chapters(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    audiobook_id BIGINT NOT NULL,
    title VARCHAR(255),
    audio_url TEXT,
    duration INT,
    FOREIGN KEY (audiobook_id) REFERENCES audiobooks(id) ON DELETE CASCADE
);

-- Người đọc
CREATE TABLE narrators(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

-- Bảng liên kết giữa (narrators & audiobooks) n-n
CREATE TABLE audiobook_narrators(
    audiobook_id BIGINT,
    narrator_id BIGINT,
    PRIMARY KEY (audiobook_id, narrator_id),
    FOREIGN KEY (audiobook_id) REFERENCES audiobooks(id) ON DELETE CASCADE,
    FOREIGN KEY (narrator_id) REFERENCES narrators(id)
);

-- Tiến độ nghe liên kết giữa (users & audiobooks) n-n
CREATE TABLE audio_progress(
    user_id BIGINT,
    audiobook_id BIGINT,
    progress INT,
    PRIMARY KEY (user_id, audiobook_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (audiobook_id) REFERENCES audiobooks(id)
);

-- Trạng thái đơn hàng (Chờ xử lý, Đang giao, Đã hủy, v.v..)
CREATE TABLE order_status(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Đơn hàng
CREATE TABLE orders(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_id  BIGINT NOT NULL,
    status_id  BIGINT,
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id),
    FOREIGN KEY (status_id) REFERENCES order_status(id)
);

-- Chi tiết đơn hàng
CREATE TABLE order_items(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    book_id BIGINT,
    audiobook_id BIGINT,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (audiobook_id) REFERENCES audiobooks(id),
    CHECK ((book_id IS NOT NULL AND audiobook_id IS NULL) OR (book_id IS NULL AND audiobook_id IS NOT NULL))
);

-- Lịch sử trạng thái đơn hàng
CREATE TABLE order_status_history(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(50),
    changed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Phương thức thanh toán
CREATE TABLE payment_methods(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE,
    description TEXT
);

-- Thanh toán
CREATE TABLE payments(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    payment_method_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50),
    paid_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id)
);

-- Giỏ hàng
CREATE TABLE carts(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Chi tiết giỏ hàng
CREATE TABLE cart_items(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Đánh giá
CREATE TABLE reviews(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    rating INT,
    comment TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Đây là ("Composite Unique" - Ràng buộc duy nhất kép) => Kiểm tra tính độc nhất của (SỰ KẾT HỢP GIỮA CÁC CỘT) được xét tính "UNIQUE"
    UNIQUE (user_id, book_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CHECK (rating BETWEEN 1 AND 5)
);

-- Danh sách yêu thích
CREATE TABLE wishlists(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Chi tiết danh sách yêu thích
CREATE TABLE wishlist_items(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wishlist_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    FOREIGN KEY (wishlist_id) REFERENCES wishlists(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Mã giảm giá
CREATE TABLE coupons(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    discount_type VARCHAR(20),      -- Kiểu giảm giá (giảm theo % / giảm số tiền cố định)
    discount_value DECIMAL(10, 2),  -- Số tiền giảm giá được tính
    max_discount DECIMAL(10, 2),    -- Số tiền được giảm giá tối đa
    expiry_date DATETIME
);

-- Người dùng lưu mã giảm giá liên kết giữa (users & coupons) n-n
CREATE TABLE user_coupons(
    user_id BIGINT,
    coupon_id BIGINT,
    used BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, coupon_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (coupon_id) REFERENCES coupons(id) ON DELETE CASCADE
);

-- Lịch sử biến động của hàng tồn kho
CREATE TABLE inventory_logs(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    change_amount INT,
    reason VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id)
);


-- Tạo bảng lưu Refresh Token (Có tác dụng lưu một chuỗi được tạo ra khi người dùng đăng nhập thành công, và có thời hạn sử dụng lâu hơn Access Token, thường là 7 ngày. Khi Access Token hết hạn, client có thể gửi Refresh Token này lên server để yêu cầu cấp mới Access Token mà không cần phải đăng nhập lại.)
CREATE TABLE refresh_tokens(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expiry_date DATETIME,
    user_id BIGINT NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);














-- Tạo INDEX để tối ưu hóa truy vấn
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_books_publisher_id ON books(publisher_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_reviews_book_id ON reviews(book_id);
























-- ============================ Đang sử dung ==================================
-- Dữ liẹu mẫu 2
INSERT INTO users (email, password, full_name, phone) VALUES
('user1@gmail.com','123','User 1','0900000001'),
('user2@gmail.com','123','User 2','0900000002'),
('user3@gmail.com','123','User 3','0900000003'),
('user4@gmail.com','123','User 4','0900000004'),
('user5@gmail.com','123','User 5','0900000005'),
('user6@gmail.com','123','User 6','0900000006'),
('user7@gmail.com','123','User 7','0900000007'),
('user8@gmail.com','123','User 8','0900000008'),
('user9@gmail.com','123','User 9','0900000009'),
('user10@gmail.com','123','User 10','0900000010');

INSERT INTO roles (name) VALUES
('ADMIN'),('USER'),('MANAGER'),('STAFF'),
('EDITOR'),('CUSTOMER'),('GUEST'),
('VIP'),('MODERATOR'),('SUPPORT');

INSERT INTO user_roles VALUES
(1,1),(2,2),(3,2),(4,2),(5,2),
(6,3),(7,2),(8,2),(9,2),(10,2);

INSERT INTO addresses (user_id, address_line, city, country) VALUES
(1,'Address 1','HCM','VN'),
(2,'Address 2','HCM','VN'),
(3,'Address 3','HN','VN'),
(4,'Address 4','DN','VN'),
(5,'Address 5','CT','VN'),
(6,'Address 6','HP','VN'),
(7,'Address 7','HCM','VN'),
(8,'Address 8','HN','VN'),
(9,'Address 9','DN','VN'),
(10,'Address 10','CT','VN');





INSERT INTO publishers (name, description) VALUES
('NXB 1','Desc'),('NXB 2','Desc'),('NXB 3','Desc'),
('NXB 4','Desc'),('NXB 5','Desc'),
('NXB 6','Desc'),('NXB 7','Desc'),
('NXB 8','Desc'),('NXB 9','Desc'),
('NXB 10','Desc');

INSERT INTO authors (name) VALUES
('Author 1'),('Author 2'),('Author 3'),('Author 4'),('Author 5'),
('Author 6'),('Author 7'),('Author 8'),('Author 9'),('Author 10');

INSERT INTO categories (name) VALUES
('Cat1'),('Cat2'),('Cat3'),('Cat4'),('Cat5'),
('Cat6'),('Cat7'),('Cat8'),('Cat9'),('Cat10');




INSERT INTO books (title, description, price, stock, publisher_id) VALUES
('Book 1','Desc',100,10,1),
('Book 2','Desc',120,10,2),
('Book 3','Desc',130,10,3),
('Book 4','Desc',140,10,4),
('Book 5','Desc',150,10,5),
('Book 6','Desc',160,10,6),
('Book 7','Desc',170,10,7),
('Book 8','Desc',180,10,8),
('Book 9','Desc',190,10,9),
('Book 10','Desc',200,10,10);

INSERT INTO book_images (book_id, image_url) VALUES
(1,'img1'),(2,'img2'),(3,'img3'),(4,'img4'),(5,'img5'),
(6,'img6'),(7,'img7'),(8,'img8'),(9,'img9'),(10,'img10');

INSERT INTO book_authors VALUES
(1,1),(2,2),(3,3),(4,4),(5,5),
(6,6),(7,7),(8,8),(9,9),(10,10);

INSERT INTO book_categories VALUES
(1,1),(2,2),(3,3),(4,4),(5,5),
(6,6),(7,7),(8,8),(9,9),(10,10);






INSERT INTO audiobooks (book_id, total_duration) VALUES
(1,300),(2,320),(3,310),(4,305),(5,330),
(6,340),(7,350),(8,360),(9,370),(10,380);

INSERT INTO audio_chapters (audiobook_id, title, audio_url, duration) VALUES
(1,'C1','url',30),(2,'C2','url',30),(3,'C3','url',30),
(4,'C4','url',30),(5,'C5','url',30),
(6,'C6','url',30),(7,'C7','url',30),
(8,'C8','url',30),(9,'C9','url',30),
(10,'C10','url',30);

INSERT INTO narrators (name) VALUES
('Nar 1'),('Nar 2'),('Nar 3'),('Nar 4'),('Nar 5'),
('Nar 6'),('Nar 7'),('Nar 8'),('Nar 9'),('Nar 10');

INSERT INTO audiobook_narrators VALUES
(1,1),(2,2),(3,3),(4,4),(5,5),
(6,6),(7,7),(8,8),(9,9),(10,10);

INSERT INTO audio_progress VALUES
(1,1,10),(2,2,20),(3,3,30),(4,4,40),(5,5,50),
(6,6,60),(7,7,70),(8,8,80),(9,9,90),(10,10,100);






INSERT INTO order_status (name) VALUES
('PENDING'),('CONFIRMED'),('SHIPPING'),
('DELIVERED'),('CANCELLED'),
('REFUNDED'),('FAILED'),
('RETURNED'),('PROCESSING'),('COMPLETED');

INSERT INTO orders (user_id, address_id, status_id, total_amount) VALUES
(1,1,1,100),(2,2,2,120),(3,3,3,130),
(4,4,4,140),(5,5,5,150),
(6,6,6,160),(7,7,7,170),
(8,8,8,180),(9,9,9,190),(10,10,10,200);


INSERT INTO order_items (order_id, book_id, quantity, price) VALUES
(1,1,1,100),(2,2,1,120),(3,3,1,130),
(4,4,1,140),(5,5,1,150),
(6,6,1,160),(7,7,1,170),
(8,8,1,180),(9,9,1,190),(10,10,1,200);

INSERT INTO order_status_history (order_id, status) VALUES
(1,'PENDING'),(2,'CONFIRMED'),(3,'SHIPPING'),
(4,'DELIVERED'),(5,'CANCELLED'),
(6,'REFUNDED'),(7,'FAILED'),
(8,'RETURNED'),(9,'PROCESSING'),(10,'COMPLETED');


INSERT INTO payment_methods (name) VALUES
('COD'),('BANK'),('MOMO'),('ZALO'),
('VNPAY'),('PAYPAL'),('CARD'),
('APPLEPAY'),('GOOGLEPAY'),('CRYPTO');

INSERT INTO payments (order_id, payment_method_id, amount, status) VALUES
(1,1,100,'PAID'),(2,2,120,'PAID'),
(3,3,130,'PAID'),(4,4,140,'PAID'),
(5,5,150,'PAID'),(6,6,160,'PAID'),
(7,7,170,'PAID'),(8,8,180,'PAID'),
(9,9,190,'PAID'),(10,10,200,'PAID');




INSERT INTO carts (user_id) VALUES
(1),(2),(3),(4),(5),(6),(7),(8),(9),(10);

INSERT INTO cart_items (cart_id, book_id, quantity) VALUES
(1,1,1),(2,2,1),(3,3,1),(4,4,1),(5,5,1),
(6,6,1),(7,7,1),(8,8,1),(9,9,1),(10,10,1);




INSERT INTO reviews (user_id, book_id, rating, comment) VALUES
(1,1,5,'Good'),(2,2,4,'Nice'),(3,3,3,'OK'),
(4,4,5,'Great'),(5,5,4,'Nice'),
(6,6,5,'Good'),(7,7,3,'OK'),
(8,8,4,'Nice'),(9,9,5,'Good'),(10,10,4,'Nice');




INSERT INTO wishlists (user_id) VALUES
(1),(2),(3),(4),(5),(6),(7),(8),(9),(10);

INSERT INTO wishlist_items (wishlist_id, book_id) VALUES
(1,1),(2,2),(3,3),(4,4),(5,5),
(6,6),(7,7),(8,8),(9,9),(10,10);




INSERT INTO coupons (code, discount_type, discount_value) VALUES
('C1','PERCENT',10),('C2','PERCENT',10),
('C3','PERCENT',10),('C4','PERCENT',10),
('C5','PERCENT',10),('C6','PERCENT',10),
('C7','PERCENT',10),('C8','PERCENT',10),
('C9','PERCENT',10),('C10','PERCENT',10);

INSERT INTO user_coupons VALUES
(1,1,TRUE),(2,2,FALSE),(3,3,FALSE),(4,4,TRUE),(5,5,FALSE),
(6,6,TRUE),(7,7,FALSE),(8,8,FALSE),(9,9,TRUE),(10,10,FALSE);



INSERT INTO inventory_logs (book_id, change_amount, reason) VALUES
(1,10,'IMPORT'),(2,10,'IMPORT'),(3,10,'IMPORT'),
(4,10,'IMPORT'),(5,10,'IMPORT'),
(6,10,'IMPORT'),(7,10,'IMPORT'),
(8,10,'IMPORT'),(9,10,'IMPORT'),(10,10,'IMPORT');










-- ================================= Truy vấn thử ===================================
-- SELECT Truy vấn dữ liệu
SELECT * FROM books;
SELECT * FROM books b WHERE b.price > 100;

-- JOIN Lệnh để nối bảng
SELECT b.title, p.name
FROM books b
JOIN publishers p ON p.id = b.publisher_id;

-- Các hàm tính toán & thống kê dữ liệu

-- COUNT(): Đếm số lượng.
SELECT p.name, COUNT(b.id) AS total_books
FROM books b
JOIN publishers p ON p.id = b.publisher_id
GROUP BY p.name;

-- SUM(): Tính tổng.
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
