package rw.gov.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.erp.entity.Deduction;

import java.util.Optional;

public interface DeductionRepository extends JpaRepository<Deduction, Long> {
    Optional<Deduction> findByName(String name);
}
