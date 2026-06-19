package rw.gov.erp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rw.gov.erp.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
}
