package com.cybergrid.productsapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

class ApiErrorHandlerUnitTest {

  @Test
  @DisplayName("handle should return expected body and status for ResponseStatusException")
  void handleShouldReturnExpectedBodyAndStatusForResponseStatusException() {
    ApiErrorHandler handler = new ApiErrorHandler();

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/products/123");

    ResponseStatusException ex = new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Product not found"
    );

    ResponseEntity<Map<String, Object>> response = handler.handle(ex, request);

    assertThat(HttpStatus.NOT_FOUND).isEqualTo(response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(404).isEqualTo(body.get("status"));
    assertThat("Product not found").isEqualTo(body.get("error"));
    assertThat("/api/products/123").isEqualTo(body.get("path"));
    Object timestamp = body.get("timestamp");
    assertThat(OffsetDateTime.parse((String) timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        .isNotNull();
  }
}
