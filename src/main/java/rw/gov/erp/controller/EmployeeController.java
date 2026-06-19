package rw.gov.erp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rw.gov.erp.dto.AssignSalaryRequest;
import rw.gov.erp.dto.EmployeeRequest;
import rw.gov.erp.entity.Employee;
import rw.gov.erp.entity.Employment;
import rw.gov.erp.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        return new ResponseEntity<>(employeeService.createEmployee(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/salary")
    public ResponseEntity<Employment> assignSalary(@PathVariable Long id, @Valid @RequestBody AssignSalaryRequest request) {
        return ResponseEntity.ok(employeeService.assignSalary(id, request));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }
}
