package com.cybergrid.productsapi.controllers;

import com.cybergrid.productsapi.models.Product;
import com.cybergrid.productsapi.services.ProductsService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("api/v1/products")
public class ProductsController {

  private final ProductsService productsService;

  public ProductsController(ProductsService productsService) {
    this.productsService = productsService;
  }

  @GetMapping
  public List<Product> getProducts() {
    return productsService.getProducts();
  }

  @GetMapping("{id}")
  public Product getProductById(@PathVariable UUID id) {
    return productsService.getProductById(id);
  }

  // For more complex requests we usually accept a Product DTO from a client
  // and map it into the appropriate entity before persisting it.
  @PostMapping
  public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
    // Validation is performed by Spring automatically.
    Product savedProduct = productsService.createProduct(product);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(savedProduct.getId())
        .toUri();

    // 201 Created with a Location header and ID in the body.
    return ResponseEntity.created(location).body(savedProduct);
  }

  @PutMapping("{id}")
  public void updateProduct(@PathVariable UUID id, @Valid @RequestBody Product product) {
    // If a client provided Id in the path and body, we validate their match.
    if (product.getId() != null && !id.equals(product.getId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and body don't match");
    }

    productsService.updateProduct(id, product);
  }

  @DeleteMapping("{id}")
  public void deleteProduct(@PathVariable UUID id) {
    productsService.deleteProduct(id);
  }
}
