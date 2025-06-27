# Project Tracker

A secure and comprehensive project management system that allows tracking of projects, tasks, and developers. This application provides a RESTful API for managing project resources with robust security features and audit logging.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Security Features](#security-features)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Configuration](#configuration)
- [Usage Examples](#usage-examples)
- [Development Guidelines](#development-guidelines)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Project Management**

  - Create, read, update, and delete projects
  - Project status tracking
  - Priority management
  - Timeline tracking

- **Task Management**

  - Task creation and assignment
  - Status tracking
  - Priority levels
  - Due date management

- **Developer Management**

  - Developer profiles
  - Skill tracking
  - Task assignment
  - Workload management

- **Security Features**

  - JWT-based authentication
  - Role-based access control
  - Password encryption
  - Session management

- **Audit & Logging**

  - Comprehensive audit logging
  - Change tracking
  - User activity monitoring
  - System events logging

- **API Features**
  - RESTful endpoints
  - Pagination support
  - Filtering and sorting
  - Swagger/OpenAPI documentation

## Architecture

The application follows a layered architecture pattern:

```
src/main/java/com/ndungutse/project_tracker/
├── config/         # Configuration classes
├── controller/     # REST API endpoints
├── dto/           # Data Transfer Objects
├── exception/     # Custom exception handlers
├── model/         # Entity classes
├── repository/    # Data access layer
├── security/      # Security configuration
└── service/       # Business logic layer
```

### Architecture Components

1. **Controller Layer**

   - Handles HTTP requests
   - Input validation
   - Response formatting
   - API documentation

2. **Service Layer**

   - Business logic implementation
   - Transaction management
   - Data validation
   - Cross-cutting concerns

3. **Repository Layer**

   - Data persistence
   - Database operations
   - Query optimization
   - Data access patterns

4. **Security Layer**
   - Authentication
   - Authorization
   - JWT token management
   - Security configurations

## Technology Stack

- **Backend**

  - Java 21
  - Spring Boot 3.5.0
  - Spring Security
  - Spring Data JPA
  - Spring Data MongoDB

- **Databases**

  - PostgreSQL 12+ (Primary)
  - MongoDB (Audit logging)

- **Tools & Libraries**
  - Maven
  - Swagger/OpenAPI
  - JWT
  - Lombok
  - Validation API

## Security Features

- **Authentication**

  - JWT-based authentication
  - Secure password storage with BCrypt
  - Token refresh mechanism
  - Session management

- **Authorization**

  - Role-based access control (RBAC)
  - Fine-grained permissions
  - API endpoint security
  - Resource-level authorization

- **Data Security**
  - Input validation
  - SQL injection prevention
  - XSS protection
  - CSRF protection

## Prerequisites

- Java 21 or higher
- PostgreSQL 12 or higher
- MongoDB 4.4 or higher
- Maven 3.6 or higher
- Git

## Setup and Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/yourusername/project-tracker.git
   cd project-tracker
   ```

2. **Configure the databases**

   - PostgreSQL:

     ```sql
     CREATE DATABASE project_tracker;
     CREATE USER project_user WITH ENCRYPTED PASSWORD 'your_password';
     GRANT ALL PRIVILEGES ON DATABASE project_tracker TO project_user;
     ```

   - MongoDB:
     ```bash
     mongod --dbpath /path/to/data/directory
     ```

3. **Configure application properties**

   - Copy `application.properties.example` to `application.properties`
   - Update database credentials and other configurations

4. **Build the application**

   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

## API Documentation

The API documentation is available via Swagger UI at:

```
http://localhost:3000/swagger-ui.html
```

### Authentication Endpoints

- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - User login

### Project Endpoints

- `POST /api/v1/projects` - Create project
- `GET /api/v1/projects` - List projects
- `GET /api/v1/projects/{id}` - Get project
- `PATCH /api/v1/projects/{id}` - Update project
- `DELETE /api/v1/projects/{id}` - Delete project

### Task Endpoints

- `POST /api/v1/tasks` - Create task
- `GET /api/v1/tasks` - List tasks
- `GET /api/v1/tasks/{id}` - Get task
- `PATCH /api/v1/tasks/{id}` - Update task
- `DELETE /api/v1/tasks/{id}` - Delete task

### Developer Endpoints

- `POST /api/v1/developers` - Create developer
- `GET /api/v1/developers` - List developers
- `GET /api/v1/developers/{id}` - Get developer
- `PATCH /api/v1/developers/{id}` - Update developer
- `DELETE /api/v1/developers/{id}` - Delete developer

## Database Schema

### PostgreSQL Schema

```sql
-- Projects Table
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(50),
    priority VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tasks Table
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT REFERENCES projects(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50),
    priority VARCHAR(50),
    assigned_to BIGINT,
    due_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Developers Table
CREATE TABLE developers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    skills TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Configuration

Key configuration properties in `application.properties`:

```properties
# Server Configuration
server.port=3000
server.servlet.context-path=/api/v1

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/project_tracker
spring.datasource.username=project_user
spring.datasource.password=your_password

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/audit_logs

# JWT Configuration
jwt.secret=your_jwt_secret
jwt.expiration=86400000

# Logging Configuration
logging.level.root=INFO
logging.level.com.ndungutse=DEBUG
```

## Development Guidelines

1. **Code Style**

   - Follow Google Java Style Guide
   - Use meaningful variable names
   - Add comments for complex logic
   - Keep methods small and focused

2. **Git Workflow**

   - Create feature branches
   - Write meaningful commit messages
   - Create pull requests for changes
   - Keep commits atomic

3. **Testing**
   - Write unit tests for services
   - Integration tests for controllers
   - Maintain test coverage above 80%

## Testing

Run tests using Maven:

```bash
mvn test
```

Run specific test class:

```bash
mvn test -Dtest=ProjectServiceTest
```

## Deployment

1. **Build the application**

   ```bash
   mvn clean package
   ```

2. **Run the JAR file**

   ```bash
   java -jar target/project-tracker-1.0.0.jar
   ```
