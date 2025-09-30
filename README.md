#  Inventory Management System API

A backend API built with **Spring WebFlux** and **PostgreSQL** to manage products in a warehouse. 
Supports CRUD operations, inventory management, and low-stock alerts.

---

## 🚀 Features

- **Product Management (CRUD)**
   - Create, Read, Update, Delete products
  - Each product has: `name`, `description`, `stock_quantity`, `low_stock_threshold`

- **Inventory Logic**
   - Increase stock (`/products/{id}/increase-stock`)
  - Decrease stock with validation (cannot go below 0)
  - Proper error handling (e.g., 400 Bad Request for invalid operations)

- **Bonus ✨**
   - Low stock alert: `/products/low-stock`
   - Unit and integration tests for CRUD + stock management

---

## 🛠 Tech Stack

- **Java 17+**
- **Spring Boot (WebFlux)**
- **R2DBC + PostgreSQL**
- **JUnit 5 + Mockito + WebTestClient**

---

## 📂 Database Design

**Table: `products`**

| Column                | Type        | Notes                         |
|-----------------------|-------------|--------------------------------|
| `id` (PK)             | BIGSERIAL   | Auto-generated ID             |
| `name`                | VARCHAR     | Product name (not null)       |
| `description`         | TEXT        | Optional description          |
| `stock_quantity`      | INT         | Must be >= 0                  |
| `low_stock_threshold` | INT         | Threshold for low-stock alert |

---
## API Endpoints base Url is http://localhost:8080

### Product Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/product` | Get all products |
| `POST` | `/product` | Create new product |
| `PUT` | `/product/{id}` | Update product |
| `DELETE` | `/product/{id}` | Delete product |

### Inventory Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/products/{id}/increase-stock?quantity={amount}` | Increase stock quantity |
| `POST` | `/products/{id}/decrease-stock?quantity={amount}` | Decrease stock quantity |

### Low Stock Alerts

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/products/low-stock` | Get products below threshold |

## Requirements
### java 17+
### Postgres

## ⚙️ Setup & Run

### 1️ Clone the repository
```bash
git clone https://github.com/Jawad4632/Apis.git
cd Apis
```
### 2️ Set Up PostgreSQL Database
```bash
-- Create a new PostgreSQL user
CREATE ROLE inventory WITH superuser LOGIN PASSWORD 'invent';

--Alter the role of user
ALTER ROLE inventory CREATEDB CREATEROLE;

-- Create a new database and assign ownership to the user
CREATE DATABASE assessment WITH OWNER = inventory;
```
### 3️ Run the Spring Boot Application (Gradle)
```bash
# Run with Gradle Wrapper (Unix/macOS)
./gradlew bootRun

# OR on Windows
gradlew bootRun

```
OR build and run the JAR manually:
```bash
# Build the project
./gradlew clean build

# Run the generated JAR (usually inside build/libs/)
java -jar build/libs/Apis-0.0.1-SNAPSHOT.jar
```
## Video Link
https://drive.google.com/file/d/1z6KqsFxA7DXfebMGDUQrcg9NsMW8Wkxi/view?usp=sharing
