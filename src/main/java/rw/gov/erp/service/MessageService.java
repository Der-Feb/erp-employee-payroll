package rw.gov.erp.service;

import java.util.List;

import rw.gov.erp.dto.MessageResponse;

public interface MessageService {
    List<MessageResponse> getAllMessages();
    List<MessageResponse> getMessagesByEmployeeId(Long employeeId);
    List<MessageResponse> getMessagesByMonthYear(String monthYear);
    List<MessageResponse> getMessagesByEmployeeIdAndMonthYear(Long employeeId, String monthYear);
}