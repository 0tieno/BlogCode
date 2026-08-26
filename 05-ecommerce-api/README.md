# 05 - E-Commerce API

A standalone Spring Boot REST API for a small e-commerce catalog: products,
categories, and orders, backed by PostgreSQL and Redis. This module is the
fifth step in the BlogCode Spring Boot curriculum and is intentionally the
first project in the series to combine **advanced JPA relationships**,
**caching**, **async processing**, and **file uploads** in one codebase.

---

## 1. What this project teaches

| Concept                                   | Where to look |
|--------------------------------------------|----------------|
| Many-to-many JPA relationships              | `domain/Product.java`, `domain/Category.java` |
| One-to-many aggregate roots (cascade, orphan removal) | `domain/Order.java`, `domain/OrderItem.java` |
| Dynamic queries with the JPA Criteria API (Specifications) | `specification/ProductSpecifications.java` |
| Pagination & sorting                        | `controller/ProductController.java`, `dto/PageResponse.java` |
| Redis-backed caching (`@Cacheable`/`@CacheEvict`) | `service/impl/ProductServiceImpl.java` |
| Asynchronous processing (`@Async`)          | `service/impl/EmailServiceImpl.java`, `config/AsyncConfig.java` |
| File uploads served as static resources     | `service/impl/FileStorageServiceImpl.java`, `config/WebConfig.java` |
| Centralized exception handling              | `exception/GlobalExceptionHandler.java` |
| Bean Validation (`jakarta.validation`)      | `dto/ProductRequest.java`, `dto/OrderCreateRequest.java` |
| Production endpoints via Actuator           | `application.yml` (`management.*`) |
| Layered architecture (controller/service/repository) with interfaces + DTOs | whole project |
| Web-layer slice tests with `spring-boot-starter-webmvc-test` | `src/test/java/.../controller/ProductControllerTest.java` |

### New concepts vs. earlier curriculum modules

- **Many-to-many mapping**: a `Product` can belong to several `Category`
  rows and vice versa, requiring an explicit `@JoinTable` (the owning side,
  in `Product`) and a `mappedBy` inverse side (in `Category`).
- **Aggregate roots**: `Order` fully owns its `OrderItem`s via
  `cascade = CascadeType.ALL, orphanRemoval = true` - there is deliberately
  no `OrderItemRepository`.
- **JPA Specifications**: instead of writing one derived query method per
  filter combination, `ProductSpecifications` builds small, composable
  `Specification<Product>` predicates combined at runtime with
  `Specification.allOf(...)`.
- **Caching**: `ProductServiceImpl.getById` is `@Cacheable`, meaning repeat
  lookups are served from Redis instead of PostgreSQL; `update`/`delete` are
  `@CacheEvict` so the cache never serves stale data.
- **Async email simulation**: placing an order returns to the HTTP client
  immediately while `EmailServiceImpl.sendOrderConfirmation` "sends" (really:
  logs, after a simulated delay) on a background thread pool.
- **File uploads**: uploaded images are written to `uploads/images/` inside
  this project (not the OS temp folder) and served back over HTTP via a
  `WebMvcConfigurer` resource handler.

---

## 2. Architecture

```
                                   ┌─────────────────────────┐
                                   │        HTTP Client       │
                                   │ (curl / Postman / React) │
                                   └────────────┬─────────────┘
                                                │ JSON / multipart
                                                ▼
 ┌───────────────────────────────────────────────────────────────────────┐
 │                              Controller layer                          │
 │  CategoryController │ ProductController │ OrderController │ FileUpload │
 └───────────────────────────────┬─────────────────────────────────────┬─┘
                                  │ DTOs (records)                      │
                                  ▼                                     │
 ┌───────────────────────────────────────────────────────────────────┐ │
 │                             Service layer                          │ │
 │  CategoryService │ ProductService │ OrderService │ EmailService     │ │
 │        (interfaces)         │        impl/* (business rules)       │ │
 └───────┬───────────────┬─────┴───────────┬─────────────────┬───────┘ │
         │               │ @Cacheable       │ @Async          │        │
         ▼               ▼ @CacheEvict      ▼                 │        │
 ┌───────────────┐ ┌──────────────┐  ┌───────────────┐        │        │
 │ Repository     │ │    Redis     │  │ "Email" thread│        │        │
 │ layer (Spring  │ │  (cache)     │  │  pool (async) │        │        │
 │ Data JPA)      │ └──────────────┘  └───────────────┘        │        │
 └───────┬────────┘                                            │        │
         │ SQL                                                 ▼        ▼
         ▼                                              ┌─────────────────┐
 ┌───────────────┐                                       │ FileStorageService│
 │  PostgreSQL    │                                       │  -> uploads/images│
 │ products       │                                       │  <- WebConfig    │
 │ categories     │                                       │  serves /images/**│
 │ product_category (join table)                          └─────────────────┘
 │ orders         │
 │ order_items    │
 └───────────────┘
```

Entity relationship summary:

```
Category  ──many-to-many──  Product  ──one-to-many (via OrderItem)──  Order
   ▲ mappedBy                  ▲ owning side (JoinTable)                 │
   └── product_category table ─┘                                        │
                                                                          │
                            OrderItem ───many-to-one──────────────────► Order
                            OrderItem ───many-to-one──────────────────► Product
```

---

## 3. Project layout

```
05-ecommerce-api/
├── pom.xml                      Spring Boot 4.1.0 / Java 25 build definition
├── docker-compose.yml           PostgreSQL + Redis for local development
├── mvnw / mvnw.cmd               Maven launcher scripts
├── uploads/images/                Where uploaded product images are stored
├── src/main/java/com/blogcode/ecommerce/
│   ├── EcommerceApiApplication.java   Spring Boot entry point
│   ├── config/                  CacheConfig, AsyncConfig, WebConfig
│   ├── domain/                  Category, Product, Order, OrderItem, OrderStatus
│   ├── dto/                     Request/response records + PageResponse
│   ├── repository/               Spring Data JPA repositories
│   ├── specification/            ProductSpecifications (Criteria API)
│   ├── mapper/                   Entity <-> DTO static mappers
│   ├── service/                  Service interfaces
│   ├── service/impl/              Service implementations
│   ├── controller/               REST controllers
│   └── exception/                 ResourceNotFoundException, GlobalExceptionHandler, ...
├── src/main/resources/application.yml
└── src/test/java/...             ProductControllerTest (MockMvc), OrderServiceImplTest (Mockito)
```

---

## 4. Running the project

### Prerequisites

- JDK 25
- Apache Maven (or use the provided `mvnw`/`mvnw.cmd` wrapper scripts)
- Docker + Docker Compose (for PostgreSQL and Redis)

### Steps (Windows PowerShell)

```powershell
cd C:\Users\USER\developer\BlogCode\05-ecommerce-api

# 1. Start PostgreSQL and Redis
docker compose up -d

# 2. Run the application
.\mvnw.cmd spring-boot:run

# The API is now listening on http://localhost:8080
```

To run the automated tests:

```powershell
.\mvnw.cmd test
```

---

## 5. Endpoints & curl examples

Base URL: `http://localhost:8080`

### Categories

```bash
# Create a category
curl -X POST http://localhost:8080/api/v1/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Electronics","description":"Gadgets and devices"}'

# List all categories
curl http://localhost:8080/api/v1/categories

# Get one category
curl http://localhost:8080/api/v1/categories/1

# Update a category
curl -X PUT http://localhost:8080/api/v1/categories/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Electronics","description":"Updated description"}'

# Delete a category
curl -X DELETE http://localhost:8080/api/v1/categories/1
```

### Products

```bash
# Create a product (categoryIds may reference existing categories)
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Mechanical Keyboard",
        "description": "Clicky and loud",
        "price": 99.99,
        "stockQuantity": 25,
        "categoryIds": [1]
      }'

# Search/paginate/filter products
curl "http://localhost:8080/api/v1/products?name=keyboard&minPrice=50&maxPrice=150&page=0&size=10&sort=price,asc"

# Get a single product (cached in Redis after the first call)
curl http://localhost:8080/api/v1/products/1

# Update a product
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Mechanical Keyboard",
        "description": "Clicky and loud - now RGB",
        "price": 109.99,
        "stockQuantity": 20,
        "categoryIds": [1]
      }'

# Delete a product
curl -X DELETE http://localhost:8080/api/v1/products/1
```

### Image upload

```bash
# Upload a product image (multipart/form-data)
curl -X POST http://localhost:8080/api/v1/uploads/images \
  -F "file=@C:/path/to/keyboard.jpg"

# Response: {"fileName":"<uuid>.jpg","url":"/images/<uuid>.jpg","sizeBytes":12345}
# Fetch the stored image back:
curl http://localhost:8080/images/<uuid>.jpg -o downloaded.jpg
```

### Orders

```bash
# Place an order
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
        "customerEmail": "shopper@example.com",
        "items": [ { "productId": 1, "quantity": 2 } ]
      }'

# List orders (paginated)
curl "http://localhost:8080/api/v1/orders?page=0&size=10"

# Get a single order
curl http://localhost:8080/api/v1/orders/1

# Transition an order's status
curl -X PATCH "http://localhost:8080/api/v1/orders/1/status?status=CONFIRMED"

# Cancel an order (restores stock)
curl -X PATCH http://localhost:8080/api/v1/orders/1/cancel
```

### Actuator (operations endpoints)

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/caches
```

---

## 6. Key design decisions worth studying

1. **Records for DTOs, classes for entities.** DTOs are immutable data
   carriers with no identity beyond their values - a perfect fit for Java
   `record`s. Entities need mutable state, lazy-loading proxies, and
   identity-based equality, so they stay regular classes with Lombok
   `@Getter`/`@Setter`.
2. **No `@Data` on entities.** `@Data` generates `equals`/`hashCode` from
   every field, which is dangerous on JPA entities with collections and
   bidirectional relationships (see the Javadoc on `Category.equals`).
3. **Service interfaces + `impl` package.** Every service is defined as an
   interface (`ProductService`) with a single implementation
   (`service/impl/ProductServiceImpl`), injected into controllers via
   `@RequiredArgsConstructor`. This keeps controllers testable against a
   contract instead of a concrete class.
4. **BigDecimal for money.** `double`/`float` cannot exactly represent most
   decimal fractions and must never be used for currency; every price field
   uses `BigDecimal`.
