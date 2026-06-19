#!/bin/bash

echo "=== ERP Payroll System - API Test Script ==="
echo ""

BASE_URL="http://localhost:8080/api"

echo "1. Getting all employees..."
curl -s "$BASE_URL/employees" | jq . || curl "$BASE_URL/employees"
echo ""
echo ""

echo "2. Getting all deductions..."
curl -s "$BASE_URL/deductions" | jq . || curl "$BASE_URL/deductions"
echo ""
echo ""

echo "3. Generating payroll for June 2025..."
curl -s -X POST -H "Content-Type: application/json" -d '{
  "month": 6,
  "year": 2025
}' "$BASE_URL/payroll/generate" | jq . || curl -X POST -H "Content-Type: application/json" -d '{
  "month": 6,
  "year": 2025
}' "$BASE_URL/payroll/generate"
echo ""
echo ""

echo "4. Getting payslips for June 2025..."
curl -s "$BASE_URL/payroll/payslips?month=6&year=2025" | jq . || curl "$BASE_URL/payroll/payslips?month=6&year=2025"
echo ""
echo ""

echo "5. Approving payroll for June 2025..."
curl -X POST -H "Content-Type: application/json" -d '{
  "month": 6,
  "year": 2025
}' "$BASE_URL/payroll/approve"
echo ""
echo ""

echo "6. Getting payslips again to check status..."
curl -s "$BASE_URL/payroll/payslips?month=6&year=2025" | jq . || curl "$BASE_URL/payroll/payslips?month=6&year=2025"
echo ""
echo ""

echo "=== Test Complete ==="
