package rw.gov.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayslipResponse {
    private String empId;
    private String name;
    private BigDecimal base;
    private BigDecimal house;
    private BigDecimal transport;
    private BigDecimal gross;
    private BigDecimal tax;
    private BigDecimal pansion; // Note: sample uses "pansion" instead of "pension"
    private BigDecimal medic;
    private BigDecimal others;
    private BigDecimal netSalary;
    private String status;
    private String month;
    private Integer year;
}
