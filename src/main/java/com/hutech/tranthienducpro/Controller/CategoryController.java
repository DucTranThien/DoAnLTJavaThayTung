package com.hutech.tranthienducpro.Controller;

import com.hutech.tranthienducpro.model.Category;
import com.hutech.tranthienducpro.model.Product;
import com.hutech.tranthienducpro.service.CategoryService;
import com.hutech.tranthienducpro.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("categories")
@RequiredArgsConstructor
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;

    @Autowired
    private final ProductService productService;

    @GetMapping("/categories/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "Category/add-category";
    }

    @PostMapping("/categories/add")
    public String addCategory(@Valid Category category, BindingResult result) {
        if (result.hasErrors()) {
            return "Category/add-category";
        }
        categoryService.addCategory(category);
        return "redirect:/categories";
    }

    // Hiển thị danh sách danh mục
    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "Category/categories-list";
    }

    // GET request to show category edit form
    @GetMapping("/categories/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        model.addAttribute("category", category);
        return "/Category/update-category";
    }

    // POST request to update category
    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable("id") Long id, @Valid Category category,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            category.setId(id);
            return "/Category/update-category";
        }
        categoryService.updateCategory(category);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/categories";
    }

    // GET request for deleting category
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        List<Product> productsToDelete = productService.getProductsByCategoryId(id);

        if (id == 0) {
                // TH1: Xóa tất cả Product có CategoryID = 0
            for (Product product : productsToDelete) {
                productService.deleteProductById(product.getId());
            }
            categoryService.deleteCategoryById(id);
        } else {
            // TH2: Thông báo lỗi nếu có sản phẩm thuộc danh mục cần xóa
            if (!productsToDelete.isEmpty()) {
                String errorMessage = "Có " + productsToDelete.size() + " sản phẩm thuộc danh mục " + category.getName() + ". Không được phép xoá!";
                model.addAttribute("errorMessage", errorMessage);
                model.addAttribute("categories", categoryService.getAllCategories());
                return "Category/categories-list";
            } else {
                categoryService.deleteCategoryById(id);
            }
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/categories";
    }
    @GetMapping("/api/categories")
    public String getAllCategoryByAPI() {
        return "api/categories";
    }
}
