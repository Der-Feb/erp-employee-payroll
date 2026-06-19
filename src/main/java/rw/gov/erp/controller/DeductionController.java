package rw.gov.erp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rw.gov.erp.entity.Deduction;
import rw.gov.erp.service.DeductionService;

@RestController
@RequestMapping("/api/deductions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DeductionController {

    private final DeductionService deductionService;

    @GetMapping
    public ResponseEntity<List<Deduction>> getAllDeductions() {
        return ResponseEntity.ok(deductionService.getAllDeductions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deduction> updateDeduction(@PathVariable Long id, @Valid @RequestBody Deduction deduction) {
        return ResponseEntity.ok(deductionService.updateDeduction(id, deduction));
    }
}
