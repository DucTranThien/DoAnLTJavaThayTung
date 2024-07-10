package com.hutech.tranthienducpro.Controller;

import com.hutech.tranthienducpro.model.Product;
import com.hutech.tranthienducpro.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/search/products")
public class SearchApiController {
    @Autowired
    private ProductService productService;
    @GetMapping("")
    public List<Product> getAllProductsByKeyWord(@RequestParam(name = "keyword", required = false) String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return productService.getAllProducts();
        }
        return productService.searchProduct(keyword);
    }
}
