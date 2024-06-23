package com.products_management_system.product_management_system.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.products_management_system.product_management_system.dto.ProductDTO;
import com.products_management_system.product_management_system.models.Product;
import com.products_management_system.product_management_system.repositories.ProductsRepository;

import jakarta.validation.Valid;

@Service
public class ProductService {
    @Autowired
    private ProductsRepository productsRepository;

    public List<Product> collectionOrderDESC() {
        List<Product> products = productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return products;
    }

    public void addNewProduct(@Valid ProductDTO productDTO, Date createdAt, String storageFileName) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setBrand(productDTO.getBrand());
        product.setCategory(productDTO.getCategory());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setCreatedAt(createdAt);
        product.setLastModified(createdAt);
        product.setImageFileName(storageFileName);
        productsRepository.save(product);
    }


	public Product findProductById(Integer id) {
        Product product = productsRepository.findById(id).get();
        return product;
    }

    public void delete(Product product) {
        productsRepository.delete(product);
    }

    public void updateProduct(Product product, @Valid ProductDTO productDTO, Date lastModified,
            String storageFileName) {
        product.setName(productDTO.getName());
        product.setBrand(productDTO.getBrand());
        product.setCategory(productDTO.getCategory());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setLastModified(lastModified);
        product.setImageFileName(storageFileName);
        productsRepository.save(product);
    }
    public void updateProduct(Product product, @Valid ProductDTO productDTO, Date lastModified) {
        product.setName(productDTO.getName());
        product.setBrand(productDTO.getBrand());
        product.setCategory(productDTO.getCategory());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setLastModified(lastModified);
        productsRepository.save(product);
    }


    

  

}
