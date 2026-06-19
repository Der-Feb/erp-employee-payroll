package rw.gov.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rw.gov.erp.entity.Employee;
import rw.gov.erp.entity.Employment;

public interface EmploymentRepository extends JpaRepository<Employment, Long> {
    List<Employment> findByStatus(String status);
    Employment findByEmployeeId(String employeeId);
    boolean existsByEmployeeId(String employeeId);
    Employment findByEmployee(Employee employee);
}
