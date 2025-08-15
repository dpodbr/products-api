package com.cybergrid.productsapi;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handles expected controller exceptions to include custom messages in response.
 * Leaves other exceptions to the default Spring Boot error handler.
 */
@RestControllerAdvice
public class ApiErrorHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handle(
      ResponseStatusException ex,
      HttpServletRequest request) {

    // Match Spring Boot's default error response format.
    String timestamp = OffsetDateTime.now(ZoneOffset.UTC)
        .truncatedTo(ChronoUnit.MILLIS)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", timestamp);
    body.put("status", ex.getStatusCode().value());
    body.put("error", ex.getReason());
    body.put("path", request.getRequestURI());

    return ResponseEntity.status(ex.getStatusCode()).body(body);
  }
}
