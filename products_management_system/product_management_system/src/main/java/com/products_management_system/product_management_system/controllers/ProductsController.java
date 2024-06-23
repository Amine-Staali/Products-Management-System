package com.products_management_system.product_management_system.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.products_management_system.product_management_system.dto.ProductDTO;
import com.products_management_system.product_management_system.models.Product;
import com.products_management_system.product_management_system.services.ProductService;

import jakarta.validation.Valid;

@Controller
@CrossOrigin(origins = { "http://127.0.0.1:8080", "http://localhost:8080" })
public class ProductsController {
    @Autowired
    private ProductService productService;

    @GetMapping({ "", "/" })
    public String LandingPage() {
        return "products/LandingPage";
    }

    @GetMapping({ "/products", "/products/" })
    public String getAllProducts(Model model) {
        List<Product> products = productService.collectionOrderDESC();
        model.addAttribute("products", products);
        return "products/products";
    }

    @GetMapping({ "/products/create", "/products/create/" })
    public String createProductPage(Model model) {
        ProductDTO productDTO = new ProductDTO();
        model.addAttribute("productDTO", productDTO);
        model.addAttribute("whereToSubmit", "/products/create");
        return "products/manageProduct";
    }

    @PostMapping({ "/products/create", "/products/create/" })
    public String createProduct(
            @Valid @ModelAttribute ProductDTO productDTO,
            BindingResult result,
            Model model) {

        if (productDTO.getImageFile() == null || productDTO.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDTO", "imageFile", "The image is required"));
        }
        if (result.hasErrors()) {
            model.addAttribute("productDTO", productDTO);
            return "products/manageProduct";
        }

        // save the image File
        MultipartFile image = productDTO.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
        try {
            String uploadDirectory = "public/images/";
            Path uploadPath = Paths.get(uploadDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Get the input stream from the uploaded image file
            try (InputStream inputStream = image.getInputStream()) {
                // Copy the input stream to the destination file
                Files.copy(inputStream, Paths.get(uploadDirectory + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            productService.addNewProduct(productDTO, createdAt, storageFileName);

        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/products/edit")
    public String editProductPage(@RequestParam Integer id, Model model) {
        try {
            Product product = productService.findProductById(id);
            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(product.getName());
            productDTO.setBrand(product.getBrand());
            productDTO.setCategory(product.getCategory());
            productDTO.setPrice(product.getPrice());
            productDTO.setDescription(product.getDescription());
            model.addAttribute("product", product);
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("whereToSubmit", "/products/edit?id=" + id);
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
            return "redirect:/products";
        }
        return "products/manageProduct";
    }

    @PostMapping("/products/edit")
    public String editProduct(
            @RequestParam Integer id,
            @Valid @ModelAttribute ProductDTO productDTO,
            BindingResult result,
            Model model) {

        Product product = productService.findProductById(id);
        Date lastModified = new Date();

        if (result.hasErrors()) {
            model.addAttribute("product", product);
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("whereToSubmit", "/products/edit?id=" + id);
            return "products/manageProduct";
        }

        if (!productDTO.getImageFile().isEmpty()) {
            MultipartFile image = productDTO.getImageFile();
            String storageFileName = lastModified.getTime() + "_" + image.getOriginalFilename();
            try {
                String uploadDirectory = "public/images/";

                // delete old image
                Path oldImagePath = Paths.get(uploadDirectory + product.getImageFileName());
                try {
                    Files.delete(oldImagePath);
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }

                // store the new image
                Path uploadPath = Paths.get(uploadDirectory);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                // Get the input stream from the uploaded image file
                try (InputStream inputStream = image.getInputStream()) {
                    // Copy the input stream to the destination file
                    Files.copy(inputStream, Paths.get(uploadDirectory + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                productService.updateProduct(product, productDTO, lastModified, storageFileName);

            } catch (Exception e) {
                System.out.println("Exception:" + e.getMessage());
                return "products/manageProduct";
            }
            return "redirect:/products";
        }

        productService.updateProduct(product, productDTO, lastModified);
        return "redirect:/products";
    }

    @GetMapping("/products/delete")
    public String deleteProduct(@RequestParam Integer id) {
        try {
            Product product = productService.findProductById(id);
            String uploadDirectory = "public/images/";
            // delete old image
            Path oldImagePath = Paths.get(uploadDirectory + product.getImageFileName());
            try {
                Files.delete(oldImagePath);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }

            productService.delete(product);

        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
            return "redirect:/products";
        }
        return "redirect:/products";
    }

}
