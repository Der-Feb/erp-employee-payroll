package rw.gov.erp.service;

import java.util.List;

import rw.gov.erp.entity.Deduction;

public interface DeductionService {
    List<Deduction> getAllDeductions();
    Deduction updateDeduction(Long id, Deduction deduction);
}
