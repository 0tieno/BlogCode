# Blogging Platform API

A simple RESTful API for a personal blogging platform built with Spring Boot and PostgreSQL.

> **Project Reference:** https://roadmap.sh/projects/blogging-platform-api
>
> **GitHub Repository:** https://github.com/0tieno/BlogCode/tree/main/Mentorship/blogging-platform-api

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
  - [Create a Blog Post](#create-a-blog-post)
  - [Get All Blog Posts](#get-all-blog-posts)
  - [Get a Single Blog Post](#get-a-single-blog-post)
  - [Update a Blog Post](#update-a-blog-post)
  - [Delete a Blog Post](#delete-a-blog-post)
- [Error Handling](#error-handling)
- [Project Structure](#project-structure)

---

## Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Language     | Java 25                             |
| Framework    | Spring Boot 4.0.6                   |
| Database     | PostgreSQL                          |
| ORM          | Spring Data JPA / Hibernate         |
| Validation   | Jakarta Bean Validation             |
| Build Tool   | Maven (Maven Wrapper included)      |
| Utilities    | Lombok                              |

---

## Features

- Create, Read, Update, and Delete blog posts
- Filter/search posts by keyword (title, content, or category)
- Input validation with meaningful error messages
- Proper HTTP status codes and structured error responses

---

## Prerequisites

Make sure you have the following installed:

- **Java 25** (or a compatible JDK)
- **Maven 3.8+** (or use the included `mvnw` wrapper)
- **PostgreSQL** (running locally or accessible remotely)

---

## Getting Started

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd blogging-platform-api
```

### 2. Set Up the Database

Create a PostgreSQL database:

```sql
CREATE DATABASE userdb;
```

> The default configuration expects a database named `userdb`. See [Configuration](#configuration) to change this.

### 3. Configure the Application

Update `src/main/resources/application.properties` with your database credentials (see [Configuration](#configuration)).

### 4. Run the Application

Using the Maven wrapper (no Maven installation required):

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

The API will start on **http://localhost:8080**.

---

## Configuration

Edit `src/main/resources/application.properties` to match your environment:

```properties
spring.application.name=blogging-platform-api

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/userdb
spring.datasource.username=postgres
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

> The `ddl-auto=update` setting will automatically create/update the database schema on startup.

---

## API Endpoints

**Base URL:** `http://localhost:8080/api/v1`

---

### Create a Blog Post

**`POST /posts`**

Creates a new blog post.

**Request Body:**
```json
{
  "title": "My First Blog Post",
  "content": "This is the content of my first blog post.",
  "category": "Technology",
  "tags": ["Tech", "Programming"]
}
```

**Validation Rules:**
| Field      | Rule                                      |
|------------|-------------------------------------------|
| `title`    | Required, max 100 characters              |
| `content`  | Required                                  |
| `category` | Required                                  |
| `tags`     | Required, must not be empty, no blank tags|

**Response `201 Created`:**
```json
{
  "id": 1,
  "title": "My First Blog Post",
  "content": "This is the content of my first blog post.",
  "category": "Technology",
  "tags": ["Tech", "Programming"],
  "createdAt": "2021-09-01T12:00:00",
  "updatedAt": "2021-09-01T12:00:00"
}
```

**Response `400 Bad Request`** – when validation fails.

---

### Get All Blog Posts

**`GET /posts`**

Returns all blog posts. Supports optional keyword filtering.

**Query Parameters:**

| Parameter | Type   | Required | Description                                             |
|-----------|--------|----------|---------------------------------------------------------|
| `term`    | string | No       | Wildcard search on `title`, `content`, or `category`    |

**Examples:**
```
GET /api/v1/posts
GET /api/v1/posts?term=tech
```

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "title": "My First Blog Post",
    "content": "This is the content of my first blog post.",
    "category": "Technology",
    "tags": ["Tech", "Programming"],
    "createdAt": "2021-09-01T12:00:00",
    "updatedAt": "2021-09-01T12:00:00"
  }
]
```

---

### Get a Single Blog Post

**`GET /posts/{id}`**

Returns a single blog post by its ID.

**Response `200 OK`:**
```json
{
  "id": 1,
  "title": "My First Blog Post",
  "content": "This is the content of my first blog post.",
  "category": "Technology",
  "tags": ["Tech", "Programming"],
  "createdAt": "2021-09-01T12:00:00",
  "updatedAt": "2021-09-01T12:00:00"
}
```

**Response `404 Not Found`** – when the post with the given ID does not exist.

---

### Update a Blog Post

**`PUT /posts/{id}`**

Updates an existing blog post by its ID.

**Request Body:**
```json
{
  "title": "My Updated Blog Post",
  "content": "This is the updated content of my first blog post.",
  "category": "Technology",
  "tags": ["Tech", "Programming"]
}
```

**Response `200 OK`:**
```json
{
  "id": 1,
  "title": "My Updated Blog Post",
  "content": "This is the updated content of my first blog post.",
  "category": "Technology",
  "tags": ["Tech", "Programming"],
  "createdAt": "2021-09-01T12:00:00",
  "updatedAt": "2021-09-01T12:30:00"
}
```

**Response `400 Bad Request`** – when validation fails.  
**Response `404 Not Found`** – when the post with the given ID does not exist.

---

### Delete a Blog Post

**`DELETE /posts/{id}`**

Deletes an existing blog post by its ID.

**Response `204 No Content`** – when the post is successfully deleted.  
**Response `404 Not Found`** – when the post with the given ID does not exist.

---

## Error Handling

All errors return a structured JSON response:

```json
{
  "message": "Post Not found",
  "status": 404,
  "timestamp": "2021-09-01T12:00:00"
}
```

| HTTP Status | Scenario                              |
|-------------|---------------------------------------|
| `400`       | Validation errors on request body     |
| `404`       | Requested resource not found          |
| `500`       | Unexpected internal server error      |

---

## Project Structure

```
src/main/java/com/kampuni/blogging_platform_api/
├── BloggingPlatformApiApplication.java   # Application entry point
├── controller/
│   └── PostController.java               # REST endpoints
├── dto/
│   ├── PostRequestDto.java               # Request payload (with validation)
│   └── PostResponseDto.java              # Response payload
├── entity/
│   └── Post.java                         # JPA entity
├── exception/
│   ├── ErrorResponse.java                # Error response structure
│   ├── GlobalExceptionHandler.java       # Centralized exception handling
│   └── ResourceNotFoundException.java   # Custom 404 exception
├── repository/
│   └── PostRespository.java              # Spring Data JPA repository
└── service/
    ├── PostService.java                  # Service interface
    └── PostServiceImpl.java              # Service implementation
```

