package com.cybergrid.productsapi.controllers;

import com.cybergrid.productsapi.dto.ProductRequest;
import com.cybergrid.productsapi.dto.ProductResponse;
import com.cybergrid.productsapi.mappers.ProductMapper;
import com.cybergrid.productsapi.models.Product;
import com.cybergrid.productsapi.services.ProductsService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST controller for managing {@link Product}s.
 *
 * <p>We're not setting any cache control headers, since we're expecting products
 * to change often.
 */
@RestController
@RequestMapping("api/v1/products")
public class ProductsController {
  private final ProductsService productsService;

  public ProductsController(ProductsService productsService) {
    this.productsService = productsService;
  }

  @GetMapping
  public List<ProductResponse> getProducts() {
    return productsService.getProducts()
        .stream()
        .map(ProductMapper::toResponse)
        .toList();
  }

  @GetMapping("{id}")
  public ProductResponse getProductById(@PathVariable UUID id) {
    return ProductMapper.toResponse(productsService.getProductById(id));
  }

  // Validation is handled by Spring Boot (@Valid).
  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productDto) {
    Product product = ProductMapper.toEntity(productDto);

    ProductResponse response = ProductMapper.toResponse(productsService.createProduct(product));

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.getId())
        .toUri();

    // return 201 Created with a Location header and product in response body.
    return ResponseEntity.created(location).body(response);
  }

  @PutMapping("{id}")
  public void updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequest productDto) {
    Product product = ProductMapper.toEntity(productDto);

    productsService.updateProduct(id, product);
  }

  @DeleteMapping("{id}")
  public void deleteProduct(@PathVariable UUID id) {
    productsService.deleteProduct(id);
  }
}
