package rw.gov.erp.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rw.gov.erp.dto.PayrollRequest;
import rw.gov.erp.dto.PayslipResponse;
import rw.gov.erp.entity.Deduction;
import rw.gov.erp.entity.Employee;
import rw.gov.erp.entity.Employment;
import rw.gov.erp.entity.Payslip;
import rw.gov.erp.repository.DeductionRepository;
import rw.gov.erp.repository.EmploymentRepository;
import rw.gov.erp.repository.PayslipRepository;
import rw.gov.erp.service.PayrollService;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayslipRepository payslipRepository;
    private final EmploymentRepository employmentRepository;
    private final DeductionRepository deductionRepository;

    @Override
    @Transactional
    public List<PayslipResponse> generatePayroll(PayrollRequest request) {
        List<Employment> activeEmployments = employmentRepository.findByStatus("Active");
        List<PayslipResponse> payslipResponses = new ArrayList<>();

        BigDecimal housePercentage = getDeductionPercentage("House");
        BigDecimal transportPercentage = getDeductionPercentage("Transport");
        BigDecimal taxPercentage = getDeductionPercentage("EmployeeTax");
        BigDecimal pensionPercentage = getDeductionPercentage("Pension");
        BigDecimal medicalPercentage = getDeductionPercentage("MedicalInsurance");
        BigDecimal othersPercentage = getDeductionPercentage("Others");

        for (Employment employment : activeEmployments) {
            Employee employee = employment.getEmployee();

            if (payslipRepository.existsByEmployeeAndMonthAndYear(employee, request.getMonth(), request.getYear())) {
                continue; // Skip if already exists
            }

            BigDecimal baseSalary = employment.getBaseSalary();

            // Calculate allowances - round to nearest 1000
            BigDecimal rawHouse = baseSalary.multiply(housePercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
            BigDecimal rawTransport = baseSalary.multiply(transportPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
            
            // Round to nearest 1000
            BigDecimal thousand = BigDecimal.valueOf(1000);
            BigDecimal house = rawHouse.divide(thousand, 0, RoundingMode.HALF_UP).multiply(thousand);
            BigDecimal transport = rawTransport.divide(thousand, 0, RoundingMode.HALF_UP).multiply(thousand);
            
            BigDecimal grossSalary = baseSalary.add(house).add(transport);

            // Calculate deductions - NO ROUNDING (keep 2 decimal places)
            BigDecimal tax = baseSalary.multiply(taxPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
            BigDecimal pension = baseSalary.multiply(pensionPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
            BigDecimal medical = baseSalary.multiply(medicalPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
            BigDecimal others = baseSalary.multiply(othersPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
            BigDecimal totalDeductions = tax.add(pension).add(medical).add(others);
            BigDecimal netSalary = grossSalary.subtract(totalDeductions);

            Payslip payslip = new Payslip();
            payslip.setEmployee(employee);
            payslip.setBaseSalary(baseSalary);
            payslip.setHouse(house);
            payslip.setTransport(transport);
            payslip.setGrossSalary(grossSalary);
            payslip.setTax(tax);
            payslip.setPension(pension);
            payslip.setMedical(medical);
            payslip.setOthers(others);
            payslip.setNetSalary(netSalary);
            payslip.setStatus("Pending");
            payslip.setMonth(request.getMonth());
            payslip.setYear(request.getYear());

            Payslip savedPayslip = payslipRepository.save(payslip);
            payslipResponses.add(convertToPayslipResponse(savedPayslip, employment));
        }

        return payslipResponses;
    }

    @Override
    @Transactional
    public void approvePayroll(PayrollRequest request) {
        List<Payslip> payslips = payslipRepository.findByMonthAndYear(request.getMonth(), request.getYear());
        for (Payslip payslip : payslips) {
            if (payslip.getStatus().equals("Pending")) {
                // Set status to "Paid" to trigger UPDATE statement
                // The database trigger will detect OLD.status = 'Pending' and:
                // 1. Create the message record
                // 2. Confirm the status is set to "Paid"
                payslip.setStatus("Paid");
                payslipRepository.save(payslip);
            }
        }
    }

    @Override
    public List<PayslipResponse> getPayslipsByMonthAndYear(Integer month, Integer year) {
        List<Payslip> payslips = payslipRepository.findByMonthAndYear(month, year);
        List<PayslipResponse> responses = new ArrayList<>();
        for (Payslip payslip : payslips) {
            Employment employment = employmentRepository.findByEmployee(payslip.getEmployee());
            responses.add(convertToPayslipResponse(payslip, employment));
        }
        return responses;
    }

    @Override
    public List<PayslipResponse> getPayslipsByEmployeeId(Long employeeId) {
        Employee employee = new Employee();
        employee.setId(employeeId);
        List<Payslip> payslips = payslipRepository.findByEmployee(employee);
        List<PayslipResponse> responses = new ArrayList<>();
        for (Payslip payslip : payslips) {
            Employment employment = employmentRepository.findByEmployee(payslip.getEmployee());
            responses.add(convertToPayslipResponse(payslip, employment));
        }
        return responses;
    }

    private BigDecimal getDeductionPercentage(String name) {
        Deduction deduction = deductionRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Deduction not found: " + name));
        return deduction.getPercentage();
    }

    private PayslipResponse convertToPayslipResponse(Payslip payslip, Employment employment) {
        String month = String.format("%02d", payslip.getMonth());
        return PayslipResponse.builder()
                .empId(employment.getEmployeeId())
                .name(payslip.getEmployee().getFirstName() + " " + payslip.getEmployee().getLastName())
                .base(payslip.getBaseSalary())
                .house(payslip.getHouse())
                .transport(payslip.getTransport())
                .gross(payslip.getGrossSalary())
                .tax(payslip.getTax())
                .pansion(payslip.getPension()) // Match sample spelling "pansion"
                .medic(payslip.getMedical())
                .others(payslip.getOthers())
                .netSalary(payslip.getNetSalary())
                .status(payslip.getStatus().toLowerCase()) // Match sample lowercase "pending"
                .month(month)
                .year(payslip.getYear())
                .build();
    }
}
