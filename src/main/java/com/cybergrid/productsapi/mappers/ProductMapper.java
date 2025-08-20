package com.cybergrid.productsapi.mappers;

import com.cybergrid.productsapi.dto.ProductRequest;
import com.cybergrid.productsapi.dto.ProductResponse;
import com.cybergrid.productsapi.models.Product;

public final class ProductMapper {
  public static Product toEntity(ProductRequest request) {
    if (request == null) {
      return null;
    }

    return new Product(
        null,
        request.getName(),
        request.getDescription(),
        request.getPrice()
    );
  }

  public static ProductResponse toResponse(Product product) {
    if (product == null) {
      return null;
    }

    return new ProductResponse(
      product.getId(),
      product.getName(),
      product.getDescription(),
      product.getPrice()
    );
  }
}
