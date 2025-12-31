# AI-Powered Inventory Management System (Full Stack)

A robust Full Stack application for inventory management, combining a **Spring Boot** backend with a modern **React** frontend. This project features secure REST endpoints, a relational database schema, high-performance caching, and an advanced **AI-powered assistant** that translates natural language questions into executable SQL queries.

The entire application is containerized and includes a complete CI/CD pipeline with automated integration testing for production readiness.

## Key Features

* **Full Stack Architecture**: A decoupled system with a responsive React dashboard consuming a Spring Boot REST API.
* **AI-Powered Assistant**: An interactive chat interface that securely translates natural language (e.g., "Which items are low on stock?") into SQL queries using **Groq (Llama 3)**.
* **Live Inventory Dashboard**: Real-time view of stock levels with sortable columns (ID, Name, Quantity, Price) and visual stock status indicators.
* **Relational Database Schema**: Uses PostgreSQL with JPA/Hibernate for object-relational mapping between `Product` and `Supplier` entities.
* **Database Migrations**: Leverages Flyway for version-controlled, automated database schema changes, ensuring consistency across all environments.
* **High-Performance Caching**: Integrates Redis to cache database query results, significantly reducing latency on frequent requests.
* **Automated Testing**: A comprehensive test suite using **JUnit** and **Mockito** for unit tests, and **Testcontainers** for reliable, self-contained integration tests.
* **CI/CD Pipeline**: A GitHub Actions workflow automatically builds and runs the full test suite on every push.
* **Cloud Ready**: Configured for deployment on platforms like Render (Backend) and Vercel (Frontend), with Docker support for local development.

## Tech Stack

| Category | Technology |
| :--- | :--- |
| **Frontend** | React, Vite, Tailwind CSS, Lucide React, Axios |
| **Backend** | Java 17, Spring Boot, Spring Data JPA, Hibernate, Spring WebFlux |
| **Database** | PostgreSQL, Redis, Flyway |
| **DevOps** | Docker, Docker Compose, GitHub Actions |
| **AI** | Groq API (Llama-3-70b-Versatile) |

## System Architecture

The application runs as a multi-container stack. The Spring Boot API orchestrates data persistence and AI logic, while the React Frontend provides the user interface.

`User (React UI) -> Spring Boot API -> (1. AIService -> Groq LLM), (2. Repository -> Redis Cache / PostgreSQL DB)`

## Getting Started

Follow these instructions to get the full stack up and running locally.

### Prerequisites

* Java Development Kit (JDK) 17+
* Node.js 18+ and npm
* Docker and Docker Compose
* A Groq API Key

### Installation

1.  **Clone the repository:**
    ```bash
    git clone [link to this repository]
    cd inventory-fullstack
    ```

2.  **Configure Environment:**
    Create a file named `.env` in the root directory (or set environment variables) for your AI API Key:
    ```
    GROQ_API_KEY=your_secret_key_here
    ```

3.  **Start Database & Cache:**
    Use Docker Compose to spin up PostgreSQL and Redis instantly.
    ```bash
    docker-compose up -d
    ```

### Running the Backend

1.  Open a terminal in the root directory.
2.  Run the Spring Boot application using the Maven wrapper:
    ```bash
    ./mvnw clean spring-boot:run
    ```
    *The Backend API will start at `http://localhost:8080`*

### Running the Frontend

1.  Open a **new terminal**.
2.  Navigate to the UI folder:
    ```bash
    cd inventory-ui
    ```
3.  Install dependencies and start the dev server:
    ```bash
    npm install
    npm run dev
    ```
4.  Open your browser and visit the URL shown (usually `http://localhost:5173`).

## API Endpoints

| Method | Path             | Description |
| :----- | :--------------- | :---------- |
| `POST` | `/api/query`     | **AI Endpoint**: Accepts natural language, returns data or chat response. |
| `GET`  | `/api/products`  | Retrieves all products. |
| `POST` | `/api/products`  | Creates a new product. |
| `GET`  | `/api/suppliers` | Retrieves all suppliers. |
| `POST` | `/api/suppliers` | Creates a new supplier. |

## Running Tests

To run the complete suite of backend unit and integration tests locally, use the following command. The integration tests will use Testcontainers to automatically start and stop their own temporary database and cache.

```bash
./mvnw test
