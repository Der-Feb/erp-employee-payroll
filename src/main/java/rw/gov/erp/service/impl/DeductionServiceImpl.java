package rw.gov.erp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rw.gov.erp.entity.Deduction;
import rw.gov.erp.repository.DeductionRepository;
import rw.gov.erp.service.DeductionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionServiceImpl implements DeductionService {

    private final DeductionRepository deductionRepository;

    @Override
    public List<Deduction> getAllDeductions() {
        return deductionRepository.findAll();
    }

    @Override
    public Deduction updateDeduction(Long id, Deduction deduction) {
        Deduction existingDeduction = deductionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction not found"));
        existingDeduction.setName(deduction.getName());
        existingDeduction.setPercentage(deduction.getPercentage());
        return deductionRepository.save(existingDeduction);
    }
}
