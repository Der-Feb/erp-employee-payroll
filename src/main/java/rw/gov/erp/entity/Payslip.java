package rw.gov.erp.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "payslip",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payslip_employee_month_year",
                        columnNames = {"employee_id", "month", "year"}
                )
        }
)
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;

    @NotNull
    @Column(name = "base_salary")
    private BigDecimal baseSalary;

    @NotNull
    private BigDecimal house;

    @NotNull
    private BigDecimal transport;

    @NotNull
    @Column(name = "gross_salary")
    private BigDecimal grossSalary;

    @NotNull
    private BigDecimal tax;

    @NotNull
    private BigDecimal pension;

    @NotNull
    private BigDecimal medical;

    @NotNull
    private BigDecimal others;

    @NotNull
    @Column(name = "net_salary")
    private BigDecimal netSalary;

    @NotNull
    private String status; // "Pending" or "Paid"

    @NotNull
    private Integer month;

    @NotNull
    private Integer year;
}
