package com.hutech.tranthienducpro.Controller;

import com.hutech.tranthienducpro.model.CartItem;
import com.hutech.tranthienducpro.model.Order;
import com.hutech.tranthienducpro.model.Product;
import com.hutech.tranthienducpro.model.User;
import com.hutech.tranthienducpro.service.CartService;
import com.hutech.tranthienducpro.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @GetMapping("/checkout")
    public String checkout() {
        return "/cart/checkout";
    }
//    @PostMapping("/submit")
//    public String submitOrder(String customerName, String shippingAddress, String phoneNumber, String email, String note, String paymentMethod, BigDecimal totalAmount, String paymentStatus, String transactionId) {
//        List<CartItem> cartItems = cartService.getCartItems();
//        if (cartItems.isEmpty()) {
//            return "redirect:/cart"; // Redirect if cart is empty
//        }
//        if (paymentMethod.equalsIgnoreCase("cash")) {
//            totalAmount = cartItems.stream()
//                    .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//            paymentStatus = "Thành Công";
//            transactionId = "113";
//        }
//
//        // Create order
//        orderService.createOrder(customerName, shippingAddress, phoneNumber, email, note, paymentMethod, cartItems, totalAmount, paymentStatus, transactionId);
//        return "redirect:/order/confirmation";
//    }
    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Thanh toán COD cho đơn hàng thành công.");
        return "Cart/order-confirmation";
    }

    @GetMapping("/history")
    public String orderHistory(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        if(orderService.getOrderByUser(userId).isEmpty()){
            model.addAttribute("error", true);
        }else{
            List<Order> orders = orderService.getOrderByUser(userId);
            model.addAttribute("error", false);
            model.addAttribute("orders", orders);
        }
        return "Cart/order-history";
    }

}
