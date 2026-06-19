package rw.gov.erp.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private String employeeName;
    private String employeeId;
    private String message;
    private String monthYear;
    private LocalDateTime sentAt;
}
