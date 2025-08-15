package com.cybergrid.productsapi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cybergrid.productsapi.models.Product;
import com.cybergrid.productsapi.repositories.ProductsRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class ProductsServiceUnitTest {
  @Mock
  private ProductsRepository productsRepository;

  @InjectMocks
  private ProductsService productsService;

  @Test
  @DisplayName("getProductById should return product if product is found")
  void getProductByIdShouldReturnProduct() {
    UUID productId = UUID.randomUUID();
    Product product = new Product(productId, "Name", "Description", new BigDecimal("100.00"));

    when(productsRepository.findById(productId)).thenReturn(Optional.of(product));

    Product result = productsService.getProductById(productId);

    assertThat(result).isEqualTo(product);
  }

  @Test
  @DisplayName("getProductById should throw if product not found")
  void getProductByIdShouldThrowIfProductNotFound() {
    UUID productId = UUID.randomUUID();
    when(productsRepository.findById(productId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productsService.getProductById(productId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Product not found");
  }

  @Test
  @DisplayName("updateProduct should update product if product is found")
  void updateProductShouldUpdateProduct() {
    UUID productId = UUID.randomUUID();
    Product product = new Product(productId, "Name", "Description", new BigDecimal("100.00"));

    when(productsRepository.existsById(productId)).thenReturn(true);

    productsService.updateProduct(productId, product);

    verify(productsRepository).save(product);
  }

  @Test
  @DisplayName("updateProduct should throw if product not found")
  void updateProductShouldThrowIfProductNotFound() {
    UUID productId = UUID.randomUUID();
    Product product = new Product(productId, "Name", "Description", new BigDecimal("100.00"));

    when(productsRepository.existsById(productId)).thenReturn(false);

    assertThatThrownBy(() -> productsService.updateProduct(productId, product))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Product not found");
  }

  @Test
  @DisplayName("deleteProduct should delete product if product is found")
  void deleteProductShouldDeleteProduct() {
    UUID productId = UUID.randomUUID();

    when(productsRepository.existsById(productId)).thenReturn(true);

    productsService.deleteProduct(productId);

    verify(productsRepository).deleteById(productId);
  }

  @Test
  @DisplayName("deleteProduct should throw if product not found")
  void deleteProductShouldThrowIfProductNotFound() {
    UUID productId = UUID.randomUUID();

    when(productsRepository.existsById(productId)).thenReturn(false);

    assertThatThrownBy(() -> productsService.deleteProduct(productId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Product not found");
  }
}
