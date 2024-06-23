package com.products_management_system.product_management_system.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductDTO {
    @NotEmpty(message = "The name is required")
    private String name;
    @NotEmpty(message = "The brand is required")
    private String brand;
    @NotEmpty(message = "The category is required")
    private String category;
    @Min(0)
    private double price;

    @Size(min = 10, message = "The descriptions should be at least 10 characters")
    @Size(max = 2000, message = "The descriptions cannot exceed 2000 characters")
    private String description;

    private MultipartFile imageFile;
}
