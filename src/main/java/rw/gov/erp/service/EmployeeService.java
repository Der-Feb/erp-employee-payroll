package rw.gov.erp.service;

import java.util.List;

import rw.gov.erp.dto.AssignSalaryRequest;
import rw.gov.erp.dto.EmployeeRequest;
import rw.gov.erp.entity.Employee;
import rw.gov.erp.entity.Employment;

public interface EmployeeService {
    Employee createEmployee(EmployeeRequest request);
    Employment assignSalary(Long employeeId, AssignSalaryRequest request);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
}
