package com.cybergrid.productsapi.repositories;

import com.cybergrid.productsapi.models.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Product, UUID> {
}
