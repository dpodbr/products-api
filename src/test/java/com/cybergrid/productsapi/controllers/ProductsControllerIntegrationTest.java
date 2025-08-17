package com.cybergrid.productsapi.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.cybergrid.productsapi.dto.ProductRequest;
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

  private final ProductRequest productRequest = new ProductRequest(
      "Name",
      "Description",
      new BigDecimal("100.00"));

  @Test
  @DisplayName("createProduct should create a new product")
  void createProductShouldCreateProduct() throws Exception {
    MockHttpServletResponse response = postProductHelper(productRequest);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

    String body = response.getContentAsString();
    assertThat(body).isNotBlank();
    Product product = objectMapper.readValue(body, Product.class);

    // Validate the Location header and returned product.
    UUID productId = product.getId();
    assertThat(productId).isNotNull();
    assertThat(response.getHeader("Location")).contains("/api/v1/products/" + productId);

    assertThat(product.getName()).isEqualTo(productRequest.getName());
    assertThat(product.getDescription()).isEqualTo(productRequest.getDescription());
    assertThat(product.getPrice()).isEqualByComparingTo(productRequest.getPrice());
  }

  @Test
  @DisplayName("createProduct should validate product parameters")
  void createProductShouldValidateProductParameters() throws Exception {
    ProductRequest invalidProductRequest = new ProductRequest(
        "",
        null,
        new BigDecimal("-5.00"));

    MockHttpServletResponse response = postProductHelper(invalidProductRequest);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    // Fix the name so it's not blank.
    invalidProductRequest.setName("Name");

    response = postProductHelper(invalidProductRequest);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    // Fix price so it's not negative.
    invalidProductRequest.setPrice(new BigDecimal("0.00"));

    // All fields are now valid, so we should be able to create the product.
    response = postProductHelper(invalidProductRequest);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
  }

  @Test
  @DisplayName("getProducts should return all products")
  void getProductsShouldReturnAllProducts() throws Exception {
    // Create a few products so we can get them back.
    MockHttpServletResponse firstResponse = postProductHelper(productRequest);
    MockHttpServletResponse secondResponse = postProductHelper(productRequest);

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
    MockHttpServletResponse postResponse = postProductHelper(productRequest);
    Product product = objectMapper.readValue(
        postResponse.getContentAsString(),
        Product.class);

    // Validate returned product.
    getProductAndValidate(product.getId(), productRequest);
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
    MockHttpServletResponse postResponse = postProductHelper(productRequest);
    UUID productId = objectMapper
        .readValue(postResponse.getContentAsString(), Product.class)
        .getId();

    // Update the product.
    ProductRequest updateProductRequest = new ProductRequest(
        "Updated Name",
        "Updated Description",
        new BigDecimal("200.00")
    );

    MockHttpServletResponse updateResponse = updateProductHelper(productId, updateProductRequest);
    assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

    // Validate returned product.
    getProductAndValidate(productId, updateProductRequest);
  }

  @Test
  @DisplayName("updateProduct should return 404 if product is not found")
  void updateProductShouldReturn404IfProductNotFound() throws Exception {
    MockHttpServletResponse response = updateProductHelper(UUID.randomUUID(), productRequest);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.getContentAsString()).contains("Product not found");
  }

  @Test
  @DisplayName("deleteProduct should delete product if product is found")
  void deleteProductShouldDeleteProduct() throws Exception {
    // Create a product so we can delete it.
    MockHttpServletResponse postResponse = postProductHelper(productRequest);
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

  private MockHttpServletResponse postProductHelper(ProductRequest request) throws Exception {
    MvcResult result = mockMvc
        .perform(
          post("/api/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
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

  private MockHttpServletResponse updateProductHelper(
      UUID id,
      ProductRequest request) throws Exception {
    MvcResult result = mockMvc
        .perform(
          put("/api/v1/products/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
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

  private void getProductAndValidate(
      UUID productId,
      ProductRequest request) throws Exception {
    MockHttpServletResponse getResponse = getProductHelper(productId);
    assertThat(getResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(getResponse.getContentAsString()).isNotBlank();

    Product product = objectMapper.readValue(
        getResponse.getContentAsString(),
        Product.class);
    assertThat(product.getId()).isEqualTo(productId);
    assertThat(product.getName()).isEqualTo(request.getName());
    assertThat(product.getDescription()).isEqualTo(request.getDescription());
    assertThat(product.getPrice()).isEqualByComparingTo(request.getPrice());
  }
}
