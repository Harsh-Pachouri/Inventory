# Inventory Management API 📦

This project is a comprehensive RESTful API for inventory management, built with the Spring Boot framework. It features full CRUD functionality for products and suppliers and includes an advanced AI-powered endpoint that translates natural language questions into secure, validated SQL queries.

This project was built to demonstrate a full software development lifecycle, from core application logic to testing, performance optimization, containerization, and CI/CD.

## Key Features

* **CRUD Operations:** Full Create, Read, Update, Delete functionality for Products and Suppliers.
* **Relational Data:** Manages a Many-to-One relationship between Products and Suppliers.
* **AI-Powered Queries:** A `POST /api/query` endpoint that uses a Large Language Model (LLM) to convert natural language questions into safe SQL.
* **Robustness:** Includes a comprehensive suite of unit tests (**JUnit/Mockito**) and integration tests (**Testcontainers**).
* **Performance:** Implements **Redis** caching for high-performance reads on frequently accessed data.
* **Portability:** The entire application and its services are containerized with **Docker**.
* **Automation:** A complete **CI/CD pipeline** with GitHub Actions automatically builds and tests the application on every push.

## Technology Stack

| Category              | Technology                                                              |
| --------------------- | ----------------------------------------------------------------------- |
| **Backend** | Java 17, Spring Boot 3, Spring Data JPA, Spring WebFlux (for WebClient) |
| **Database** | PostgreSQL, Hibernate (ORM), Flyway (Migrations), JdbcTemplate          |
| **Cache** | Redis, Spring Cache                                                     |
| **Testing** | JUnit 5, Mockito, Testcontainers                                        |
| **DevOps** | Docker, Docker Compose, GitHub Actions                                  |
| **AI Integration** | Groq API (LLM)                                                          |
| **Build Tool** | Maven                                                                   |

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

* Java Development Kit (JDK) 17 or newer
* Docker and Docker Compose
* An API Key from [Groq](https://console.groq.com/keys) or another LLM provider

### Local Setup

1.  **Clone the repository:**
    ```bash
    git clone [link to this repository]
    cd YourRepoName
    ```

2.  **Configure Environment Variables:**
    Create a file named `.env` in the root of the project and add your API key:
    ```env
    GROQ_API_KEY=your_api_key_goes_here
    ```

3.  **Start Services with Docker Compose:**
    The easiest way to run the required PostgreSQL and Redis instances is with Docker Compose.
    ```bash
    docker-compose up -d
    ```
    This command will start the database and cache in the background.

4.  **Run the Application:**
    You can run the application using the Maven wrapper:
    ```bash
    ./mvnw spring-boot:run
    ```
    The API will be available at `http://localhost:8080`.

### Running Tests

To run the full suite of unit and integration tests (which will use Testcontainers to spin up its own temporary databases), run:
```bash
./mvnw test
