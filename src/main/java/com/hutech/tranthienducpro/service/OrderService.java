package com.hutech.tranthienducpro.service;

import com.hutech.tranthienducpro.model.CartItem;
import com.hutech.tranthienducpro.model.Order;
import com.hutech.tranthienducpro.model.OrderDetail;
import com.hutech.tranthienducpro.Repository.OrderDetailRepository;
import com.hutech.tranthienducpro.Repository.OrderRepository;
import com.hutech.tranthienducpro.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @Transactional
    public Order createOrder(String customerName, String shippingAddress, String phoneNumber, String email, String note, String paymentMethod, List<CartItem> cartItems, BigDecimal totalAmount, Boolean paymentStatus, String transactionId, User user) {

        // Tạo đối tượng Order và lưu vào cơ sở dữ liệu
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setOrderDate(LocalDateTime.now());
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNote(note);
        order.setUser(user);
        order.setPaymentMethod(paymentMethod);
        order.setTotalAmount(totalAmount);
        order.setPaymentStatus(paymentStatus); // Cập nhật trạng thái thanh toán
        order.setTransactionId(transactionId); // Cập nhật transactionId
        order = orderRepository.save(order);

        // Lưu các chi tiết đơn hàng vào cơ sở dữ liệu và trừ số lượng sản phẩm trong kho
        for (CartItem item : cartItems) {
            // Lưu chi tiết đơn hàng
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);

            // Trừ số lượng sản phẩm trong kho
            productService.updateProductStock(item.getProduct().getId(), item.getQuantity());
        }
        // Xóa giỏ hàng sau khi đặt hàng thành công
        cartService.clearCart();

        return order;
    }

    @Transactional
    public List<Order> getOrderByUser(Long userId) {
        List<Order> orders = orderRepository.findOrderByUserId(userId);
        return orders.isEmpty() ? null : orders;
    }
}
