package rw.gov.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String message;

    @NotNull
    @Column(name = "month_year")
    private String monthYear;

    @NotNull
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
