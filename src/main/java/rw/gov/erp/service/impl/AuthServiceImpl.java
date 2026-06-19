package rw.gov.erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rw.gov.erp.dto.AuthResponse;
import rw.gov.erp.dto.LoginRequest;
import rw.gov.erp.dto.SignupRequest;
import rw.gov.erp.entity.Employee;
import rw.gov.erp.entity.Employment;
import rw.gov.erp.entity.User;
import rw.gov.erp.repository.EmployeeRepository;
import rw.gov.erp.repository.EmploymentRepository;
import rw.gov.erp.repository.UserRepository;
import rw.gov.erp.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        return createEmployeeUser(request, false);
    }

    @Override
    @Transactional
    public AuthResponse createEmployeeByAdmin(SignupRequest request) {
        return createEmployeeUser(request, true);
    }

    private AuthResponse createEmployeeUser(SignupRequest request, boolean allowSalaryAssignment) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create Employee
        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDistrict(request.getDistrict());
        employee.setMobile(request.getMobile());
        employee.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        Employee savedEmployee = employeeRepository.save(employee);

        // Create Employment
        Employment employment = new Employment();
        employment.setEmployeeId(resolveEmployeeId(request.getEmployeeId(), savedEmployee.getId(), allowSalaryAssignment));
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        if (allowSalaryAssignment && request.getBaseSalary() != null) {
            employment.setBaseSalary(BigDecimal.valueOf(request.getBaseSalary()));
            employment.setStatus("Active");
        } else {
            employment.setBaseSalary(null);
            employment.setStatus("Inactive");
        }
        employment.setJoiningDate(LocalDate.parse(request.getJoiningDate()));
        employment.setEmployee(savedEmployee);
        employmentRepository.save(employment);

        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("EMPLOYEE");
        user.setEmployee(savedEmployee);
        userRepository.save(user);

        return new AuthResponse("Signup successful", "EMPLOYEE");
    }

    private String resolveEmployeeId(String requestedEmployeeId, Long employeeRecordId, boolean allowRequestedEmployeeId) {
        if (allowRequestedEmployeeId && requestedEmployeeId != null && !requestedEmployeeId.isBlank()) {
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
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return new AuthResponse("Login successful", user.getRole());
    }
}
