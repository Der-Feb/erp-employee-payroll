package rw.gov.erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employment")
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @NotBlank
    private String department;

    @NotBlank
    private String position;

    @Column(name = "base_salary")
    private BigDecimal baseSalary;

    @NotBlank
    private String status; // "Active" or "Inactive"

    @NotNull
    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @OneToOne
    @JoinColumn(name = "employee_id_ref", unique = true)
    private Employee employee;
}
