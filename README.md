# Bank Management System

A secure RESTful banking backend application built using **Spring Boot 4**, **Spring Web MVC**, **Spring Security**, **JWT Authentication**, **Spring Data JPA**, **Hibernate**, and **MySQL**. The application provides customer registration and login along with authenticated banking operations such as account creation, balance checking, deposit, withdrawal, fund transfer, and transaction-history filtering.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Business Rules](#business-rules)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Database Design](#database-design)
- [Entity Relationships](#entity-relationships)
- [Security](#security)
- [Authentication Flow](#authentication-flow)
- [REST API](#rest-api)
- [API Details](#api-details)
- [Transaction History APIs](#transaction-history-apis)
- [Validation](#validation)
- [Exception Handling](#exception-handling)
- [HTTP Status Codes](#http-status-codes)
- [Swagger / OpenAPI](#swagger--openapi)
- [Logging](#logging)
- [Configuration](#configuration)
- [Environment Variables](#environment-variables)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Testing Flow](#api-testing-flow)
- [Testing](#testing)
- [Important Implementation Notes](#important-implementation-notes)
- [Common Errors](#common-errors)
- [Example API Flow](#example-api-flow)
- [Future Improvements](#future-improvements)
- [Author](#author)
- [Version History](#version-history)
- [License](#license)

---

# Project Overview

The **Bank Management System** is a Spring Boot backend application that exposes banking operations through REST APIs.

The application uses:

- Spring Boot 4.0.7
- Spring Web MVC
- Spring Data JPA
- Hibernate
- MySQL
- Spring Security
- JWT
- BCrypt
- Jakarta Bean Validation
- Swagger/OpenAPI
- JUnit 5
- Mockito
- MockMvc
- Maven
- Lombok

The application follows a layered architecture:

```text
Client
   │
   ▼
REST Controller
   │
   ▼
Service Layer
   │
   ▼
Repository Layer
   │
   ▼
Hibernate / JPA
   │
   ▼
MySQL Database
```

Authentication is handled using a stateless JWT-based security configuration:

```text
HTTP Request
     │
     ▼
JWT Filter
     │
     ├── Valid JWT ───────► Authenticated Principal
     │
     └── Invalid JWT ─────► Request continues without authentication
                                      │
                                      ▼
                             Protected endpoint
                                      │
                                      ▼
                              401 Unauthorized
```

---

# Features

## Customer Management

- Register a new customer
- Unique email validation at the database level
- Secure password storage using BCrypt
- Login using email and password
- JWT token generation after successful login
- Generic invalid-login response

## Account Management

- Open a new bank account
- Support for `SAVING` and `CURRENT` account types
- Generate a 12-digit account number
- Check account balance
- View all accounts belonging to the logged-in customer
- Prevent a customer from viewing another customer's account through account/balance and transaction-history services
- Prevent the same customer from creating more than one account of the same account type

## Banking Operations

- Deposit money
- Withdraw money
- Transfer money between accounts
- Record successful and failed transactions
- Store balance after every transaction
- Maintain transaction descriptions and timestamps

## Transaction History

The application provides transaction-history APIs for:

- All transactions for an account
- Deposit transactions
- Withdrawal transactions
- Transfer-in transactions
- Transfer-out transactions
- Transactions where the balance after transaction is below a supplied amount
- Transactions within a supplied time range
- Transactions filtered by status

## Validation

- Required account numbers
- Confirmation account numbers
- Positive transaction amounts
- Valid customer email
- Customer name format
- Minimum password length
- Required account type

## API Documentation

- OpenAPI documentation
- Swagger UI
- Swagger authorization support through the generated JWT

## Error Handling

- Global exception handling using `@RestControllerAdvice`
- Custom banking exceptions
- Validation error handling
- Invalid credentials handling
- Account ownership handling
- Account-not-found handling
- Invalid enum/date/time input handling

## Testing

The project contains:

- JUnit 5 tests
- Mockito-based service tests
- Spring Boot tests
- MockMvc controller tests

---

# Business Rules

## Customer Registration

- Customer name is required.
- Customer name can contain only letters and spaces.
- Email must be valid.
- Password must contain at least 6 characters.
- Email must be unique.
- Duplicate email registration returns `409 Conflict`.
- Password is encoded using BCrypt before being stored.

## Account Creation

- Account type is required.
- Supported account types are:

```text
SAVING
CURRENT
```

- A customer can have at most one `SAVING` account.
- A customer can have at most one `CURRENT` account.
- A new account starts with a balance of `0`.
- Account numbers are generated as 12-digit numeric strings.

## Deposit Rules

- Account number is required.
- Confirmation account number is required.
- Account number and confirmation account number must match.
- Deposit amount must be greater than zero.
- A successful deposit increases the account balance.
- A transaction with type `DEPOSIT` is recorded.
- Failed invalid-amount deposits are also recorded with status `FAILED`.

## Withdrawal Rules

- Account number is required.
- Confirmation account number is required.
- Account number and confirmation account number must match.
- Withdrawal amount must be greater than zero.
- Withdrawal cannot exceed the available account balance.
- A successful withdrawal decreases the account balance.
- A transaction with type `WITHDRAW` is recorded.
- Failed withdrawals due to insufficient balance are recorded with status `FAILED`.

## Transfer Rules

- Sender account number is required.
- Recipient account number is required.
- Confirmation recipient account number is required.
- Recipient and confirmation recipient account numbers must match.
- Sender and recipient accounts must be different.
- Transfer amount must be greater than zero.
- Sender must have sufficient balance.
- A successful transfer creates:
  - `TRANSFER_OUT` transaction for the sender
  - `TRANSFER_IN` transaction for the recipient
- Failed transfers caused by invalid amount or insufficient balance create a failed `TRANSFER_OUT` transaction.

---

# Technology Stack

| Technology | Version / Usage | Purpose |
| --- | --- | --- |
| Java | 21 | Application development |
| Spring Boot | 4.0.7 | Backend framework |
| Spring Web MVC | Spring Boot dependency | REST API development |
| Spring Data JPA | Spring Boot dependency | Database access |
| Hibernate | Spring Boot dependency | ORM |
| MySQL Connector/J | Runtime | MySQL connectivity |
| Spring Security | Spring Boot dependency | Authentication and authorization |
| JJWT | 0.11.5 | JWT creation and validation |
| BCrypt | Spring Security | Password hashing |
| Jakarta Validation | Spring Boot dependency | Request validation |
| springdoc-openapi | 3.0.2 | OpenAPI and Swagger UI |
| Lombok | Spring dependency | Boilerplate reduction |
| JUnit 5 | Spring Boot testing | Unit testing |
| Mockito | Spring Boot testing | Mocking |
| MockMvc | Spring Boot testing | Controller/API testing |
| Maven | Build tool | Dependency and project management |
| Postman | External tool | REST API testing |

---

# Architecture

The project uses a layered architecture.

```text
                         ┌──────────────────────┐
                         │       REST Client    │
                         │   Postman / Swagger  │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │   Spring Security    │
                         │      JWT Filter      │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │     Controllers      │
                         │     REST Endpoints   │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │      Services        │
                         │    Business Logic    │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │     Repositories     │
                         │    Spring Data JPA   │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │   Hibernate / JPA    │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │        MySQL         │
                         └──────────────────────┘
```

---

# Project Structure

```text
BankingApplication
├── pom.xml
├── README.md
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── nikhil
│   │   │           └── BankingApplication
│   │   │               │
│   │   │               ├── BankingApplication.java
│   │   │               │
│   │   │               ├── config
│   │   │               │   └── SecurityConfig.java
│   │   │               │
│   │   │               ├── controller
│   │   │               │   ├── AccountController.java
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── DepositController.java
│   │   │               │   ├── TransactionController.java
│   │   │               │   ├── TransferController.java
│   │   │               │   └── WithdrawController.java
│   │   │               │
│   │   │               ├── dto
│   │   │               │   ├── AccountDetailsDTO.java
│   │   │               │   ├── AccountListDTO.java
│   │   │               │   ├── AuthenticationResultDTO.java
│   │   │               │   ├── CreateAccountDTO.java
│   │   │               │   ├── CustomerLoginDTO.java
│   │   │               │   ├── CustomerRegistrationDTO.java
│   │   │               │   ├── MoneyTransferDTO.java
│   │   │               │   ├── TransactionHistoryDTO.java
│   │   │               │   ├── TransactionRequestDTO.java
│   │   │               │   └── TransactionResultDTO.java
│   │   │               │
│   │   │               ├── entity
│   │   │               │   ├── Account.java
│   │   │               │   ├── AccountType.java
│   │   │               │   ├── Customer.java
│   │   │               │   ├── Transaction.java
│   │   │               │   ├── TransactionStatus.java
│   │   │               │   └── TransactionType.java
│   │   │               │
│   │   │               ├── exception
│   │   │               │   ├── AccountNotFoundException.java
│   │   │               │   ├── AccountOwnershipException.java
│   │   │               │   ├── BankingException.java
│   │   │               │   ├── DuplicateCustomerException.java
│   │   │               │   └── InvalidCredentialsException.java
│   │   │               │
│   │   │               ├── repository
│   │   │               │   ├── AccountRepository.java
│   │   │               │   └── CustomerRepository.java
│   │   │               │
│   │   │               ├── security
│   │   │               │   └── JwtFilter.java
│   │   │               │
│   │   │               ├── service
│   │   │               │   ├── AccountService.java
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── DepositService.java
│   │   │               │   ├── TransactionService.java
│   │   │               │   ├── TransferService.java
│   │   │               │   ├── WithdrawService.java
│   │   │               │   │
│   │   │               │   └── impl
│   │   │               │       ├── AccountServiceImpl.java
│   │   │               │       ├── AuthServiceImpl.java
│   │   │               │       ├── DepositServiceImpl.java
│   │   │               │       ├── JwtService.java
│   │   │               │       ├── TransactionServiceImpl.java
│   │   │               │       ├── TransferServiceImpl.java
│   │   │               │       └── WithdrawServiceImpl.java
│   │   │               │
│   │   │               └── utility
│   │   │                   └── GlobalExceptionHandler.java
│   │   │
│   │   └── resources
│   │       └── application.properties
│   │
│   └── test
│       └── java
│           └── com
│               └── nikhil
│                   └── BankingApplication
│                       ├── BankingApplicationTests.java
│                       ├── controller
│                       │   └── AccountControllerTest.java
│                       └── service
│                           └── impl
│                               ├── AccountServiceImplTest.java
│                               ├── AuthServiceTest.java
│                               ├── DepositServiceImplTest.java
│                               ├── JwtServiceTest.java
│                               ├── TransactionServiceImplTest.java
│                               ├── TransferServiceImplTest.java
│                               └── WithdrawServiceImplTest.java
```

---

# Database Design

The application uses **MySQL** with Spring Data JPA and Hibernate.

The configured database name is:

```text
banking_application
```

Hibernate is configured to update the database schema automatically:

```properties
spring.jpa.hibernate.ddl-auto=update
```

The main database tables are:

```text
customers
accounts
transactions
```

---

# Entity Relationships

The entity relationship is:

```text
Customer
   │
   │ 1
   │
   │ *
   ▼
Account
   │
   │ 1
   │
   │ *
   ▼
Transaction
```

## Customer

The `Customer` entity contains:

- `id`
- `name`
- `email`
- `password`
- `createdDate`
- `accounts`

The email column is unique.

The password field stores the BCrypt-encoded password.

## Account

The `Account` entity contains:

- `accountNumber`
- `balance`
- `accountType`
- `owner`
- `createdDate`
- `transactions`

The account number is the primary key.

Account balance uses:

```java
BigDecimal
```

with database precision:

```text
precision = 19
scale = 2
```

## Transaction

The `Transaction` entity contains:

- `id`
- `type`
- `amount`
- `timestamp`
- `description`
- `balanceAfterTransaction`
- `status`
- `account`

Supported transaction types:

```text
DEPOSIT
WITHDRAW
TRANSFER_IN
TRANSFER_OUT
```

Supported transaction statuses:

```text
SUCCESS
FAILED
```

## Account Types

```text
SAVING
CURRENT
```

---

# Security

The application uses **Spring Security** with stateless JWT authentication.

## Public Endpoints

The following endpoints are permitted without authentication:

```text
/api/auth/**
/swagger-ui/**
/v3/api-docs/**
```

All other endpoints require authentication.

## Password Security

Customer passwords are encoded using:

```java
BCryptPasswordEncoder
```

The original password is not stored directly in the database.

## JWT Authentication

After successful login, the application creates a JWT token.

The token contains the customer's email as its subject.

The configured token lifetime is:

```text
1 hour
```

Protected requests must send:

```http
Authorization: Bearer <JWT_TOKEN>
```

## Stateless Security

Spring Security uses:

```text
SessionCreationPolicy.STATELESS
```

The application does not maintain server-side login sessions.

---

# Authentication Flow

```text
Register Customer
       │
       ▼
Customer saved in MySQL
       │
       ▼
Password encoded using BCrypt
       │
       ▼
Login with Email + Password
       │
       ▼
Spring Security validates credentials
       │
       ▼
JWT generated
       │
       ▼
Client receives token
       │
       ▼
Client sends Bearer Token
       │
       ▼
JwtFilter extracts token
       │
       ▼
Email extracted from token
       │
       ▼
JWT validated
       │
       ▼
Authenticated Principal
       │
       ▼
Protected Banking API
```

### Example Authorization Header

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

# REST API

Base URL:

```text
http://localhost:8080
```

## Authentication APIs

| Method | Endpoint | Authentication | Description |
| --- | --- | --- | --- |
| POST | `/api/auth/register` | No | Register customer |
| POST | `/api/auth/login` | No | Login and receive JWT |

## Account APIs

| Method | Endpoint | Authentication | Description |
| --- | --- | --- | --- |
| POST | `/api/accounts/open` | Yes | Open account |
| GET | `/api/accounts/{accountNumber}` | Yes | Check balance |
| GET | `/api/accounts/my-accounts` | Yes | Get logged-in customer's accounts |

## Banking APIs

| Method | Endpoint | Authentication | Description |
| --- | --- | --- | --- |
| POST | `/api/deposit` | Yes | Deposit money |
| POST | `/api/withdraw` | Yes | Withdraw money |
| POST | `/api/transfer` | Yes | Transfer money |

## Transaction History APIs

| Method | Endpoint | Authentication | Description |
| --- | --- | --- | --- |
| GET | `/api/accounts/{accountNumber}/history` | Yes | All account transactions |
| GET | `/api/accounts/{accountNumber}/deposit-history` | Yes | Deposit history |
| GET | `/api/accounts/{accountNumber}/withdraw-history` | Yes | Withdrawal history |
| GET | `/api/accounts/{accountNumber}/transferIn-history` | Yes | Transfer-in history |
| GET | `/api/accounts/{accountNumber}/transferOut-history` | Yes | Transfer-out history |
| GET | `/api/accounts/{amount}/balance-history` | Yes | Transactions with balance below amount |
| GET | `/api/accounts/time-history` | Yes | Transactions in a time range |
| GET | `/api/accounts/status-history` | Yes | Transactions by status |

---

# API Details

## 1. Register Customer

### Endpoint

```http
POST /api/auth/register
```

### Authentication

Not required.

### Request Body

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### Validation

- `name` is required.
- Name must contain only letters and spaces.
- Email must be valid.
- Password must contain at least 6 characters.

### Successful Response

```text
201 Created
Customer registered successfully
```

### Duplicate Email

```text
409 Conflict
Email already registered
```

---

## 2. Login

### Endpoint

```http
POST /api/auth/login
```

### Authentication

Not required.

### Request Body

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

### Successful Response

```json
{
  "token": "<JWT_TOKEN>",
  "message": "Login successful"
}
```

Use the returned token for protected endpoints.

```http
Authorization: Bearer <JWT_TOKEN>
```

### Invalid Credentials

The application returns:

```text
401 Unauthorized
Invalid email or password
```

---

## 3. Open Account

### Endpoint

```http
POST /api/accounts/open
```

### Authentication

Required.

### Header

```http
Authorization: Bearer <JWT_TOKEN>
```

### Request Body

```json
{
  "accountType": "SAVING"
}
```

or:

```json
{
  "accountType": "CURRENT"
}
```

### Successful Response

```json
{
  "accountNumber": "123456789012",
  "accountType": "SAVING",
  "balance": 0,
  "owner": "John Doe"
}
```

### Important Rule

A customer cannot create another account of the same account type.

For example, if a customer already has a `SAVING` account, another `SAVING` account is rejected.

---

## 4. Check Account Balance

### Endpoint

```http
GET /api/accounts/{accountNumber}
```

### Example

```http
GET /api/accounts/123456789012
```

### Authentication

Required.

### Header

```http
Authorization: Bearer <JWT_TOKEN>
```

### Successful Response

```json
{
  "message": "Balance in your Account : 5000.00"
}
```

The account service verifies that the requested account belongs to the authenticated customer.

---

## 5. Get My Accounts

### Endpoint

```http
GET /api/accounts/my-accounts
```

### Authentication

Required.

### Header

```http
Authorization: Bearer <JWT_TOKEN>
```

### Example Response

```json
[
  {
    "accountNumber": "123456789012",
    "holderName": "John Doe",
    "balance": 5000.00,
    "accountType": "SAVING"
  }
]
```

If the customer has no accounts, the service returns a banking error.

---

## 6. Deposit Money

### Endpoint

```http
POST /api/deposit
```

### Authentication

Required.

### Request Body

```json
{
  "accountNumber": "123456789012",
  "confirmAccountNumber": "123456789012",
  "amount": 5000
}
```

### Successful Response

```json
{
  "message": "₹5000 credited successfully in your account"
}
```

### Deposit Processing

```text
Validate Request
      ↓
Check Account
      ↓
Validate Amount
      ↓
Increase Balance
      ↓
Create DEPOSIT Transaction
      ↓
Save Account
```

---

## 7. Withdraw Money

### Endpoint

```http
POST /api/withdraw
```

### Authentication

Required.

### Request Body

```json
{
  "accountNumber": "123456789012",
  "confirmAccountNumber": "123456789012",
  "amount": 1000
}
```

### Successful Response

```json
{
  "message": "₹1000 debited successfully in your account"
}
```

### Withdrawal Processing

```text
Validate Request
      ↓
Check Account
      ↓
Validate Amount
      ↓
Check Available Balance
      ↓
Decrease Balance
      ↓
Create WITHDRAW Transaction
      ↓
Save Account
```

If the amount is greater than the available balance:

```text
Withdrawal failed: Insufficient balance
```

---

## 8. Transfer Money

### Endpoint

```http
POST /api/transfer
```

### Authentication

Required.

### Request Body

```json
{
  "senderAccountNumber": "123456789012",
  "recipientAccountNumber": "987654321098",
  "confirmRecipientAccountNumber": "987654321098",
  "amount": 1000
}
```

### Successful Response

```json
{
  "message": "₹1000 transferred successfully from John Doe to Jane Doe"
}
```

### Transfer Processing

```text
Validate Recipient Confirmation
          ↓
Find Sender Account
          ↓
Find Recipient Account
          ↓
Check Different Accounts
          ↓
Validate Amount
          ↓
Check Sender Balance
          ↓
Withdraw from Sender
          ↓
Deposit into Recipient
          ↓
Create TRANSFER_OUT Transaction
          ↓
Create TRANSFER_IN Transaction
          ↓
Save Sender
          ↓
Save Recipient
```

---

# Transaction History APIs

## 1. Get Complete Transaction History

### Endpoint

```http
GET /api/accounts/{accountNumber}/history
```

### Example

```http
GET /api/accounts/123456789012/history
```

### Response

```json
[
  {
    "id": 1,
    "type": "DEPOSIT",
    "amount": 5000.00,
    "timestamp": "2026-08-27T10:30:00",
    "description": "₹5000 credited successfully",
    "balanceAfterTransaction": 5000.00,
    "status": "SUCCESS"
  }
]
```

---

## 2. Deposit History

### Endpoint

```http
GET /api/accounts/{accountNumber}/deposit-history
```

Returns only transactions where:

```text
type = DEPOSIT
```

If no deposit transactions exist, the service returns:

```text
No deposit transaction found
```

---

## 3. Withdraw History

### Endpoint

```http
GET /api/accounts/{accountNumber}/withdraw-history
```

Returns only transactions where:

```text
type = WITHDRAW
```

If no withdrawal transactions exist:

```text
No withdraw transaction found
```

---

## 4. Transfer-In History

### Endpoint

```http
GET /api/accounts/{accountNumber}/transferIn-history
```

Returns transactions where:

```text
type = TRANSFER_IN
```

---

## 5. Transfer-Out History

### Endpoint

```http
GET /api/accounts/{accountNumber}/transferOut-history
```

Returns transactions where:

```text
type = TRANSFER_OUT
```

---

## 6. Balance-Based Transaction History

### Endpoint

```http
GET /api/accounts/{amount}/balance-history
```

### Example

```http
GET /api/accounts/5000/balance-history
```

The service returns transactions where:

```text
balanceAfterTransaction < amount
```

The amount must be greater than zero.

---

## 7. Time-Based Transaction History

### Endpoint

```http
GET /api/accounts/time-history?from=09:00&to=18:00
```

### Query Parameters

| Parameter | Example | Description |
| --- | --- | --- |
| `from` | `09:00` | Starting time |
| `to` | `18:00` | Ending time |

The expected format is:

```text
HH:mm
```

Example:

```text
09:00
18:30
```

Transactions are selected when their transaction time is between the supplied times, inclusive.

---

## 8. Status-Based Transaction History

### Endpoint

```http
GET /api/accounts/status-history?status=SUCCESS
```

Supported statuses:

```text
SUCCESS
FAILED
```

The status comparison is case-insensitive because the service converts the supplied value to uppercase.

---

# Validation

The project uses **Jakarta Bean Validation**.

## Customer Registration Validation

```java
@NotBlank
@Pattern
@Email
@Size(min = 6)
```

Rules:

- Name cannot be blank.
- Name can contain letters and spaces.
- Email must be valid.
- Password must contain at least 6 characters.

## Login Validation

```java
@Email
@NotBlank
```

## Account Validation

```java
@NotNull
```

Account type is required.

## Transaction Validation

```java
@NotBlank
@Positive
```

The transaction request requires:

- Account number
- Confirmation account number
- Positive amount

## Transfer Validation

The transfer request requires:

- Sender account number
- Recipient account number
- Confirmation recipient account number
- Positive amount

---

# Exception Handling

The application uses:

```java
@RestControllerAdvice
```

through:

```text
GlobalExceptionHandler
```

Handled exceptions include:

- `DuplicateCustomerException`
- `InvalidCredentialsException`
- `BankingException`
- `AccountOwnershipException`
- `AccountNotFoundException`
- `MethodArgumentNotValidException`
- `DateTimeParseException`
- `HttpMessageNotReadableException`
- Generic `RuntimeException`

## Example Error Response

For banking exceptions:

```json
{
  "error": "Insufficient balance"
}
```

For ownership errors:

```json
{
  "error": "..."
}
```

For validation errors:

```json
{
  "error": "Amount must be greater than 0"
}
```

For invalid account type:

```text
Invalid account type. Allowed values: SAVING, CURRENT
```

For invalid time input:

```json
{
  "message": "Invalid time format. Please use HH:mm (e.g., 09:00)"
}
```

---

# HTTP Status Codes

The current controllers and global exception handler use the following responses.

| Status Code | Meaning |
| --- | --- |
| `200 OK` | Successful request |
| `201 Created` | Customer registration successful |
| `400 Bad Request` | Validation/business/input error |
| `401 Unauthorized` | Invalid credentials or unauthenticated access |
| `403 Forbidden` | Authenticated user does not own the requested account |
| `404 Not Found` | Account not found |
| `409 Conflict` | Duplicate customer registration |

> **Note:** The current account-opening controller returns `200 OK`, not `201 Created`.

---

# Swagger / OpenAPI

The project uses:

```text
springdoc-openapi-starter-webmvc-ui
```

Version:

```text
3.0.2
```

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON documentation is available under:

```text
http://localhost:8080/v3/api-docs
```

Swagger UI can be used to:

- View available APIs
- Inspect request/response models
- Execute APIs from the browser
- Provide a JWT Bearer token for protected APIs

For protected requests, use:

```text
Bearer <JWT_TOKEN>
```

---

# Logging

The services use **SLF4J** logging through:

```java
LoggerFactory.getLogger(...)
```

Logging is present in:

- Authentication service
- Account service
- Deposit service
- Withdrawal service
- Transfer service
- Transaction-history service
- JWT service

The application records information such as:

- Registration attempts
- Login attempts
- Account creation
- Deposit requests
- Withdrawal requests
- Transfer requests
- Transaction-history queries
- Ownership mismatches
- Invalid inputs
- Account-not-found conditions

> **Security note:** The current `JwtService` contains a DEBUG log statement that logs the generated JWT token. For a production banking application, this should be removed or changed so that tokens are never written to logs.

The `JwtFilter` currently prints JWT validation failures using `System.out.println`. This can also be replaced with the application's SLF4J logger for consistent logging.

---

# Configuration

The application uses:

```text
src/main/resources/application.properties
```

Current configuration:

```properties
spring.application.name=BankingApplication

spring.datasource.url: jdbc:mysql://${DB_HOST}:${DB_PORT}/banking_application?createDatabaseIfNotExist=true
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
jwt.secret=${JWT_SECRET}
```

The database connection uses environment variables.

The database name is fixed as:

```text
banking_application
```

The server runs on:

```text
8080
```

---

# Environment Variables

The application expects these environment variables:

| Variable | Purpose | Example |
| --- | --- | --- |
| `DB_HOST` | MySQL host | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | `your_password` |
| `JWT_SECRET` | JWT signing secret | `your_long_secret_key` |

Example environment configuration:

```text
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_long_secret_key
```

> **Important:** The current `JwtService` reads `JWT_SECRET` directly using `@Value("${JWT_SECRET}")`. Therefore, make sure the `JWT_SECRET` environment variable is available when the application starts.

Do not commit real database passwords or JWT secrets to GitHub.

---

# Prerequisites

Install the following software before running the project:

- Java JDK 21
- Maven
- MySQL Server
- MySQL Workbench (optional)
- Postman (optional)
- IntelliJ IDEA / Eclipse / VS Code

Verify Java:

```bash
java -version
```

The project is configured for:

```text
Java 21
```

Verify Maven:

```bash
mvn -version
```

---

# Database Setup

Start MySQL and create the database.

```sql
CREATE DATABASE banking_application;
```

The application also uses:

```text
createDatabaseIfNotExist=true
```

in the JDBC URL, so the database can be created automatically when the configured MySQL user has permission to create databases.

Hibernate automatically manages the tables because:

```properties
spring.jpa.hibernate.ddl-auto=update
```

---

# Installation

## 1. Clone the Repository

```bash
git clone <your-repository-url>
```

## 2. Enter the Project Directory

```bash
cd BankingApplication
```

## 3. Configure Environment Variables

Set:

```text
DB_HOST
DB_PORT
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

## 4. Install Dependencies

```bash
mvn clean install
```

## 5. Run Tests

```bash
mvn test
```

## 6. Start the Application

```bash
mvn spring-boot:run
```

---

# Running the Application

After startup, the application is available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

---

# API Testing Flow

The recommended API testing order is:

```text
1. Register Customer
        ↓
2. Login Customer
        ↓
3. Copy JWT Token
        ↓
4. Add Authorization Header
        ↓
5. Open Saving/Current Account
        ↓
6. Check Account Balance
        ↓
7. Deposit Money
        ↓
8. Check Balance Again
        ↓
9. Withdraw Money
        ↓
10. Transfer Money
        ↓
11. View Transaction History
        ↓
12. Filter Transaction History
```

## Authorization Header

For every protected endpoint:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# Testing

The project contains test classes for application, controller, and service functionality.

## Test Classes

```text
src/test/java/com/nikhil/BankingApplication/

├── BankingApplicationTests.java
│
├── controller
│   └── AccountControllerTest.java
│
└── service
    └── impl
        ├── AccountServiceImplTest.java
        ├── AuthServiceTest.java
        ├── DepositServiceImplTest.java
        ├── JwtServiceTest.java
        ├── TransactionServiceImplTest.java
        ├── TransferServiceImplTest.java
        └── WithdrawServiceImplTest.java
```

## Testing Technologies

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc

Run all tests:

```bash
mvn test
```

The tests cover functionality such as:

- Customer registration
- Authentication
- JWT generation/validation
- Account creation
- Account balance
- Deposit
- Withdrawal
- Fund transfer
- Transaction history
- Business-rule failures
- Controller behavior

---

# Important Implementation Notes

This README describes the implementation currently present in the project code.

## 1. Account Ownership

The following service operations explicitly check whether the authenticated email owns the requested account:

- Account balance checking
- Account listing
- Account transaction history
- Deposit-history lookup
- Withdraw-history lookup
- Transfer-in history
- Transfer-out history

However, the current `DepositServiceImpl`, `WithdrawServiceImpl`, and `TransferServiceImpl` receive only the request DTO and do not receive the authenticated user's email.

Therefore, although those endpoints require a valid JWT, the current service implementation does **not** perform the same explicit owner-email check before processing a deposit, withdrawal, or transfer.

For a production banking system, ownership authorization should be enforced in those operations as well.

## 2. Transfer Transactionality

The current transfer implementation saves the sender and recipient separately:

```text
save(sender)
save(recipient)
```

The service is not currently annotated with:

```java
@Transactional
```

Therefore, the README does not claim full database transaction atomicity for transfers.

A future improvement should wrap the transfer operation in a database transaction.

## 3. JWT Token Logging

The current `JwtService` has a DEBUG statement that includes the generated token.

For production:

```text
Do not log JWT tokens.
```

## 4. JWT Secret Configuration

`application.properties` contains:

```properties
jwt.secret=${JWT_SECRET}
```

while `JwtService` reads:

```java
@Value("${JWT_SECRET}")
```

The actual `JwtService` therefore expects the uppercase `JWT_SECRET` configuration key/environment variable.

## 5. Account Creation Response

The account-opening endpoint currently returns:

```text
200 OK
```

even though the operation creates a new account.

It is not currently returning `201 Created`.

## 6. Monetary Values

The project uses `BigDecimal` for:

- Account balance
- Deposit amount
- Withdrawal amount
- Transfer amount
- Transaction amount

This is appropriate for representing currency values without the normal floating-point limitations of `double`.

---

# Common Errors

## 1. `401 Unauthorized`

Possible reasons:

- JWT token is missing.
- JWT token is invalid.
- JWT token is expired.
- Authorization header is missing.
- `Bearer` prefix is missing.

Use:

```http
Authorization: Bearer <JWT_TOKEN>
```

## 2. `403 Forbidden`

This can occur when an authenticated customer attempts to access an account that does not belong to them in services that perform ownership validation.

## 3. MySQL Connection Error

Check:

```text
DB_HOST
DB_PORT
DB_USERNAME
DB_PASSWORD
```

Also make sure MySQL is running.

Default local MySQL configuration commonly uses:

```text
DB_HOST=localhost
DB_PORT=3306
```

## 4. JWT Secret Error

Check:

```text
JWT_SECRET
```

Make sure the secret is available to the application before startup.

## 5. Invalid Account Type

Only these values are accepted:

```text
SAVING
CURRENT
```

Example:

```json
{
  "accountType": "SAVING"
}
```

## 6. Validation Error

Example invalid transaction:

```json
{
  "accountNumber": "123456789012",
  "confirmAccountNumber": "123456789012",
  "amount": -100
}
```

The amount must be greater than zero.

## 7. Invalid Time Format

The time-history API expects:

```text
HH:mm
```

Correct:

```text
09:00
18:30
```

Incorrect:

```text
9 AM
18.30
```

## 8. Swagger Not Opening

Make sure the application is running and open:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Example API Flow

## Step 1 — Register

```http
POST /api/auth/register
```

```json
{
  "name": "Nikhil Patidar",
  "email": "nikhil@example.com",
  "password": "password123"
}
```

## Step 2 — Login

```http
POST /api/auth/login
```

```json
{
  "email": "nikhil@example.com",
  "password": "password123"
}
```

Copy:

```text
token
```

## Step 3 — Open Account

```http
POST /api/accounts/open
Authorization: Bearer <JWT_TOKEN>
```

```json
{
  "accountType": "SAVING"
}
```

Copy the generated 12-digit account number.

## Step 4 — Deposit

```http
POST /api/deposit
Authorization: Bearer <JWT_TOKEN>
```

```json
{
  "accountNumber": "123456789012",
  "confirmAccountNumber": "123456789012",
  "amount": 5000
}
```

## Step 5 — Check Balance

```http
GET /api/accounts/123456789012
Authorization: Bearer <JWT_TOKEN>
```

## Step 6 — Withdraw

```http
POST /api/withdraw
Authorization: Bearer <JWT_TOKEN>
```

```json
{
  "accountNumber": "123456789012",
  "confirmAccountNumber": "123456789012",
  "amount": 1000
}
```

## Step 7 — Transfer

```http
POST /api/transfer
Authorization: Bearer <JWT_TOKEN>
```

```json
{
  "senderAccountNumber": "123456789012",
  "recipientAccountNumber": "987654321098",
  "confirmRecipientAccountNumber": "987654321098",
  "amount": 1000
}
```

## Step 8 — Transaction History

```http
GET /api/accounts/123456789012/history
Authorization: Bearer <JWT_TOKEN>
```

---

# Future Improvements

The following improvements can make the application more production-ready:

- Add explicit authenticated-user ownership validation to deposit, withdrawal, and transfer operations.
- Add `@Transactional` to fund-transfer operations.
- Replace `System.out.println` with SLF4J logging in `JwtFilter`.
- Use a single consistent JWT configuration property.
- Add stronger password rules.
- Add database-level concurrency controls for simultaneous withdrawals/transfers.
- Add transaction pagination.
- Add sorting options for transaction history.
- Add full integration tests with a dedicated test database.
- Add Docker support.
- Add CI/CD with GitHub Actions.
- Add production database migrations using Flyway or Liquibase.
- Add rate limiting and additional security hardening.
- Add refresh-token support if required.
- Add API versioning.

---

# Author

**Nikhil Patidar**

Bank Management System — Spring Boot Backend Project

### Technologies Used

- Java 21
- Spring Boot 4.0.7
- Spring Web MVC
- Spring Security
- JWT
- BCrypt
- Spring Data JPA
- Hibernate
- MySQL
- Jakarta Validation
- Swagger/OpenAPI
- JUnit 5
- Mockito
- MockMvc
- Maven
- Lombok

---

# Version History

## 0.0.1-SNAPSHOT — Current Project Version

- Added Spring Boot REST API
- Added MySQL database integration
- Added Spring Data JPA
- Added Hibernate ORM
- Added customer registration
- Added BCrypt password hashing
- Added customer login
- Added JWT authentication
- Added stateless Spring Security configuration
- Added SAVING and CURRENT account types
- Added 12-digit account number generation
- Added account creation
- Added account balance checking
- Added logged-in customer's account listing
- Added deposit functionality
- Added withdrawal functionality
- Added money transfer functionality
- Added successful and failed transaction records
- Added complete transaction history
- Added deposit transaction history
- Added withdrawal transaction history
- Added transfer-in transaction history
- Added transfer-out transaction history
- Added balance-based transaction filtering
- Added time-based transaction filtering
- Added status-based transaction filtering
- Added Jakarta Bean Validation
- Added custom banking exceptions
- Added global exception handling
- Added Swagger/OpenAPI documentation
- Added SLF4J logging
- Added JUnit and Mockito tests
- Added MockMvc controller testing

---

# License

This project is developed as an educational banking backend project.

Unless otherwise specified, the project can be used and modified for learning and educational purposes.

---

# Acknowledgments

- Spring Boot documentation and community
- Spring Security documentation
- Spring Data JPA and Hibernate
- MySQL
- Swagger / OpenAPI
- JUnit
- Mockito
- Maven
- Lombok
- Spring Boot ecosystem and community
