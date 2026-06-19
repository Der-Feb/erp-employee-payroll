package rw.gov.erp.dto;

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
public class SignupRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String district;
    @NotBlank
    private String mobile;
    @NotNull
    private String dateOfBirth;
    private String employeeId;
    @NotBlank
    private String department;
    @NotBlank
    private String position;
    @Positive
    private Double baseSalary;
    @NotNull
    private String joiningDate;
}
