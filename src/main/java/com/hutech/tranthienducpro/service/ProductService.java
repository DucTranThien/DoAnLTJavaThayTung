package com.hutech.tranthienducpro.service;

import com.hutech.tranthienducpro.model.Product;
import com.hutech.tranthienducpro.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    // Retrieve a product by its id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    // Add a new product to the database
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }
    // Update an existing product
    public Product updateProduct(@NotNull Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setStock(product.getStock());
        existingProduct.setCategory(product.getCategory());
        return productRepository.save(existingProduct);
    }
    // Delete a product by its id
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }

    // Phương thức để lấy danh sách các sản phẩm thuộc một category cụ thể
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    // Search products by name containing the keyword
    public List<Product> searchProductsByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
    public List<Product> searchProduct(String keyword) {
        return productRepository.search(keyword);
    }
    @Transactional
    public void updateProductStock(Long productId, int quantitySold) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("ID sản phẩm không hợp lệ: " + productId));
        int updatedStock = product.getStock() - quantitySold;
        if (updatedStock < 0) {
            throw new IllegalArgumentException("Không đủ hàng tồn kho cho ID sản phẩm: " + productId);
        }
        product.setStock(updatedStock);
        productRepository.save(product);
    }
}
