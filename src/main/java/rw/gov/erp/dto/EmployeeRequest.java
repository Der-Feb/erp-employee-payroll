package rw.gov.erp.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String district;

    @NotBlank
    private String mobile;

    @NotNull
    private LocalDate dateOfBirth;

    private String employeeId;

    @NotBlank
    private String department;

    @NotBlank
    private String position;

    @Positive
    private Double baseSalary;

    private String status;

    @NotNull
    private LocalDate joiningDate;
}
