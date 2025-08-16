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

  private final Product product = new Product(
      UUID.randomUUID(),
      "Name",
      "Description",
      new BigDecimal("100.00"));

  @Test
  @DisplayName("getProductById should return product if product is found")
  void getProductByIdShouldReturnProduct() {
    when(productsRepository.findById(product.getId())).thenReturn(Optional.of(product));

    Product result = productsService.getProductById(product.getId());

    assertThat(result).isEqualTo(product);
  }

  @Test
  @DisplayName("getProductById should throw if product not found")
  void getProductByIdShouldThrowIfProductNotFound() {
    when(productsRepository.findById(product.getId())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productsService.getProductById(product.getId()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Product not found");
  }

  @Test
  @DisplayName("updateProduct should update product if product is found")
  void updateProductShouldUpdateProduct() {
    when(productsRepository.existsById(product.getId())).thenReturn(true);

    productsService.updateProduct(product.getId(), product);

    verify(productsRepository).save(product);
  }

  @Test
  @DisplayName("updateProduct should throw if product not found")
  void updateProductShouldThrowIfProductNotFound() {
    when(productsRepository.existsById(product.getId())).thenReturn(false);

    assertThatThrownBy(() -> productsService.updateProduct(product.getId(), product))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Product not found");
  }

  @Test
  @DisplayName("deleteProduct should delete product if product is found")
  void deleteProductShouldDeleteProduct() {
    when(productsRepository.existsById(product.getId())).thenReturn(true);

    productsService.deleteProduct(product.getId());

    verify(productsRepository).deleteById(product.getId());
  }

  @Test
  @DisplayName("deleteProduct should throw if product not found")
  void deleteProductShouldThrowIfProductNotFound() {
    when(productsRepository.existsById(product.getId())).thenReturn(false);

    assertThatThrownBy(() -> productsService.deleteProduct(product.getId()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Product not found");
  }
}
