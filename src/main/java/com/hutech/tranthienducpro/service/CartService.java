package com.hutech.tranthienducpro.service;

import com.hutech.tranthienducpro.model.CartItem;
import com.hutech.tranthienducpro.model.Product;
import com.hutech.tranthienducpro.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {

    private List<CartItem> cartItems = new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;

    public void addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        // Kiểm tra số lượng tồn kho của sản phẩm
        if (quantity > product.getStock()) {
            throw new IllegalArgumentException("Số lượng sản phẩm trong giỏ hàng không được lớn hơn số lượng hiện có: " + product.getStock());
        }

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
        boolean found = false;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                int newQuantity = item.getQuantity() + quantity;
                // Kiểm tra số lượng tổng cộng sau khi thêm vào giỏ hàng
                if (newQuantity > product.getStock()) {
                    throw new IllegalArgumentException("Số lượng sản phẩm trong giỏ hàng không được lớn hơn số lượng hiện có: " + product.getStock());
                }
                item.setQuantity(newQuantity);
                found = true;
                break;
            }
        }

        // Nếu sản phẩm chưa có trong giỏ hàng hoặc cập nhật số lượng thành công, thêm vào giỏ hàng
        if (!found) {
            cartItems.add(new CartItem(product, quantity));
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void clearCart() {
        cartItems.clear();
    }

    public boolean isQuantityValid(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        return quantity <= product.getStock();
    }
}
