package com.hutech.tranthienducpro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(precision = 20, scale = 0)
    @DecimalMin(value = "0.01", inclusive = true, message = "Giá sản phẩm phải lớn hơn 0")
    @NotNull(message = "Giá sản phẩm không được để trống")
    private BigDecimal price;

    private String description;
    private String imageUrl;
    @Transient
    private MultipartFile imageFile;

    private int stock; // Số lượng tồn kho của sản phẩm
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
