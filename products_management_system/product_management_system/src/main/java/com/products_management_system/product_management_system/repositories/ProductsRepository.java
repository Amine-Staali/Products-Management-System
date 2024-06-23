package com.products_management_system.product_management_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.products_management_system.product_management_system.models.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer>{
    
}
