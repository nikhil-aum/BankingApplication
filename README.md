# Bank Management System

A secure RESTful banking backend application built using **Spring Boot**, **Spring Security**, **JWT Authentication**, **Spring Data JPA**, and **MySQL**. The application provides customer registration and login along with authenticated banking operations such as account creation, deposit, withdrawal, fund transfer, and balance checking.

## Description

The **Bank Management System** is a backend web service developed as part of a Spring Boot banking application project. The application converts traditional banking operations into REST APIs and provides secure access through **JWT-based authentication**.

Customers can register using their email and password and securely log in to receive a JWT token. The token must be provided as a Bearer token when accessing protected banking operations.

The application supports the following major operations:
* JWT-based authentication
* Customer registration
* Customer login using email and password
* Authenticated account creation
* Account balance checking
* Deposit money into an account
* Withdraw money from an account
* Transfer money between accounts
* Account ownership validation
* Input validation using Jakarta Bean Validation
* Global exception handling
* Swagger UI API documentation
* MySQL database persistence
* REST API testing using Postman
* Unit/controller test cases using JUnit and Mockito

The project follows a layered architecture where:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL Database
```

Controllers are responsible for handling HTTP requests and responses, while business rules are implemented inside the service layer.

### Security

Passwords are stored using **BCrypt hashing** instead of plain text. JWT is used to authenticate users after login.

Protected requests require:

```text
Authorization: Bearer <JWT_TOKEN>
```

Sensitive information such as passwords, JWT tokens, and other credentials is not exposed in API responses or application logs.

### Main Banking Rules

The application maintains important banking rules such as:

* Deposit amount must be positive.
* Withdrawal amount must be positive.
* Account balance cannot become negative.
* Withdrawal is rejected when sufficient funds are unavailable.
* A customer cannot access another customer's account.
* A customer cannot transfer money to the same account.
* Transfer operations validate the required confirmation details.
* Duplicate customer registration is rejected.
* Invalid authentication credentials are handled securely.
* Unauthorized requests are rejected with HTTP `401`.
* Authenticated users attempting to access another user's account receive HTTP `403`.

## Getting Started

### Dependencies

The following software and dependencies are required to run the project:

* **Java JDK 17 or later**
* **Spring Boot**
* **Spring Web**
* **Spring Data JPA**
* **Hibernate**
* **MySQL**
* **MySQL Workbench** (optional, for database management)
* **Spring Security**
* **JWT**
* **Spring Boot Validation**
* **springdoc-openapi / Swagger UI**
* **JUnit 5**
* **Mockito**
* **Maven**
* **Postman**
* **IntelliJ IDEA / Eclipse / VS Code** (any Java IDE)

### Technologies Used

| Technology      | Purpose                             |
| --------------- | ----------------------------------- |
| Java            | Application development             |
| Spring Boot     | Backend framework                   |
| Spring Web      | REST API development                |
| Spring Data JPA | Database access                     |
| Hibernate       | ORM                                 |
| MySQL           | Relational database                 |
| Spring Security | Authentication and authorization    |
| JWT             | Stateless authentication            |
| BCrypt          | Password hashing                    |
| Validation      | Request validation                  |
| Swagger UI      | API documentation and testing       |
| JUnit 5         | Unit testing                        |
| Mockito         | Mocking dependencies during testing |
| Maven           | Dependency and project management   |
| Postman         | REST API testing                    |

### Installing

1. Clone or download the project from the repository.

```bash
git clone <your-repository-url>
```

2. Open the project in your preferred Java IDE.

3. Create a MySQL database.

```sql
CREATE DATABASE bank_management_system;
```

4. Configure the database connection in `application.properties` or `application.yml`.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bank_management_system
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

5. Configure the JWT secret using application configuration or an environment variable.

```properties
jwt.secret=your-secret-key
```

**Do not commit real database passwords or JWT secrets to the repository.**

6. Install Maven dependencies.

```bash
mvn clean install
```

### Executing program

1. Start the MySQL server.

2. Make sure the database configuration is correct.

3. Start the Spring Boot application from the IDE or using Maven:

```bash
mvn spring-boot:run
```

4. Once the application starts successfully, the REST APIs can be tested using Postman.

5. Swagger UI can be accessed through:

```text
http://localhost:8080/swagger-ui/index.html
```

or:

```text
http://localhost:8080/swagger-ui.html
```

---

## API Flow

### 1. Register Customer

```http
POST /api/customers/register
```

Register a new customer using their email and password.

Example request:

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

A customer with an already registered email cannot register again.

Expected response:

```text
201 Created
```

---

### 2. Login

```http
POST /api/auth/login
```

Login using the registered email and password.

Example:

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

On successful authentication, the application generates a JWT token.

The token is then used to access protected APIs.

```text
Authorization: Bearer <JWT_TOKEN>
```

Invalid login attempts return a generic authentication error instead of revealing whether the email or password was incorrect.

---

### 3. Create Account

```http
POST /api/accounts
```

This endpoint requires authentication.

The JWT token must be included in the request.

```text
Authorization: Bearer <JWT_TOKEN>
```

The account is created for the currently authenticated customer.

Expected response:

```text
201 Created
```

---

### 4. Check Account Balance

```http
GET /api/accounts/{accountNumber}
```

Returns account details and balance for the authenticated account owner.

Example:

```http
GET /api/accounts/100001
```

A customer cannot access another customer's account.

Possible responses:

```text
200 OK
401 Unauthorized
403 Forbidden
404 Not Found
```

---

### 5. Deposit Money

```http
POST /api/accounts/{accountNumber}/deposit
```

Adds money to the authenticated customer's account.

Example:

```http
POST /api/accounts/100001/deposit
```

Example request:

```json
{
  "amount": 5000
}
```

The deposit amount must be positive.

---

### 6. Withdraw Money

```http
POST /api/accounts/{accountNumber}/withdraw
```

Withdraws money from the authenticated customer's account.

Example:

```http
POST /api/accounts/100001/withdraw
```

Example request:

```json
{
  "amount": 1000
}
```

The application verifies that:

* The amount is positive.
* The account exists.
* The authenticated customer owns the account.
* Sufficient funds are available.

The account balance can never become negative.

---

### 7. Transfer Money

```http
POST /api/accounts/transfer
```

Transfers money from one account to another.

Example request:

```json
{
  "fromAccount": 100001,
  "toAccount": 100002,
  "amount": 1000,
  "confirmation": "1000"
}
```

The transfer validates important business rules including:

* Source account exists.
* Destination account exists.
* Source account belongs to the authenticated customer.
* Transfer amount is positive.
* Source account has sufficient funds.
* Source and destination accounts are different.
* Required transfer confirmation matches.

---

## Authentication Flow

The authentication flow works as follows:

```text
Register
   ↓
Customer stored in MySQL
   ↓
Password hashed using BCrypt
   ↓
Login using Email + Password
   ↓
Spring Security validates credentials
   ↓
JWT Token Generated
   ↓
Client stores token
   ↓
Client sends Bearer Token
   ↓
JWT Filter validates token
   ↓
Authenticated User
   ↓
Protected Banking APIs
```

### Example Authorization Header

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## HTTP Status Codes

The application uses appropriate HTTP status codes for API responses.

| Status Code        | Meaning                                                  |
| ------------------ | -------------------------------------------------------- |
| `200 OK`           | Request completed successfully                           |
| `201 Created`      | New customer/account successfully created                |
| `400 Bad Request`  | Invalid request or validation failure                    |
| `401 Unauthorized` | Missing or invalid authentication                        |
| `403 Forbidden`    | Authenticated user does not own the requested resource   |
| `404 Not Found`    | Requested resource does not exist                        |
| `409 Conflict`     | Duplicate customer registration or conflicting operation |

---

## Validation and Exception Handling

Request DTOs use Jakarta Bean Validation annotations such as:

```java
@NotNull
@NotBlank
@Email
@Positive
@Size
```

Invalid requests are rejected before they reach the business/service layer.

The application also uses a global exception handler through:

```java
@RestControllerAdvice
```

This converts application exceptions into clean JSON responses instead of exposing Java stack traces.

Examples of handled exceptions include:

* Account not found
* Customer not found
* Insufficient funds
* Invalid transfer
* Duplicate customer
* Unauthorized access
* Validation errors

---

## Swagger UI

Swagger UI is integrated using **springdoc-openapi**.

Swagger provides documentation for the available REST APIs and allows the APIs to be tested directly from the browser.

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

The API documentation includes the available banking endpoints and their request/response information.

For protected endpoints, the JWT Bearer token can be provided through Swagger's authorization option.

Example:

```text
Bearer <JWT_TOKEN>
```

---

## Testing

The application contains test cases for the implemented Controller/API functionality using:

* **JUnit 5**
* **Mockito**
* **Spring Boot Test**
* **MockMvc**


### Example Test Flow

```text
MockMvc
   ↓
Controller
   ↓
Mock Service
   ↓
Expected HTTP Response
```

Test cases cover scenarios such as:

* Customer registration
* Login behavior
* Account creation
* Balance checking
* Deposit
* Withdrawal
* Transfer
* Authentication
* Authorization
* Invalid requests
* Unauthorized access
* Service exceptions
* HTTP status code validation

---

## Database

The application uses **MySQL** for persistent data storage.

The main entities include:

```text
Customer
   │
   │ 1
   │
   │ *
 Account
   │
   │ 1
   │
   │ *
Transaction
```

### Customer

Stores customer information such as:

* Customer ID
* Name
* Email
* Hashed password
* Created date

The customer's email is unique and is used for login.

### Account

Stores information such as:

* Account number
* Balance
* Account owner
* Created date

### Transaction

Stores transaction information such as:

* Transaction ID
* Transaction type
* Amount
* Timestamp
* Associated account

Money values are represented using `BigDecimal` instead of `double` to avoid floating-point precision problems when handling currency.

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.yourname.minibank
│   │       ├── controller
│   │       ├── service
│   │       ├── repository
│   │       ├── entity
│   │       ├── dto
│   │       ├── security
│   │       ├── exception
│   │       ├── config
│   │       └── MiniBankApplication.java
│   │
│   └── resources
│       ├── application.properties
│       └── ...
│
└── test
    └── java
        └── com.yourname.minibank
            ├── controller
            ├── service
            └── ..
```

### Layer Responsibilities

**Controller**

Handles HTTP requests and responses.

```text
Request → Controller → Service → Response
```

Controllers do not contain banking business logic.

**Service**

Contains the main banking rules and business logic.

Examples:

* Insufficient funds checking
* Ownership checking
* Transfer validation
* Deposit/withdrawal rules

**Repository**

Handles database operations using Spring Data JPA.

**Entity**

Represents database tables.

**DTO**

Defines the data exchanged through the REST API.

Entities are not directly exposed through REST endpoints.

**Security**

Contains JWT authentication, JWT filtering, Spring Security configuration, and user authentication logic.

**Exception**

Contains custom exceptions and global exception handling.

---

## Help

### 1. `401 Unauthorized`

If a protected endpoint returns:

```text
401 Unauthorized
```

check that:

* You have logged in successfully.
* The JWT token is valid.
* The token has not expired.
* The Authorization header is present.

Example:

```http
Authorization: Bearer <JWT_TOKEN>
```

### 2. `403 Forbidden`

If you receive:

```text
403 Forbidden
```

the user may be authenticated but does not have permission to access the requested resource.

For example, attempting to access another customer's account.

### 3. MySQL Connection Error

Check:

```properties
spring.datasource.url
spring.datasource.username
spring.datasource.password
```

Also make sure MySQL is running.

### 4. JWT Authentication Error

Check:

* JWT secret configuration
* Token expiration
* `Authorization` header
* `Bearer` prefix
* JWT filter configuration

### 5. Swagger Not Opening

Make sure the Spring Boot application is running and try:

```text
http://localhost:8080/swagger-ui/index.html
```

### 6. Validation Error

Check that the request body contains all required fields and follows the validation rules.

For example, an amount should not be:

```json
{
  "amount": -100
}
```

because transaction amounts must be positive.

---

## Authors

**Nikhil Patidar**

Bank Management System — Spring Boot Backend Project

Technologies used:

* Java
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* Hibernate
* MySQL
* Swagger UI
* JUnit
* Mockito
* Postman
* Maven

---

## Version History

### 0.2 — Current Version

* Added Spring Boot REST API
* Added MySQL database integration
* Added Spring Data JPA
* Added customer registration
* Added JWT-based authentication
* Added secure password hashing using BCrypt
* Added login using email and password
* Added authenticated account creation
* Added account balance checking
* Added deposit functionality
* Added withdrawal functionality
* Added money transfer functionality
* Added ownership validation
* Added request validation
* Added global exception handling
* Added Swagger UI documentation
* Added Controller/API test cases using JUnit and Mockito
* Added Postman API testing

### 0.1

* Initial banking application implementation
* Basic banking operations and business rules

---

## License

This project is developed as an educational banking backend project.

Unless otherwise specified, the project can be used and modified for learning and educational purposes.

---

## Acknowledgments

* Spring Boot documentation and community
* Spring Security documentation
* Spring Data JPA and Hibernate
* MySQL
* Swagger / OpenAPI
* JUnit and Mockito
* Postman
* Spring Boot ecosystem and community resources
