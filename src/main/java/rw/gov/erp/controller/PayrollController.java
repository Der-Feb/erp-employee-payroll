package rw.gov.erp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rw.gov.erp.dto.PayrollRequest;
import rw.gov.erp.dto.PayslipResponse;
import rw.gov.erp.entity.User;
import rw.gov.erp.repository.UserRepository;
import rw.gov.erp.service.PayrollService;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PayrollController {

    private final PayrollService payrollService;
    private final UserRepository userRepository;

    @PostMapping("/generate")
    public ResponseEntity<List<PayslipResponse>> generatePayroll(@Valid @RequestBody PayrollRequest request) {
        return new ResponseEntity<>(payrollService.generatePayroll(request), HttpStatus.CREATED);
    }

    @PostMapping("/approve")
    public ResponseEntity<Void> approvePayroll(@Valid @RequestBody PayrollRequest request) {
        payrollService.approvePayroll(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/payslips")
    public ResponseEntity<List<PayslipResponse>> getPayslipsByMonthAndYear(@RequestParam Integer month, @RequestParam Integer year, Authentication authentication) {
        // Only admin can get all payslips for a month
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(payrollService.getPayslipsByMonthAndYear(month, year));
    }

    @GetMapping("/payslips/employee/{employeeId}")
    public ResponseEntity<List<PayslipResponse>> getPayslipsByEmployeeId(@PathVariable Long employeeId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // Check if employee is viewing their own payslips
            User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            if (currentUser.getEmployee() == null || !currentUser.getEmployee().getId().equals(employeeId)) {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.ok(payrollService.getPayslipsByEmployeeId(employeeId));
    }
}
