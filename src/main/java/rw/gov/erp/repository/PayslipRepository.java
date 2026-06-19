package rw.gov.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rw.gov.erp.entity.Employee;
import rw.gov.erp.entity.Payslip;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    boolean existsByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);
    Optional<Payslip> findByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);
    List<Payslip> findByMonthAndYear(Integer month, Integer year);
    List<Payslip> findByEmployee(Employee employee);
}
