# Products API
This is a REST API for products.
It connects to a PostgreSQL database which stores products.
There is no auth required (API is open to everyone) since it was not a requirement.

## Installation instructions
1. Copy the provided `.env.example` to `.env`. These are the environment variables that are read by the application (you can leave defaults)
   * Be sure to run `docker compose build` so the changes in `.env` are copied to container
2. Run `docker compose up -d postgresdb productsapi`
   * WARNING: You can start `pgadmin` for database Web UI: `docker compose up -d pgadmin` but this should not be used in production as it's meant for development and not properly secured
   * You can also run `docker compose up -d` to bring all services up, but note above warning
3. With the provided defaults, endpoints are available at:
   * API documentation (fully interactable): http://localhost:8080/swagger-ui/index.html
   * API endpoint: http://localhost:8080/api/v1/products
   * Optionally (if started) pgadmin: http://localhost:5050/

## Local development instructions
1. Copy the provided `.env.example` to `.env`
2. Change `DB_HOST` in `.env` to `localhost`
   * Be sure to run `docker compose build` so the changes in `.env` are copied to container
3. Start up `postgresdb` and (optionally) `pgadmin` or provide your own PostgresSQL database `docker compose up -d postgresdb pgadmin`
4. Project was built with IntelliJ IDEA, so it's recommended to use it for development
5. There are 3 run configurations provided:
   * `ProductsAPIApplication` — runs the application
     * You can use some predefined requests for testing in `src/test/java/ProductsRequests.http` or use API documentation from above to test endpoints
   * `All Unit Tests` — runs all unit tests
     * Choose `Run 'All Unit Tests' with Coverage` to run unit tests with coverage report
   * `All Integration Tests` — runs all integration tests
     * Choose `Run 'All Integration Tests' with Coverage` to run integration tests with coverage report
6. For linting and formatting, CheckStyle plugin in IntelliJ IDEA is used with `Google Checks` preset (install it in IDEA)
