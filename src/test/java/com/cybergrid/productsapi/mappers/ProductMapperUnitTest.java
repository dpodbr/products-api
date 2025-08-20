package com.cybergrid.productsapi.mappers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.cybergrid.productsapi.dto.ProductRequest;
import com.cybergrid.productsapi.dto.ProductResponse;
import com.cybergrid.productsapi.models.Product;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductMapperUnitTest {

  @Test
  @DisplayName("toEntity returns null when request is null")
  void toEntityNullRequestReturnsNull() {
    assertThat(ProductMapper.toEntity(null)).isNull();
  }

  @Test
  @DisplayName("toEntity maps fields and leaves id null")
  void toEntityMapsFieldsAndIdIsNull() {
    ProductRequest request = new ProductRequest(
        "Laptop",
        "14-inch ultrabook",
        new BigDecimal("1299.99"));

    Product entity = ProductMapper.toEntity(request);

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isNull();
    assertThat(entity.getName()).isEqualTo(request.getName());
    assertThat(entity.getDescription()).isEqualTo(request.getDescription());
    assertThat(entity.getPrice()).isEqualTo(request.getPrice());
  }

  @Test
  @DisplayName("toResponse returns null when product is null")
  void toResponseNullProductReturnsNull() {
    assertThat(ProductMapper.toResponse(null)).isNull();
  }

  @Test
  @DisplayName("toResponse maps all fields")
  void toResponseMapsAllFields() {
    UUID id = UUID.randomUUID();
    Product product = new Product(id, "Mouse", "Wireless mouse", new BigDecimal("24.50"));

    ProductResponse response = ProductMapper.toResponse(product);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(id);
    assertThat(response.getName()).isEqualTo("Mouse");
    assertThat(response.getDescription()).isEqualTo("Wireless mouse");
    assertThat(response.getPrice()).isEqualTo(new BigDecimal("24.50"));
  }
}
