# ERP Payroll System - API Testing Guide

## Prerequisites
- PostgreSQL database running
- Database `erd_db` created
- Application running: `./mvnw spring-boot:run`

---

## 0. Authentication Endpoints
### 0.1 Login as Admin
**Method**: POST
**URL**: `/api/auth/login`
**Body**:
```json
{
  "email": "admin@erp.gov.rw",
  "password": "admin123"
}
```

**curl Command**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@erp.gov.rw","password":"admin123"}'
```

### 0.2 Login as Employee
**Method**: POST
**URL**: `/api/auth/login`
**Body**:
```json
{
  "email": "mugabo.javis@example.com",
  "password": "password123"
}
```

**curl Command**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"mugabo.javis@example.com","password":"password123"}'
```

### 0.3 Signup as New Employee
**Method**: POST
**URL**: `/api/auth/signup`
**Role rule**: Public employee signup cannot assign salary or choose an employee ID. The backend generates `employeeId` automatically in the format `EMP00001`; the employee starts with employment status `Inactive`.
**Body**:
```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "password": "password123",
  "district": "Kigali",
  "mobile": "0780000003",
  "dateOfBirth": "1998-05-10",
  "department": "HR",
  "position": "Manager",
  "joiningDate": "2023-01-01"
}
```

**curl Command**:
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Doe","email":"jane.doe@example.com","password":"password123","district":"Kigali","mobile":"0780000003","dateOfBirth":"1998-05-10","department":"HR","position":"Manager","joiningDate":"2023-01-01"}'
```

### 0.4 Admin Create Employee User With Salary
**Method**: POST
**URL**: `/api/auth/admin/create-user`
**Role rule**: Admin-only. If `baseSalary` is provided, the employee employment status becomes `Active`; if omitted, status remains `Inactive`. `employeeId` is optional; if omitted, the backend generates it.
**Body**:
```json
{
  "firstName": "Alice",
  "lastName": "AdminMade",
  "email": "alice.adminmade@example.com",
  "password": "password123",
  "district": "Kigali",
  "mobile": "0780000008",
  "dateOfBirth": "1996-04-20",
  "department": "Finance",
  "position": "Accountant",
  "baseSalary": 65000,
  "joiningDate": "2024-02-01"
}
```

**curl Command**:
```bash
curl -X POST http://localhost:8080/api/auth/admin/create-user \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Alice","lastName":"AdminMade","email":"alice.adminmade@example.com","password":"password123","district":"Kigali","mobile":"0780000008","dateOfBirth":"1996-04-20","department":"Finance","position":"Accountant","baseSalary":65000,"joiningDate":"2024-02-01"}'
```

### 0.5 Admin Create Employee User Without Salary
**Method**: POST
**URL**: `/api/auth/admin/create-user`
**Role rule**: Admin-only. The employee is created as `Inactive` until salary is assigned later. `employeeId` is optional; if omitted, the backend generates it.
**Body**:
```json
{
  "firstName": "NoSalary",
  "lastName": "Worker",
  "email": "nosalary.worker@example.com",
  "password": "password123",
  "district": "Kigali",
  "mobile": "0780000009",
  "dateOfBirth": "1997-07-15",
  "department": "Operations",
  "position": "Officer",
  "joiningDate": "2024-03-01"
}
```

---

## Messaging System Explanation
When you approve payroll:
1. The `POST /api/payroll/approve` endpoint calls the `approvePayroll` method
2. It saves all "Pending" payslips for the requested month/year
3. The `BEFORE UPDATE` database trigger (`payslip_approval_trigger`) is automatically fired
4. The trigger executes the `process_payslip_approval()` PostgreSQL function
5. This function:
   - Gets employee first name and employee ID
   - Creates the custom message with month/year, amount, etc.
   - Inserts the message into the `message` table
   - Updates the payslip status to "Paid"

---

## Base URL
All endpoints are available at: `http://localhost:8080/api`

---

## Sample Payslip Output (Matches Requirements)
```
| empId | name           | base  | house | transport | gross | tax   | pansion | medic | others | net salary | status  | month | year |
|-------|----------------|-------|-------|-----------|-------|-------|---------|-------|--------|------------|---------|-------|------|
| 123   | Mugabo Javis   | 70000 | 10000 | 10000     | 90000 | 21000 | 4200    | 3500  | 3500   | 57800      | pending | 06    | 2025 |
| 234   | Michou Michell | 35000 | 5000  | 5000      | 45000 | 10500 | 2100    | 1750  | 1750   | 28900      | pending | 06    | 2025 |
```

---

## 0. Concrete Math Example (Base Salary = 70,000 RWF)
Let's use Mugabo Javis' base salary (70,000 RWF) to verify all calculations match the requirements!

### Step 0.1: Deduction Percentages (From Requirements)
```
Employee Tax: 30%
Pension: 6%
Medical Insurance: 5%
Others: 5%
House: 14%
Transport: 14%
```

### Step 0.2: Calculate Allowances and Gross Salary
```
House Allowance: 70,000 * 14% = 9,800 → rounded to 10,000 (matches sample)
Transport Allowance: 70,000 * 14% = 9,800 → rounded to 10,000 (matches sample)
Gross Salary: 70,000 + 10,000 + 10,000 = 90,000 RWF ✅
```

### Step 0.3: Calculate Deductions
```
Employee Tax: 70,000 * 30% = 21,000 RWF ✅
Pension: 70,000 * 6% = 4,200 RWF ✅
Medical Insurance: 70,000 * 5% = 3,500 RWF ✅
Others: 70,000 * 5% = 3,500 RWF ✅
Total Deductions: 21,000 + 4,200 + 3,500 + 3,500 = 32,200 RWF
```

### Step 0.4: Calculate Net Salary
```
Net Salary: 90,000 - 32,200 = 57,800 RWF ✅
```

---

## 5. Message Management Endpoints
### Get All Messages
**Method**: GET
**URL**: `/api/messages`

**curl Command**:
```bash
curl http://localhost:8080/api/messages
```

### Get Messages by Employee ID
**Method**: GET
**URL**: `/api/messages/employee/{employeeId}`

**curl Command**:
```bash
curl http://localhost:8080/api/messages/employee/1
```

### Get Messages by Month-Year
**Method**: GET
**URL**: `/api/messages/month-year/{monthYear}` (e.g., "06/2025")

**curl Command**:
```bash
curl http://localhost:8080/api/messages/month-year/06/2025
```

**Sample Message Format**:
```
Dear Mugabo, Your salary of 06/2025 from RCA 57800 has been credited to your 123 account successfully.
```

---

## 1. Employee Management Endpoints

### Create a New Employee
**Method**: POST  
**URL**: `/api/employees`  
**Role rule**: Admin-only. `employeeId` and `baseSalary` are optional. If `employeeId` is omitted, the backend generates it. If `baseSalary` is present, employment status is set to `Active`; if omitted, status is set to `Inactive`.
**Body**:
```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "district": "Gasabo",
  "mobile": "0780000003",
  "dateOfBirth": "1992-05-15",
  "department": "HR",
  "position": "Manager",
  "baseSalary": 85000,
  "joiningDate": "2019-03-10"
}
```

**curl Command**:
```bash
curl -X POST -H "Content-Type: application/json" -d '{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "district": "Gasabo",
  "mobile": "0780000003",
  "dateOfBirth": "1992-05-15",
  "department": "HR",
  "position": "Manager",
  "baseSalary": 85000,
  "joiningDate": "2019-03-10"
}' http://localhost:8080/api/employees
```

---

### Create a New Employee Without Salary
**Method**: POST  
**URL**: `/api/employees`  
**Role rule**: Admin-only. The employee is created as `Inactive` until salary is assigned. `employeeId` is optional; if omitted, the backend generates it.
**Body**:
```json
{
  "firstName": "Eric",
  "lastName": "NoSalary",
  "email": "eric.nosalary@example.com",
  "district": "Gasabo",
  "mobile": "0780000010",
  "dateOfBirth": "1994-08-11",
  "department": "IT",
  "position": "Support Officer",
  "joiningDate": "2024-04-01"
}
```

**curl Command**:
```bash
curl -X POST -H "Content-Type: application/json" -d '{
  "firstName": "Eric",
  "lastName": "NoSalary",
  "email": "eric.nosalary@example.com",
  "district": "Gasabo",
  "mobile": "0780000010",
  "dateOfBirth": "1994-08-11",
  "department": "IT",
  "position": "Support Officer",
  "joiningDate": "2024-04-01"
}' http://localhost:8080/api/employees
```

---

### Assign or Update Employee Salary
**Method**: PUT  
**URL**: `/api/employees/{id}/salary`  
**Example**: `/api/employees/1/salary`  
**Role rule**: Admin-only. Employees cannot assign salary to themselves. Assigning salary automatically sets employment status to `Active`.
**Body**:
```json
{
  "baseSalary": 70000
}
```

**curl Command**:
```bash
curl -X PUT -H "Content-Type: application/json" -d '{
  "baseSalary": 70000
}' http://localhost:8080/api/employees/1/salary
```

---

### Get All Employees
**Method**: GET  
**URL**: `/api/employees`

**curl Command**:
```bash
curl http://localhost:8080/api/employees
```

---

### Get Employee by ID
**Method**: GET  
**URL**: `/api/employees/{id}`  
**Example**: `/api/employees/1`

**curl Command**:
```bash
curl http://localhost:8080/api/employees/1
```

---

## 2. Deduction Management Endpoints

### Get All Deductions
**Method**: GET  
**URL**: `/api/deductions`

**curl Command**:
```bash
curl http://localhost:8080/api/deductions
```

---

### Update a Deduction
**Method**: PUT  
**URL**: `/api/deductions/{id}`  
**Example**: `/api/deductions/1`  
**Body**:
```json
{
  "name": "EmployeeTax",
  "percentage": 30
}
```

**curl Command**:
```bash
curl -X PUT -H "Content-Type: application/json" -d '{
  "name": "EmployeeTax",
  "percentage": 30
}' http://localhost:8080/api/deductions/1
```

---

## 3. Payroll Management Endpoints

### Generate Payroll
**Method**: POST  
**URL**: `/api/payroll/generate`  
**Body**:
```json
{
  "month": 6,
  "year": 2025
}
```

**curl Command**:
```bash
curl -X POST -H "Content-Type: application/json" -d '{
  "month": 6,
  "year": 2025
}' http://localhost:8080/api/payroll/generate
```

**Sample Response**:
```json
[
  {
    "empId": "123",
    "name": "Mugabo Javis",
    "base": 70000,
    "house": 10000,
    "transport": 10000,
    "gross": 90000,
    "tax": 21000,
    "pansion": 4200,
    "medic": 3500,
    "others": 3500,
    "netSalary": 57800,
    "status": "pending",
    "month": "06",
    "year": 2025
  },
  {
    "empId": "234",
    "name": "Michou Michell",
    "base": 35000,
    "house": 5000,
    "transport": 5000,
    "gross": 45000,
    "tax": 10500,
    "pansion": 2100,
    "medic": 1750,
    "others": 1750,
    "netSalary": 28900,
    "status": "pending",
    "month": "06",
    "year": 2025
  }
]
```

---

### Approve Payroll
**Method**: POST  
**URL**: `/api/payroll/approve`  
**Body**:
```json
{
  "month": 6,
  "year": 2025
}
```

**curl Command**:
```bash
curl -X POST -H "Content-Type: application/json" -d '{
  "month": 6,
  "year": 2025
}' http://localhost:8080/api/payroll/approve
```

---

### Get Payslips by Month and Year
**Method**: GET  
**URL**: `/api/payroll/payslips?month={month}&year={year}`  
**Example**: `/api/payroll/payslips?month=6&year=2025`

**curl Command**:
```bash
curl "http://localhost:8080/api/payroll/payslips?month=6&year=2025"
```

**Sample Response** (after approval):
```json
[
  {
    "empId": "123",
    "name": "Mugabo Javis",
    "base": 70000,
    "house": 10000,
    "transport": 10000,
    "gross": 90000,
    "tax": 21000,
    "pansion": 4200,
    "medic": 3500,
    "others": 3500,
    "netSalary": 57800,
    "status": "paid",
    "month": "06",
    "year": 2025
  },
  {
    "empId": "234",
    "name": "Michou Michell",
    "base": 35000,
    "house": 5000,
    "transport": 5000,
    "gross": 45000,
    "tax": 10500,
    "pansion": 2100,
    "medic": 1750,
    "others": 1750,
    "netSalary": 28900,
    "status": "paid",
    "month": "06",
    "year": 2025
  }
]
```

---

### Get Payslips by Employee ID
**Method**: GET  
**URL**: `/api/payroll/payslips/employee/{employeeId}`  
**Example**: `/api/payroll/payslips/employee/1`

**curl Command**:
```bash
curl http://localhost:8080/api/payroll/payslips/employee/1
```

---

## Testing Workflow (With Math & Message Verification)
1. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

2. **Verify seeded data**
   ```bash
   # Check employees
   curl http://localhost:8080/api/employees
   
   # Check deductions (verify percentages: 30%, 6%, 5%, 5%, 14%, 14%)
   curl http://localhost:8080/api/deductions
   ```

3. **Generate payroll for June 2025**
   ```bash
   curl -X POST -H "Content-Type: application/json" -d '{"month":6,"year":2025}' http://localhost:8080/api/payroll/generate
   ```
   **Verify** in the response:
   - For Mugabo (empId=123): base=70000, house=10000, transport=10000, gross=90000, tax=21000, pansion=4200, medic=3500, others=3500, netSalary=57800, status=pending
   - For Michou (empId=234): base=35000, house=5000, transport=5000, gross=45000, tax=10500, pansion=2100, medic=1750, others=1750, netSalary=28900, status=pending

4. **Approve payroll**
   ```bash
   curl -X POST -H "Content-Type: application/json" -d '{"month":6,"year":2025}' http://localhost:8080/api/payroll/approve
   ```

5. **Verify payslip status is now "Paid"**
   ```bash
   curl "http://localhost:8080/api/payroll/payslips?month=6&year=2025"
   ```

6. **Verify messages were created!**
   ```bash
   # Get all messages
   curl http://localhost:8080/api/messages
   
   # Get messages for Mugabo (employeeId=1)
   curl http://localhost:8080/api/messages/employee/1
   
   # Get messages for June 2025
   curl http://localhost:8080/api/messages/month-year/06/2025
   ```
   **Verify message format matches**:
   ```
   Dear Mugabo, Your salary of 06/2025 from RCA 57800 has been credited to your 123 account successfully.
   ```

---

## How to Add Records Manually
You can add records manually using 3 methods: **1) API Endpoints (Postman/Swagger)**, **2) DBMS Level (PostgreSQL)**, **3) DataSeeder.java**

### Method 1: Using API Endpoints (Postman/Swagger)
- **Add new employee**: Use `POST /api/employees` (see "Employee Management Endpoints" above)
- **Add new user**: Use `POST /api/auth/signup` (for employees) or add admin manually in DB
- **Update deduction**: Use `PUT /api/deductions/{id}` (see "Deduction Management Endpoints" above)

### Method 2: DBMS Level (Directly in PostgreSQL)
Connect to your `erd_db` database using a PostgreSQL client (pgAdmin, psql, etc.) and run these SQL commands:

#### Add New Employee
```sql
INSERT INTO employee (first_name, last_name, email, district, mobile, date_of_birth)
VALUES ('John', 'Smith', 'john.smith@example.com', 'Kigali', '0780000004', '1992-03-15');
```

#### Add Employment for New Employee
```sql
INSERT INTO employment (employee_id, department, position, base_salary, status, joining_date, employee_id_ref)
VALUES ('456', 'Marketing', 'Specialist', 45000, 'Active', '2022-06-01', 3);
-- Note: Replace 3 with the id of the employee you just inserted
```

#### Add New User for Employee
```sql
-- Note: Passwords are BCrypt encrypted, generate one using an online tool or BCrypt library
-- Example hash for "password123": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH
INSERT INTO app_user (email, password, role, employee_id)
VALUES ('john.smith@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'EMPLOYEE', 3);
```

#### Update Deduction
```sql
UPDATE deduction SET percentage = 31 WHERE name = 'EmployeeTax';
```

### Method 3: DataSeeder.java (Modify the Application Main Class Seed)
You can add more default data by editing `src/main/java/rw/gov/erp/DataSeeder.java`! Just add more employees, employments, or users in the `run()` method!
