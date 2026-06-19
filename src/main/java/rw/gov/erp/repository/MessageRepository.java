package rw.gov.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rw.gov.erp.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByEmployeeId(Long employeeId);
    List<Message> findByMonthYear(String monthYear);
    List<Message> findByEmployeeIdAndMonthYear(Long employeeId, String monthYear);
}
