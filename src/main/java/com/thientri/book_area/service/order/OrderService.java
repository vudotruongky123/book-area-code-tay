package com.thientri.book_area.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.thientri.book_area.dto.request.order.OrderRequest;
import com.thientri.book_area.dto.request.payment.WebhookRequest;
import com.thientri.book_area.dto.response.order.OrderResponse;
import com.thientri.book_area.exception.BadRequestException;
import com.thientri.book_area.exception.ResourceNotFoundException;
import com.thientri.book_area.model.order.CartItem;
import com.thientri.book_area.model.order.Order;
import com.thientri.book_area.model.order.OrderItem;
import com.thientri.book_area.model.order.OrderStatus;
import com.thientri.book_area.model.order.OrderStatusHistory;
import com.thientri.book_area.model.payment.Payment;
import com.thientri.book_area.model.payment.PaymentMethod;
import com.thientri.book_area.model.user.Address;
import com.thientri.book_area.model.user.User;
import com.thientri.book_area.repository.catalog.BookRepository;
import com.thientri.book_area.repository.order.OrderRepository;
import com.thientri.book_area.repository.order.OrderStatusHistoryRepository;
import com.thientri.book_area.repository.order.OrderStatusRepository;
import com.thientri.book_area.repository.payment.PaymentMethodRepository;
import com.thientri.book_area.repository.payment.PaymentRepository;
import com.thientri.book_area.repository.user.AddressRepository;
import com.thientri.book_area.repository.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

        private final UserRepository userRepository;
        private final AddressRepository addressRepository;
        private final OrderStatusRepository orderStatusRepository;
        private final OrderStatusHistoryRepository orderStatusHistoryRepository;
        private final PaymentRepository paymentRepository;
        private final PaymentMethodRepository paymentMethodRepository;
        private final OrderRepository orderRepository;
        private final BookRepository bookRepository;

        // Phương thức đặt hàng
        @Transactional
        public OrderResponse placeOrder(String email, OrderRequest request) {
                // 1. Kiểm tra User và Giỏ hàng
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng!"));

                if (request.getSelectedCartItemIds() == null || request.getSelectedCartItemIds().isEmpty()) {
                        throw new BadRequestException("Bạn chưa chọn sản phẩm nào để đặt hàng!");
                }

                // 2. Kiểm tra Địa chỉ & Phương thức thanh toán
                Address address = addressRepository.findById(request.getAddressId())
                                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay dia chi giao hàng!"));

                PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Khong tim thay phuong thuc thanh toan!"));

                // 3. Khởi tạo đơn hàng
                // Giả sử Status ID = 1 là "PENDING" (Chờ xác nhận / Chờ thanh toán)
                OrderStatus initialStatus = orderStatusRepository.findById(1L)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Thiếu dữ liệu trạng thái đơn hàng (ID=1) trong DB!"));

                String uniqueOrderCode = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                Order order = Order.builder()
                                .orderCode(uniqueOrderCode)
                                .user(user)
                                .address(address)
                                .orderStatus(initialStatus)
                                .totalAmount(BigDecimal.ZERO) // Tạm thời để 0, Sẽ tính lại ngay bên dưới
                                .build();

                // 4. Lấy đồ từ Cart chuyển sang OrderItem, trừ Tồn kho và Tính tiền
                BigDecimal totalAmount = BigDecimal.ZERO;

                // Chỉ lấy những CartItem được chọn trong OrderRequest
                List<CartItem> selectedItems = user.getCart().getCartItems().stream()
                                .filter(item -> request.getSelectedCartItemIds().contains(item.getId())).toList();

                if (selectedItems.isEmpty()) {
                        throw new BadRequestException("Không tìm thấy sản phẩm nào trong giỏ hàng của bạn!");
                }

                for (CartItem cartItem : selectedItems) {
                        var book = cartItem.getBook();

                        if (book.getStock() < cartItem.getQuantity()) {
                                throw new BadRequestException(
                                                "Sách " + book.getTitle() + " chỉ còn " + book.getStock()
                                                                + " sản phẩm trong kho!");
                        }

                        // Trừ tồn kho thực tế (Giữ hàng khi khách đang ở bước đặt hàng, tránh tình
                        // trạng oversell)
                        book.setStock(book.getStock() - cartItem.getQuantity());
                        bookRepository.save(book);

                        // Chốt giá và tạo ỎderItem
                        OrderItem orderItem = OrderItem.builder()
                                        .order(order)
                                        .book(book)
                                        .quantity(cartItem.getQuantity())
                                        .price(book.getPrice()) // Lấy giá hiện tại của sách làm giá bán
                                        .build();

                        order.getOrderItems().add(orderItem);

                        // Cộng dồn tổng tiền
                        BigDecimal itemTotal = book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                        totalAmount = totalAmount.add(itemTotal);
                }

                // 5. Cập nhật tổng tiền cho đơn hàng
                order.setTotalAmount(totalAmount);

                // 6. Lưu đơn hàng (các OrderItem sẽ tự động được lưu nhờ Cascade)
                Order savedOrder = orderRepository.save(order);

                // Khởi tạo thông tin thanh toán lưu xuống DB
                Payment payment = Payment.builder()
                                .order(savedOrder)
                                .paymentMethod(paymentMethod)
                                .amount(savedOrder.getTotalAmount())
                                .status("PENDING") // Trạng thái ban đầu luôn là chờ thanh toán
                                .build();
                paymentRepository.save(payment);

                // 7. Xóa giỏ hàng của người dùng (Chỉ xóa những CartItem đã được chọn để đặt
                // hàng)
                user.getCart().getCartItems().removeIf(item -> request.getSelectedCartItemIds().contains(item.getId()));

                // 8. Sinh link QR code nếu chọn chuyển khoản trước
                String qrCodeUrl = null;
                if (paymentMethod.getName().equalsIgnoreCase("BANK_TRANSFER") || request.getPaymentMethodId() == 2L) {
                        qrCodeUrl = generateVietQRLink(savedOrder.getTotalAmount(), savedOrder.getOrderCode());
                }

                OrderStatusHistory history = OrderStatusHistory.builder()
                                .order(savedOrder)
                                .status(initialStatus.getName())
                                .build();
                orderStatusHistoryRepository.save(history);

                return OrderResponse.builder()
                                .orderId(savedOrder.getId())
                                .orderCode(savedOrder.getOrderCode())
                                .totalAmount(savedOrder.getTotalAmount())
                                .orderStatus(savedOrder.getOrderStatus().getName())
                                .paymentMethod(paymentMethod.getName())
                                .qrCodeUrl(qrCodeUrl)
                                .build();
        }

        // Phương thức hỗ trợ sinh link VietQR cho chuyển khoản ngân hàng
        private String generateVietQRLink(BigDecimal amount, String orderCode) {
                String bankId = "VCB"; // Mã ngân hàng (Vietctinbank, VCB, MB, ACB,...)
                String accountNo = "44384039741";
                String accountName = "Công ty TNHH Book Area";

                return String.format(
                                "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%s&addInfo=%s&accountName=%s",
                                bankId, accountNo, amount.toBigInteger().toString(), orderCode,
                                accountName.replace(" ", "%20"));
        }

        // Hàm xử lý Webhook từ ngân hàng sau khi khách đã thanh toán chuyển khoản
        @Transactional
        public void handlePaymentWebhook(WebhookRequest request) {
                String content = request.getTransactionContent().toUpperCase().trim();

                // 1. Tối ưu: Dùng Regex bóc tách chính xác Mã đơn hàng (Format: ORD- kèm 8 ký
                // tự)
                // // Giả sử mã đơn hàng luôn bắt đầu bằng "ORD-"
                Matcher matcher = Pattern.compile("ORD-[A-Z0-9]{8}").matcher(content);
                if (!matcher.find()) {
                        throw new BadRequestException(
                                        "Không tìm thấy mã đơn hàng hợp lệ trong nội dung giao dịch: " + content);
                }

                String extractedOrderCode = matcher.group();

                Order order = orderRepository.findByOrderCode(extractedOrderCode)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy đơn hàng với mã: " + extractedOrderCode));

                // 2.Kiểm tra trạng thái, nếu đã thanh toán thì bỏ qua
                if (order.getOrderStatus().getName().equalsIgnoreCase("COMPLETED")) {
                        return;
                }

                // 3. Kiểm tra số tiền có khớp không
                if (request.getAmountIn().compareTo(order.getTotalAmount()) >= 0) {

                        // Lấy trạng thái COMPLETED từ DB (Giả sử ID=10 là COMPLETED)
                        OrderStatus completedStatus = orderStatusRepository.findById(10L)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Thiếu dữ liệu trạng thái đơn hàng (ID=10) trong DB!"));

                        // Cập nhật đơn hàng
                        order.setOrderStatus(completedStatus);
                        orderRepository.save(order);

                        // Ghi nhận lịch sử trạng thái: Đã thanh toán thành công
                        OrderStatusHistory history = OrderStatusHistory.builder()
                                        .order(order)
                                        .status(completedStatus.getName())
                                        .build();
                        orderStatusHistoryRepository.save(history);

                        // Cập nhật lịch sử thanh toán kèm thời gian thực tế
                        Payment payment = paymentRepository.findByOrderId(order.getId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Không tìm thấy thông tin thanh toán cho đơn hàng này!"));

                        payment.setStatus("COMPLETED");
                        payment.setPaidAt(LocalDateTime.now()); // BỔ SUNG: Cập nhật giờ thanh toán thực tế
                        paymentRepository.save(payment);

                } else {
                        throw new BadRequestException(
                                        "Số tiền chuyển khoản không đủ cho đơn hàng: " + order.getOrderCode());
                }
        }
}
