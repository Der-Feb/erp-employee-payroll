package rw.gov.erp.service;

import rw.gov.erp.dto.PayrollRequest;
import rw.gov.erp.dto.PayslipResponse;

import java.util.List;

public interface PayrollService {
    List<PayslipResponse> generatePayroll(PayrollRequest request);
    void approvePayroll(PayrollRequest request);
    List<PayslipResponse> getPayslipsByMonthAndYear(Integer month, Integer year);
    List<PayslipResponse> getPayslipsByEmployeeId(Long employeeId);
}
