package com.cybergrid.productsapi;

import static org.assertj.core.api.Assertions.assertThat;

import com.cybergrid.productsapi.controllers.ProductsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductsApiApplicationIntegrationTest {

  @Autowired
  private ProductsController productsController;

  @Test
  void contextLoads() {
    assertThat(productsController).isNotNull();
  }
}
