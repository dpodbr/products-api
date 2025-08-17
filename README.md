# Products API
This is a REST API for products.
It connects to a PostgreSQL database which stores products.
There is no auth required (API is open to everyone) since it was not a requirement.

## Installation instructions
1. Copy the provided `.env.example` to `.env`. These are the environment variables that are read by the application (you can leave defaults)
2. Run `docker compose up -d postgresdb productsapi`
   * Optionally start `pgadmin` for database Web UI: `docker compose up -d pgadmin`
   * Or just run `docker compose up -d` to bring all services up
3. With the provided defaults, endpoints are available at:
   * API documentation: http://localhost:8080/swagger-ui/index.html
   * API endpoints: http://localhost:8080/api/v1/products
   * Optionally (if started) pgadmin: http://localhost:5050/

## Local development instructions
1. Copy the provided `.env.example` to `.env`
2. Change `DB_HOST` in `.env` to `localhost`
3. Start up `postgresdb` and (optionally) `pgadmin` or provide your own PostgresSQL database `docker compose up -d postgresdb pgadmin`
4. Project was built with IntelliJ IDEA, so it's recommended to use it for development.
5. There are 3 run configurations provided:
   * ProductsAPIApplication—runs the application
   * All Unit Tests—runs all unit tests
   * All Integration Tests—runs all integration tests
