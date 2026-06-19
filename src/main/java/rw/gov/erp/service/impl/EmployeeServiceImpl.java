package rw.gov.erp.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rw.gov.erp.dto.AssignSalaryRequest;
import rw.gov.erp.dto.EmployeeRequest;
import rw.gov.erp.entity.Employee;
import rw.gov.erp.entity.Employment;
import rw.gov.erp.repository.EmployeeRepository;
import rw.gov.erp.repository.EmploymentRepository;
import rw.gov.erp.service.EmployeeService;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;

    @Override
    public Employee createEmployee(EmployeeRequest request) {
        // Check if email already exists
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDistrict(request.getDistrict());
        employee.setMobile(request.getMobile());
        employee.setDateOfBirth(request.getDateOfBirth());

        Employee savedEmployee = employeeRepository.save(employee);

        Employment employment = new Employment();
        employment.setEmployeeId(resolveEmployeeId(request.getEmployeeId(), savedEmployee.getId()));
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        if (request.getBaseSalary() != null) {
            employment.setBaseSalary(BigDecimal.valueOf(request.getBaseSalary()));
            employment.setStatus("Active");
        } else {
            employment.setBaseSalary(null);
            employment.setStatus("Inactive");
        }
        employment.setJoiningDate(request.getJoiningDate());
        employment.setEmployee(savedEmployee);

        employmentRepository.save(employment);

        return savedEmployee;
    }

    private String resolveEmployeeId(String requestedEmployeeId, Long employeeRecordId) {
        if (requestedEmployeeId != null && !requestedEmployeeId.isBlank()) {
            if (employmentRepository.existsByEmployeeId(requestedEmployeeId)) {
                throw new RuntimeException("Employee ID already exists");
            }
            return requestedEmployeeId;
        }

        String generatedEmployeeId = String.format("EMP%05d", employeeRecordId);
        if (employmentRepository.existsByEmployeeId(generatedEmployeeId)) {
            throw new RuntimeException("Generated employee ID already exists");
        }
        return generatedEmployeeId;
    }

    @Override
    public Employment assignSalary(Long employeeId, AssignSalaryRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Employment employment = employmentRepository.findByEmployee(employee);
        if (employment == null) {
            throw new RuntimeException("Employment record not found");
        }

        employment.setBaseSalary(BigDecimal.valueOf(request.getBaseSalary()));
        employment.setStatus("Active");
        return employmentRepository.save(employment);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
    }
}
