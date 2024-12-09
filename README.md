# Backend Web Engineering Project
This project is a **REST API** for an online shop. It uses **Java Spring Boot**, **Docker**, **MySQL**, and **MinIO** for storage and demonstrates essential backend features such as authentication, role-based authorization, and CRUD operations.

---

## 📜 **Table of Contents**

1. [Project Overview](#-project-overview)
2. [Features](#-features)
3. [Technologies](#-technologies)
4. [Setup Instructions](#-setup-instructions)
5. [API Endpoints](#-api-endpoints)
6. [Container](#-container)
7. [Component Diagram](#-component-diagram)
8. [Authentication & Authorization](#-authentication--authorization)
9. [Testing](#-testing)
10. [License](#-license)
11. [Contact](#-contact)


---

## 🚀 **Project Overview**

This REST API allows users to:
- View products (user access only).
- Register and authenticate using JWT.
- Perform CRUD operations on products (admins only).
- Perform CRUD operations on users (admins only).

---

## 🔧 **Features**

- **Authentication**: JWT-based login and registration. ✅
- **Authorization**: Role-based access control (`USER`, `ADMIN`). ✅
- **CRUD Operations**: Admins can create, read, update, and delete products and users. ✅
- **File Uploads**: Supports uploading images/documents using MinIO. ❌
- **Validation**: Ensures data integrity with field-level validation. ✅
- **Docker Support**: Easy deployment using Docker and Docker Compose. ✅
- **Code Coverage**: 80%+ test coverage with unit tests. ❌

---

## 🛠️ **Technologies**

- **Java Spring Boot**
- **MariaDB** for relational database storage.
- **MinIO** for object storage (images/files).
- **Spring Security** for JWT-based authentication.
- **Docker** for containerization.

---

## 🛠️ **Setup Instructions**

### Prerequisites

- **Java 21**
- **Docker & Docker Compose**
- **Maven**

### Steps to Run the Project

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/hudidaut/bweng-project.git
   cd bweng-project
   ```

2. **Configure Environment**:

  - Update `src/main/resources/application.properties` with your database and MinIO credentials.

3. **Build and Run with Docker**:
   ```bash
   docker-compose up --build
   ```

4. **Access the API**:
  - API runs at: `http://localhost:8080`
  - Swagger UI: `http://localhost:8080/swagger-ui`

---

## 📡 **API Endpoints**

### Authentication

| Method | Endpoint         | Description                 | Access  |
|--------|------------------|-----------------------------|---------|
| `POST` | `/auth/register` | Register a new user         | Public  |
| `POST` | `/auth/login`    | Authenticate and get a JWT  | Public  |

### Products

| Method   | Endpoint    | Description              | Access     |
|----------|-------------|--------------------------|------------|
| `GET`    | `/products` | Get all products         | User/Admin |
| `GET`    | `/products/{id}` | Get a single product     | User/Admin |
| `POST`   | `/products` | Add a new product        | Admin      |
| `PATCH`  | `/products/{id}` | Update part of a product | Admin      |
| `PUT`    | `/products/{id}` | Update a product         | Admin      |
| `DELETE` | `/products/{id}` | Delete a product         | Admin      |

### Users

| Method   | Endpoint      | Description       | Access |
|----------|---------------|-------------------|-------|
| `GET`    | `/users`      | Get all users     | Admin |
| `GET`    | `/users/{id}` | Get a single user | Admin |
| `PUT`    | `/users/{id}` | Update a user     | Admin |
| `DELETE` | `/user/{id}`  | Delete a user     | Admin |
---

## 🐳 **Container**
* Spring Boot basic setup container
  * Port 8080
* MariaDB container
  * Port 3306
* MinIO container
  * Port 9000
  * Port 9001 (Dashboard)

## 📊 **Component Diagram**
![App Component Diagram](http://www.plantuml.com/plantuml/png/POxDIiL038NtUOfmz_SDHAwttRWGmJx1E1DhS9eCcTID-EwMbj8VTydv3dpdLZsOZqE6J1-EhcZSVpecDehEAW0XkXescKaSG3GHjXg_oF074ACEHML2UEcAiVHuLtLyAkKoytsZKN7JdCbEe2FxvaZr5BzHqSgknZFw1K1CmSDxg8GlmJYqzsF6ylmAKmzWsOiFr-lZthkTCzhCwx741_Fsh7Xr_oVBWXj96eVy1m00)

## 🔒 **Authentication & Authorization**

- **JWT Tokens** are used for secure authentication.
- **Roles**:
  - `ROLE_USER`: Can view products.
  - `ROLE_ADMIN`: Can add, edit, and delete products and users.

---

## ✅ **Testing**

Run unit tests using Maven:

```bash
mvn test
```

Ensure code coverage meets **80%** or higher.

---



## 📝 **License**

This project is licensed under the [MIT License](https://rem.mit-license.org).

---

## 📧 **Contact**

For questions or support, contact me:

- **Name**: Hudi Dautoski
- **Email**: wi21b053@technikum-wien.at
- **GitHub**: [hudidaut](https://github.com/hudidaut)

or my colleague

- **Name**: Ziad Abdalla
- **Email**: wi22b001@technikum-wien.at
- **GitHub**: [ziadabdalla7](https://github.com/ziadabdalla7)