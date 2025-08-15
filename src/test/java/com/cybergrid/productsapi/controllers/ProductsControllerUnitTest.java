package com.cybergrid.productsapi.controllers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import com.cybergrid.productsapi.models.Product;
import com.cybergrid.productsapi.services.ProductsService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class ProductsControllerUnitTest {

  @Mock
  private ProductsService productsService;

  @InjectMocks
  private ProductsController productsController;

  @Test
  @DisplayName("updateProduct should call service if product and path Id match")
  void updateProductShouldCallServiceIfProductAndPathIdMatch() {
    UUID productId = UUID.randomUUID();
    Product product = new Product(productId, "Name", "Description", new BigDecimal("100.00"));

    productsController.updateProduct(productId, product);

    verify(productsService).updateProduct(productId, product);
  }

  @Test
  @DisplayName("updateProduct should throw if product and path Id do not match")
  void updateProductShouldThrowIfProductAndPathIdDoNotMatch() {
    UUID productId = UUID.randomUUID();
    Product product = new Product(productId, "Name", "Description", new BigDecimal("100.00"));

    assertThatThrownBy(() -> productsController.updateProduct(UUID.randomUUID(), product))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("ID in path and body don't match");
  }

  @Test
  @DisplayName("updateProduct should call service if product does not contain Id")
  void updateProductShouldCallServiceIfProductDoesNotContainId() {
    UUID productId = UUID.randomUUID();
    Product product = new Product(null, "Name", "Description", new BigDecimal("100.00"));

    productsController.updateProduct(productId, product);

    verify(productsService).updateProduct(productId, product);
  }
}
