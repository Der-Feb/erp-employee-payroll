# ERP Payroll Management System - Backend

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Database Setup](#database-setup)
3. [Project Configuration](#project-configuration)
4. [Running the Application](#running-the-application)
5. [ER Diagram](#er-diagram)
6. [Spring Boot Flow Diagram](#spring-boot-flow-diagram)
7. [Duplicate Payroll Prevention](#duplicate-payroll-prevention)
8. [API Endpoints](#api-endpoints)

---

## Prerequisites
- Java 21
- Maven 3.x
- PostgreSQL 14 or higher

---

## Database Setup
1. Start PostgreSQL
2. Create the database:
   ```sql
   CREATE DATABASE erd_db;
   ```
3. Update `application.properties` if your PostgreSQL credentials are different:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/erd_db
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   ```

---

## Project Configuration
The project uses:
- Spring Boot 4.1.0
- Spring Data JPA
- Spring Security
- PostgreSQL Driver
- Lombok

---

## Running the Application
```bash
mvn clean install
mvn spring-boot:run
```

The application will automatically:
- Create the database tables
- Seed deductions (30%, 6%, 5%, 5%, 14%, 14%)
- Seed ADMIN user:
  - Email: admin@erp.gov.rw
  - Password: admin123
- Seed 2 sample employees:
  - Mugabo Javis (empId: 123, base salary: 70,000 RWF, email: mugabo.javis@example.com, password: password123)
  - Michou Michell (empId: 234, base salary: 35,000 RWF, email: michou.michell@example.com, password: password123)
- Create the database trigger for messaging

---

## Security Features
- Login/Signup for employees
- Seeded admin user
- Passwords are BCrypt encrypted
- Basic Spring Security setup (expanded authorization can be added)

---

## ER Diagram (Textual Representation)
```
┌──────────────────┐         ┌──────────────────┐         ┌──────────────────┐
│     Employee     │         │    Employment    │         │     Deduction    │
│------------------│         │------------------│         │------------------│
│ id (PK)          │1───────1│ id (PK)          │         │ id (PK)          │
│ first_name       │         │ employee_id (UK) │         │ name (UK)        │
│ last_name        │         │ department       │         │ percentage       │
│ email (UK)       │         │ position         │         └──────────────────┘
│ district         │         │ base_salary      │
│ mobile           │         │ status           │
│ date_of_birth    │         │ joining_date     │
└──────────────────┘         │ employee_id_ref (FK) │
                              └──────────────────┘

┌──────────────────┐         ┌──────────────────┐
│     Payslip      │         │     Message      │
│------------------│         │------------------│
│ id (PK)          │         │ id (PK)          │
│ employee_id (FK) │───────1 │ employee_id (FK) │
│ base_salary      │         │ message          │
│ house            │         │ month_year       │
│ transport        │         │ sent_at          │
│ gross_salary     │         └──────────────────┘
│ tax              │
│ pension          │
│ medical          │
│ others           │
│ net_salary       │
│ status           │
│ month            │
│ year             │
└──────────────────┘
```

---

## Spring Boot Flow Diagram
```
┌──────────────────┐
│  Client Request  │
└─────────┬────────┘
          │
┌─────────▼────────┐
│   Controller     │ (Handles HTTP requests)
└─────────┬────────┘
          │
┌─────────▼────────┐
│     Service      │ (Business logic, calculations)
└─────────┬────────┘
          │
┌─────────▼────────┐
│   Repository     │ (Database operations)
└─────────┬────────┘
          │
┌─────────▼────────┐
│  PostgreSQL DB   │ (Tables, triggers, functions)
└──────────────────┘
```

### Detailed Flow for Payroll Generation:
1. User sends `POST /api/payroll/generate` with month/year
2. PayrollController receives the request
3. PayrollService:
   a. Fetches all active employments
   b. For each employee:
      - Checks if payslip already exists for the month/year
      - If exists → skip
      - If not exists → calculates all allowances, deductions, gross and net salary
      - Saves payslip with status "Pending"
4. Returns list of generated payslips

### Detailed Flow for Payroll Approval:
1. User sends `POST /api/payroll/approve` with month/year
2. PayrollController receives the request
3. PayrollService:
   a. Fetches all "Pending" payslips for the month/year
   b. Saves each payslip (triggers database trigger)
4. Database trigger executes:
   a. Creates message for each employee
   b. Updates payslip status to "Paid"

---

## Duplicate Payroll Prevention
The system prevents duplicate payroll by:
1. In the `PayslipRepository`, we have a custom query:
   ```java
   boolean existsByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);
   ```
2. Before generating a payslip, we check if a payslip already exists for that employee, month, and year
3. If it exists → we skip creating a new one

---

## API Endpoints
### Base URL: `http://localhost:8080/api`

### 1. Authentication
| Method | URL               | Description           |
|--------|-------------------|-----------------------|
| POST   | `/auth/signup`    | Signup as new employee |
| POST   | `/auth/login`     | Login (admin or employee) |

### 2. Employee Management
| Method | URL               | Description           |
|--------|-------------------|-----------------------|
| POST   | `/employees`      | Create new employee  |
| GET    | `/employees`      | Get all employees    |
| GET    | `/employees/{id}` | Get employee by ID   |

### 3. Deduction Management
| Method | URL               | Description           |
|--------|-------------------|-----------------------|
| GET    | `/deductions`     | Get all deductions   |
| PUT    | `/deductions/{id}`| Update a deduction   |

### 4. Payroll Management
| Method | URL               | Description           |
|--------|-------------------|-----------------------|
| POST   | `/payroll/generate` | Generate payroll for month/year |
| POST   | `/payroll/approve`  | Approve payroll (triggers messaging) |
| GET    | `/payroll/payslips?month={m}&year={y}` | Get payslips for month/year |
| GET    | `/payroll/payslips/employee/{employeeId}` | Get payslips for employee |

### 5. Message Management
| Method | URL               | Description           |
|--------|-------------------|-----------------------|
| GET    | `/messages`       | Get all messages     |
| GET    | `/messages/employee/{employeeId}` | Get messages for employee |
| GET    | `/messages/month-year/{monthYear}` | Get messages for month/year (e.g., "06/2025") |

---

## Sample Message Format
```
Dear Mugabo, Your salary of 06/2025 from RCA 57800 has been credited to your 123 account successfully.
```
