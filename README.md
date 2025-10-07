# 📚 Library Management REST API

A simple **Library Management System** built with **Spring Boot**. The system provides REST API endpoints to manage **books**, **members** (patrons), and **loans** (borrowing transactions).

[![Ngôn ngữ](https://img.shields.io/github/languages/top/hoangle225/library-management-system)](link-to-repo)
[![Giấy phép](https://img.shields.io/github/license/hoangle225/library-management-system)](LICENSE)

## 🛠️ Tech Stack

* **Backend Framework:** Spring Boot 3
* **Persistentce:** Spring Data JPA & Hibernate
* **Database:** MySQL
* **Security:** Spring Security (JWT-based Authentication)
* **API Documentation:** OpenAPI 3 / Swagger UI
* **Language:** Java 17+

### 🔑 Prerequisites

Ensure you have the following tools installed:

* **Java Development Kit (JDK):** Version 17 or higher.
* **Maven** for project building.
* **MySQL Server** running locally.

### ⚙️ Database Setup

1.  Create a new MySQL database (e.g., `library_db`).
2.  Update the **`src/main/resources/application.properties`** file with your database connection details:

```properties
# In application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=update # Hibernate will automatically create the necessary tables
```

### 🚀 Running the Application
This section guides you on how to compile and run the application.
1.  **Clone Repository:**
    ```bash
    git clone https://github.com/hoangle225/library-management-system.git
    cd library-management-system
    ```

2.  **Build the Project with Maven:**
    ```bash
    mvn clean install
    ```

3.  **Run the Application:**
    ```bash
    mvn spring-boot:run
    ```

The application will be running on the default port (usually **`http://localhost:8080`**)

## 🌐 API Documentation (Swagger UI)

All API endpoints and data schemas are documented using **OpenAPI/Swagger**.

Once the application is running, you can access the documentation at:

**[http://localhost:8080/my-ui.html](http://localhost:8080/my-ui.html)**

## 🔒 Authentication

The application uses **JWT (JSON Web Tokens)** for securing endpoints.

**Registration/Login:**
* **POST** `/api/v1/auth/register`
* **POST** `/api/v1/auth/login`
* **Note:** The `endpoint` will return a **JWT Token**. You must include this token in the `Authorization: Bearer <token>` header for all other secured requests.

### 📖 Secured API Examples

* **Get all books:** `GET /api/v1/books` (Requires Token)
* **Add a new book:** `POST /api/v1/books` (Requires Token)
* **Borrow a book:** `POST /api/v1/loans` (Requires Token)

## 🤝 Contributing & License

* **Contributing:** All Pull Requests are welcome. Please read **CONTRIBUTING.md** (if available) before making a contribution.
* **License:** This project is released as a personal project and does not currently have an open-source license defined.
