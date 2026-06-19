package rw.gov.erp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rw.gov.erp.dto.MessageResponse;
import rw.gov.erp.entity.Employment;
import rw.gov.erp.entity.Message;
import rw.gov.erp.repository.EmploymentRepository;
import rw.gov.erp.repository.MessageRepository;
import rw.gov.erp.service.MessageService;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final EmploymentRepository employmentRepository;

    @Override
    public List<MessageResponse> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponse> getMessagesByEmployeeId(Long employeeId) {
        return messageRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponse> getMessagesByMonthYear(String monthYear) {
        return messageRepository.findByMonthYear(monthYear).stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponse> getMessagesByEmployeeIdAndMonthYear(Long employeeId, String monthYear) {
        return messageRepository.findByEmployeeIdAndMonthYear(employeeId, monthYear).stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());
    }

    private MessageResponse convertToMessageResponse(Message message) {
        Employment employment = employmentRepository.findByEmployee(message.getEmployee());
        String employeeId = (employment != null) ? employment.getEmployeeId() : null;
        
        return MessageResponse.builder()
                .id(message.getId())
                .employeeName(message.getEmployee().getFirstName() + " " + message.getEmployee().getLastName())
                .employeeId(employeeId)
                .message(message.getMessage())
                .monthYear(message.getMonthYear())
                .sentAt(message.getSentAt())
                .build();
    }
}