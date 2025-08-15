package com.cybergrid.productsapi.services;

import com.cybergrid.productsapi.models.Product;
import com.cybergrid.productsapi.repositories.ProductsRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductsService {
  private final ProductsRepository productsRepository;

  public ProductsService(ProductsRepository productsRepository) {
    this.productsRepository = productsRepository;
  }

  public List<Product> getProducts() {
    // For more complex entities we map database objects into DTO to filter out sensitive fields,
    // before returning it to client. In this case product is simple enough to be returned as is.
    return productsRepository.findAll();
  }

  public Product createProduct(Product product) {
    return productsRepository.save(product);
  }

  public Product getProductById(UUID id) {
    return productsRepository.findById(id).orElseThrow(
      () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
    );
  }

  public void updateProduct(UUID id, Product product) {
    if (!productsRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
    }

    product.setId(id);
    productsRepository.save(product);
  }

  public void deleteProduct(UUID id) {
    if (!productsRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
    }

    productsRepository.deleteById(id);
  }
}
