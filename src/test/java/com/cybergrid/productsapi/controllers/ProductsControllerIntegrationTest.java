package com.cybergrid.productsapi.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.cybergrid.productsapi.models.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductsControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  private final Product product = new Product(
      null,
      "Name",
      "Description",
      new BigDecimal("100.00"));

  @Test
  @DisplayName("createProduct should create a new product")
  void createProductShouldCreateProduct() throws Exception {
    MockHttpServletResponse response = postProductHelper(product);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

    String body = response.getContentAsString();
    assertThat(body).isNotBlank();
    Product savedProduct = objectMapper.readValue(body, Product.class);

    // Validate the Location header and returned product.
    UUID savedProductId = savedProduct.getId();
    assertThat(savedProductId).isNotNull();
    assertThat(response.getHeader("Location")).contains("/api/v1/products/" + savedProductId);

    assertThat(savedProduct.getName()).isEqualTo(product.getName());
    assertThat(savedProduct.getDescription()).isEqualTo(product.getDescription());
    assertThat(savedProduct.getPrice()).isEqualByComparingTo(product.getPrice());
  }

  @Test
  @DisplayName("createProduct should validate product parameters")
  void createProductShouldValidateProductParameters() throws Exception {
    Product invalidProduct = new Product(
        null,
        "",
        null,
        new BigDecimal("-5.00"));

    MockHttpServletResponse response = postProductHelper(invalidProduct);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    // Fix the name so it's not blank.
    invalidProduct.setName("Name");

    response = postProductHelper(invalidProduct);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    // Fix price so it's not negative.
    invalidProduct.setPrice(new BigDecimal("0.00"));

    // All fields are now valid, so we should be able to create the product.
    response = postProductHelper(invalidProduct);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
  }

  @Test
  @DisplayName("getProducts should return all products")
  void getProductsShouldReturnAllProducts() throws Exception {
    // Create a few products so we can get them back.
    MockHttpServletResponse firstResponse = postProductHelper(product);
    MockHttpServletResponse secondResponse = postProductHelper(product);

    // Get the Ids of the created products.
    UUID productId1 = objectMapper
        .readValue(firstResponse.getContentAsString(), Product.class)
        .getId();
    UUID productId2 = objectMapper
        .readValue(secondResponse.getContentAsString(), Product.class)
        .getId();

    // Validate we get back at least the two products we created.
    MockHttpServletResponse response = mockMvc
        .perform(get("/api/v1/products"))
        .andDo(print())
        .andReturn()
        .getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).contains(productId1.toString());
    assertThat(response.getContentAsString()).contains(productId2.toString());
  }

  @Test
  @DisplayName("getProductById should return product if product is found")
  void getProductByIdShouldReturnProduct() throws Exception {
    // Create a product so we can get it back.
    MockHttpServletResponse postResponse = postProductHelper(product);
    Product savedProduct = objectMapper.readValue(
        postResponse.getContentAsString(),
        Product.class);

    // Validate returned product.
    getProductAndValidate(savedProduct.getId(), product);
  }

  @Test
  @DisplayName("getProductById should return 404 if product is not found")
  void getProductByIdShouldReturn404IfProductNotFound() throws Exception {
    MockHttpServletResponse response = getProductHelper(UUID.randomUUID());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.getContentAsString()).contains("Product not found");
  }

  @Test
  @DisplayName("updateProduct should update product if product is found")
  void updateProductShouldUpdateProduct() throws Exception {
    // Create a product so we can update it.
    MockHttpServletResponse postResponse = postProductHelper(product);
    UUID productId = objectMapper
        .readValue(postResponse.getContentAsString(), Product.class)
        .getId();

    // Update the product.
    Product updatedProduct = new Product(
        null,
        "Updated Name",
        "Updated Description",
        new BigDecimal("200.00")
    );

    MockHttpServletResponse updateResponse = updateProductHelper(productId, updatedProduct);
    assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

    // Validate returned product.
    getProductAndValidate(productId, updatedProduct);
  }

  @Test
  @DisplayName("updateProduct should return 404 if product is not found")
  void updateProductShouldReturn404IfProductNotFound() throws Exception {
    MockHttpServletResponse response = updateProductHelper(UUID.randomUUID(), product);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.getContentAsString()).contains("Product not found");
  }

  @Test
  @DisplayName("deleteProduct should delete product if product is found")
  void deleteProductShouldDeleteProduct() throws Exception {
    // Create a product so we can delete it.
    MockHttpServletResponse postResponse = postProductHelper(product);
    UUID productId = objectMapper
        .readValue(postResponse.getContentAsString(), Product.class)
        .getId();

    // Delete the product.
    MockHttpServletResponse deleteResponse = deleteProductHelper(productId);
    assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

    // Validate product is no longer returned.
    MockHttpServletResponse getResponse = getProductHelper(productId);
    assertThat(getResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
  }

  @Test
  @DisplayName("deleteProduct should return 404 if product is not found")
  void deleteProductShouldReturn404IfProductNotFound() throws Exception {
    MockHttpServletResponse response = deleteProductHelper(UUID.randomUUID());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.getContentAsString()).contains("Product not found");
  }

  private MockHttpServletResponse postProductHelper(Product product) throws Exception {
    MvcResult result = mockMvc
        .perform(
          post("/api/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(product)))
        .andDo(print())
        .andReturn();

    return result.getResponse();
  }

  private MockHttpServletResponse getProductHelper(UUID id) throws Exception {
    MvcResult result = mockMvc
        .perform(get("/api/v1/products/{id}", id))
        .andDo(print())
        .andReturn();

    return result.getResponse();
  }

  private MockHttpServletResponse updateProductHelper(UUID id, Product product) throws Exception {
    MvcResult result = mockMvc
        .perform(
          put("/api/v1/products/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(product)))
        .andDo(print())
        .andReturn();

    return result.getResponse();
  }

  private MockHttpServletResponse deleteProductHelper(UUID id) throws Exception {
    MvcResult result = mockMvc
        .perform(
          delete("/api/v1/products/{id}", id))
        .andDo(print())
        .andReturn();

    return result.getResponse();
  }

  private void getProductAndValidate(UUID productId, Product validateWithProduct) throws Exception {
    MockHttpServletResponse getResponse = getProductHelper(productId);
    assertThat(getResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(getResponse.getContentAsString()).isNotBlank();

    Product fetchedProduct = objectMapper.readValue(
        getResponse.getContentAsString(),
        Product.class);
    assertThat(fetchedProduct.getId()).isEqualTo(productId);
    assertThat(fetchedProduct.getName()).isEqualTo(validateWithProduct.getName());
    assertThat(fetchedProduct.getDescription()).isEqualTo(validateWithProduct.getDescription());
    assertThat(fetchedProduct.getPrice()).isEqualByComparingTo(validateWithProduct.getPrice());
  }
}
