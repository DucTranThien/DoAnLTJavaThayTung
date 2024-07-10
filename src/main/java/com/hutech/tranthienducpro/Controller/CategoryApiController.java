package com.hutech.tranthienducpro.Controller;

import com.hutech.tranthienducpro.model.Category;
import com.hutech.tranthienducpro.Repository.CategoryRepository;
import com.hutech.tranthienducpro.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/categories")
public class CategoryApiController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping
    public List<Category> getAllProducts() {
        return categoryService.getAllCategories();
    }
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Product not found on :: "
                        + id));
        return ResponseEntity.ok().body(category);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id,
                                                   @RequestBody Category categoryDetails) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found on :: " + id));
        category.setName(categoryDetails.getName());
        categoryService.updateCategory(category);
        return ResponseEntity.ok(category);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Product not found on :: "+ id));
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }
}
