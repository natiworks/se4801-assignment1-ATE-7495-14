# ShopWave-Starter

> SE 4801 — Enterprise Application Development · Assignment 1 ·  
> **Name:** Natnael Nigatu  
> **Student Number:** ATE/7495/14


---



## Project Overview

**ShopWave-Starter** is a Spring Boot 3.x RESTful application implementing a basic product catalogue and order management system, built as the practical programming submission for Section C of SE 4801 Assignment 1.

The application covers:
- A JPA domain model with four entities (`Category`, `Product`, `Order`, `OrderItem`)
- A repository and service layer with pagination, search, and stock management
- A REST controller exposing CRUD endpoints for products
- Global exception handling returning structured JSON error responses
- Unit, slice, and integration tests using JUnit 5, Mockito, and Spring Test

---

## Technologies Used

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.x |
| Spring Data JPA | via Spring Boot |
| Spring Boot Actuator | via Spring Boot |
| H2 Database | in-memory |
| Lombok | latest |
| Maven | 3.9+ |
| JUnit 5 + Mockito | via Spring Boot |
| Testcontainers (PostgreSQL) | bonus |

---

## Project Structure

```
shopwave-starter/
├── src/
│   ├── main/java/com/shopwave/
│   │   ├── ShopwaveStarterApplication.java
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── mapper/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   ├── main/resources/
│   │   └── application.properties
│   └── test/java/com/shopwave/
│       ├── ProductControllerTest.java
│       ├── ProductServiceTest.java
│       ├── ProductRepositoryTest.java
│       ├── ProductRepositoryTestcontainersTest.java
│       └── ShopwaveStarterApplicationTests.java
├── pom.xml
└── README.md
```

---

## Prerequisites

- **Java 21** — verify with `java -version`
- **Maven 3.9+** — verify with `mvn -version`
- Internet connection on first build (Maven downloads dependencies)

---

## How to Build

```bash
git clone https://github.com/natiworks/se4801-assignment1-ATE-7495-14.git
cd se4801-assignment1-ATE-7495-14

mvn clean package -DskipTests
```

---

## How to Run

```bash
mvn spring-boot:run
```

Once started, the application is available at:

```
http://localhost:8080
```

Actuator health check:

```
http://localhost:8080/actuator/health
```

---

## Domain Model

Four JPA entities defined in `com.shopwave.model`:

| Entity | Key Fields | Relationships |
|---|---|---|
| `Category` | `id`, `name` (not blank), `description` | One-to-Many → `Product` |
| `Product` | `id`, `name`, `description`, `price` (positive), `stock` (min 0), `createdAt` | Many-to-One → `Category` |
| `Order` | `id`, `orderNumber` (unique), `status` (enum), `totalAmount`, `createdAt` | One-to-Many → `OrderItem` (orphanRemoval) |
| `OrderItem` | `id`, `quantity`, `unitPrice` | Many-to-One → `Product` |


## API Endpoints

Base path: `/api`

| Method | Path | Request | Success | Notes |
|---|---|---|---|---|
| `GET` | `/api/products` | `?page=0&size=10` | `200 Page<ProductDTO>` | Paginated |
| `GET` | `/api/products/{id}` | — | `200 ProductDTO` | `404` if not found |
| `POST` | `/api/products` | `CreateProductRequest` (JSON) | `201 ProductDTO` | Validated with `@Valid` |
| `GET` | `/api/products/search` | `?keyword=&maxPrice=` | `200 List<ProductDTO>` | Both params optional |
| `PATCH` | `/api/products/{id}/stock` | `{ "delta": -5 }` | `200 ProductDTO` | `400` if stock goes negative, `404` if not found |

### Error Response Format

```json
{
  "timestamp": "2026-03-27T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Product with id 999 not found",
  "path": "/api/products/999"
}
```

---

## How to Run Tests

```bash
mvn test
```

Run a specific test class:

```bash
mvn test -Dtest=ProductServiceTest
mvn test -Dtest=ProductControllerTest
mvn test -Dtest=ProductRepositoryTest
```

| Test Class | Type | What it tests |
|---|---|---|
| `ProductServiceTest` | Unit (Mockito) | `createProduct()` happy path; error path when category not found |
| `ProductControllerTest` | Slice (`@WebMvcTest`) | `GET /api/products` returns `200`; `GET /api/products/999` returns `404` with error JSON |
| `ProductRepositoryTest` | Integration (`@DataJpaTest`) | `findByNameContainingIgnoreCase` returns correct results |

---

## Sample API Responses

### GET /api/products

```bash
curl -X GET "http://localhost:8080/api/products?page=0&size=5"
```

```json
{
  "content": [
    {
      "id": 1,
      "name": "Wireless Headphones",
      "description": "Noise-cancelling over-ear headphones",
      "price": 129.99,
      "stock": 50,
      "categoryId": 1,
      "createdAt": "2026-03-27T09:00:00"
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 5 },
  "totalElements": 1,
  "totalPages": 1
}
```

### POST /api/products

```bash
curl -X POST "http://localhost:8080/api/products" \
  -H "Content-Type: application/json" \
  -d '{ "name": "Wireless Headphones", "description": "Noise-cancelling", "price": 129.99, "stock": 50, "categoryId": 1 }'
```

```json
{
  "id": 1,
  "name": "Wireless Headphones",
  "description": "Noise-cancelling",
  "price": 129.99,
  "stock": 50,
  "categoryId": 1,
  "createdAt": "2026-03-27T09:00:00"
}
```

### PATCH /api/products/1/stock

```bash
curl -X PATCH "http://localhost:8080/api/products/1/stock" \
  -H "Content-Type: application/json" \
  -d '{ "delta": -5 }'
```

```json
{
  "id": 1,
  "name": "Wireless Headphones",
  "stock": 45
}
```

### GET /api/products/999 (Not Found)

```bash
curl -X GET "http://localhost:8080/api/products/999"
```

```json
{
  "timestamp": "2026-03-27T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Product with id 999 not found",
  "path": "/api/products/999"
}
```

---

## Assignment Mapping (Section C)

| Requirement | Implementation |
|---|---|
| C1 Setup | Spring Boot project with `pom.xml` and `application.properties` |
| C2 Domain Model | `Category`, `Product`, `Order`, `OrderItem` entities + `OrderStatus` enum |
| C3 Service Layer | `ProductService` with business logic and repository integration |
| C4 REST Controller | `ProductController` and `GlobalExceptionHandler` |
| C5 Testing | Unit tests (Mockito), controller tests (`@WebMvcTest`), repository tests (`@DataJpaTest`) |

---

## Bonus — Testcontainers

A PostgreSQL Testcontainers-based test (`ProductRepositoryTestcontainersTest`) is implemented to validate repository behaviour against a real database instead of H2. This ensures compatibility with real-world database environments and satisfies the C5 bonus requirement. The PostgreSQL container startup is visible in the `mvn test` console output and is captured in the PDF report.

> ⚠️ **Docker must be running** on your machine before executing this test. Testcontainers spins up a real PostgreSQL container at test time — if Docker is not running, this test will be skipped or fail.

---

## Academic Integrity Declaration

I declare that this assignment is my own original work and has not been submitted for assessment in any other course.

AI tools (ChatGPT) were used in the following sections:
- **Section A:** Used for organizing and structuring the answer.
- **Section B (Java Fundamentals):** Used for some code generation and guidance when solving programming tasks.
- **Section C (Practical Task):** Used to assist with generating initial code structure (entities, repositories, services, controllers, DTOs) and providing implementation guidance.

All AI-assisted outputs were reviewed, modified, and verified by me. I ensured correctness by:
- Running the application and confirming successful startup
- Testing API endpoints using curl/Postman and verifying expected responses
- Running all unit, controller, and repository tests and confirming they pass
- Debugging and fixing errors independently

I confirm that I understand the submitted work and that the final implementation reflects my own verification and learning.
