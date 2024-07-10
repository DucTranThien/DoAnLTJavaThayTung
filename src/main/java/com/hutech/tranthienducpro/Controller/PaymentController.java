package com.hutech.tranthienducpro.Controller;

import com.hutech.tranthienducpro.model.CartItem;
import com.hutech.tranthienducpro.model.Order;
import com.hutech.tranthienducpro.model.User;
import com.hutech.tranthienducpro.service.CartService;
import com.hutech.tranthienducpro.service.OrderService;
import com.hutech.tranthienducpro.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final CartService cartService;
    private final VNPayService vnpayService;
    private final OrderService orderService;

    @Autowired
    public PaymentController(CartService cartService, VNPayService vnpayService, OrderService orderService) {
        this.cartService = cartService;
        this.vnpayService = vnpayService;
        this.orderService = orderService;
    }

    @PostMapping("/process")
    public String processPayment(@RequestParam("paymentMethod") String paymentMethod,
                                 @RequestParam("customerName") String customerName,
                                 @RequestParam("shippingAddress") String shippingAddress,
                                 @RequestParam("phoneNumber") String phoneNumber,
                                 @RequestParam("email") String email,
                                 @RequestParam("note") String note,
                                 Model model, HttpServletRequest request, Authentication authentication) {
        if (paymentMethod.equalsIgnoreCase("vnpay")) {
            BigDecimal totalAmount = cartService.getCartItems().stream()
                    .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            try {
                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                String paymentUrl = vnpayService.createOrder(totalAmount.intValue(), "Thanh toan mua dien thoai dubai 4.0 ", baseUrl);
                // Lưu thông tin khách hàng vào session
                request.getSession().setAttribute("customerName", customerName);
                request.getSession().setAttribute("shippingAddress", shippingAddress);
                request.getSession().setAttribute("phoneNumber", phoneNumber);
                request.getSession().setAttribute("email", email);
                request.getSession().setAttribute("note", note);
                return "redirect:" + paymentUrl;
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("message", "Lỗi trong quá trình tạo URL thanh toán.");
                return "Cart/payment-result";
            }
        }else if(paymentMethod.equalsIgnoreCase("cod")){
            List<CartItem> cartItems = cartService.getCartItems();
            if (cartItems.isEmpty()) {
                return "redirect:/cart"; // Redirect if cart is empty
            }

            BigDecimal totalAmount = cartItems.stream()
                    .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Boolean paymentStatus = true;
            String transactionId = "113";
            User user = (User) authentication.getPrincipal();
            orderService.createOrder(customerName, shippingAddress, phoneNumber, email, note, paymentMethod, cartItems, totalAmount, paymentStatus, transactionId, user);
            return "redirect:/order/confirmation";
        }

        model.addAttribute("message", "Phương thức thanh toán không được hỗ trợ!");
        return "Cart/payment-result";
    }

    @GetMapping("/vnpay_return")
    public String vnPayReturn(HttpServletRequest request, Model model, Authentication authentication) {
        int paymentStatus = vnpayService.orderReturn(request);
        String statusCode = request.getParameter("vnp_TransactionStatus");

        if (paymentStatus == 1) { // Thanh toán thành công
            String orderInfo = request.getParameter("vnp_OrderInfo");
            String paymentTime = request.getParameter("vnp_PayDate");
            String transactionId = request.getParameter("vnp_TransactionNo");
            String totalPriceStr = request.getParameter("vnp_Amount");


            BigDecimal totalPrice = new BigDecimal(totalPriceStr).divide(new BigDecimal(100)); // VNPay trả về tổng tiền theo đơn vị là 100

            // Lấy thông tin khách hàng từ request hoặc session
            String customerName = (String) request.getSession().getAttribute("customerName");
            String shippingAddress = (String) request.getSession().getAttribute("shippingAddress");
            String phoneNumber = (String) request.getSession().getAttribute("phoneNumber");
            String email = (String) request.getSession().getAttribute("email");
            String note = (String) request.getSession().getAttribute("note");

            // Lưu thông tin đơn hàng vào cơ sở dữ liệu
            List<CartItem> cartItems = cartService.getCartItems(); // Lấy danh sách sản phẩm trong giỏ hàng
            User user = (User) authentication.getPrincipal();
            Order order = orderService.createOrder(customerName, shippingAddress, phoneNumber, email, note, "vnpay", cartItems, totalPrice, true, transactionId, user);

            // Xóa giỏ hàng sau khi đã thanh toán thành công
            cartService.clearCart();

            // Gửi các thông tin cần thiết để hiển thị kết quả thanh toán
            model.addAttribute("orderId", "VNPay");
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("paymentTime", paymentTime);
            model.addAttribute("transactionId", transactionId);
            model.addAttribute("customerName", customerName);
            model.addAttribute("shippingAddress", shippingAddress);
            model.addAttribute("phoneNumber", phoneNumber);
            model.addAttribute("email", email);
            model.addAttribute("note", note);
            model.addAttribute("message", "Thanh toán VNPay thành công.");
            }else {
            model.addAttribute("message", "Thanh toán VNPay không thành công.Mã lỗi: "+statusCode);
        }
        return "Cart/payment-result";
    }
}
