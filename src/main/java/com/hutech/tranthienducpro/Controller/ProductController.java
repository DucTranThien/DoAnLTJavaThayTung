package com.hutech.tranthienducpro.Controller;

import com.hutech.tranthienducpro.model.Product;
import com.hutech.tranthienducpro.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import com.hutech.tranthienducpro.service.ProductService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService; // Đảm bảo bạn đã inject CategoryService
    // Display a list of all products
    @GetMapping
    public String showProductList(Model model, @RequestParam(name = "search", required = false) String search) {
        List<Product> products;
        if (search != null && !search.isEmpty()) {
            products = productService.searchProductsByName(search);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        model.addAttribute("search", search);
        return "Product/product-list";
    }
    // For adding a new product
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "Product/add-product";
    }
    // Process the form for adding a new product
    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result, @RequestParam("imageFile") MultipartFile file) {
        if (result.hasErrors()) {
            return "Product/add-product";
        }
        // Lưu file ảnh
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get("src/main/resources/static/uploads/" + file.getOriginalFilename());
                Files.write(path, bytes);
                product.setImageUrl("/uploads/" + file.getOriginalFilename());  // Lưu đường dẫn ảnh vào database
            } catch (IOException e) {
                e.printStackTrace();
                return "Product/add-product";
            }
        }

        productService.addProduct(product);
        return "redirect:/products";
    }
    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "Product/update-product";
    }
    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "Product/product-detail";
    }

    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product,
                                BindingResult result, @RequestParam("imageFile") MultipartFile file) {
        if (result.hasErrors()) {
            product.setId(id);
            return "Product/update-product";
        }

        // Lưu file ảnh nếu có
        if (file != null && !file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get("src/main/resources/static/uploads/" + file.getOriginalFilename());
                Files.write(path, bytes);
                product.setImageUrl("/uploads/" + file.getOriginalFilename());  // Lưu đường dẫn ảnh vào database
            } catch (IOException e) {
                e.printStackTrace();
                return "Product/update-product";
            }
        } else {
            // Nếu không chọn ảnh mới, giữ nguyên ảnh cũ bằng cách lấy từ database
            Product existingProduct = productService.getProductById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
            product.setImageUrl(existingProduct.getImageUrl());
        }

        productService.updateProduct(product);
        return "redirect:/products";
    }
    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
    // Search products by name
    @GetMapping("/search")
    public String searchProductsByName(@RequestParam String keyword, Model model) {
        List<Product> products = productService.searchProductsByName(keyword);
        model.addAttribute("products", products);
        return "Product/product-list";
    }
    @GetMapping("/api/products")
    public String getAllProductByAPI() {
         return "api/products";
    }
}
