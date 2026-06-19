package rw.gov.erp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import rw.gov.erp.dto.MessageResponse;
import rw.gov.erp.entity.User;
import rw.gov.erp.repository.UserRepository;
import rw.gov.erp.service.MessageService;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MessageController {
    private final MessageService messageService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getAllMessages(Authentication authentication) {
        // Only admin can get all messages
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<MessageResponse>> getMessagesByEmployeeId(@PathVariable Long employeeId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // Check if employee is viewing their own messages
            User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            if (currentUser.getEmployee() == null || !currentUser.getEmployee().getId().equals(employeeId)) {
                return ResponseEntity.status(403).build();
            }
        }

        return ResponseEntity.ok(messageService.getMessagesByEmployeeId(employeeId));
    }

    @GetMapping("/month-year/{monthYear}")
    public ResponseEntity<List<MessageResponse>> getMessagesByMonthYear(@PathVariable String monthYear, Authentication authentication) {
        return getMessagesForMonthYear(monthYear, authentication);
    }

    @GetMapping("/month-year/{month}/{year}")
    public ResponseEntity<List<MessageResponse>> getMessagesByMonthAndYear(@PathVariable String month, @PathVariable String year, Authentication authentication) {
        return getMessagesForMonthYear(month + "/" + year, authentication);
    }

    private ResponseEntity<List<MessageResponse>> getMessagesForMonthYear(String monthYear, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<MessageResponse> messages;
        if (isAdmin) {
            messages = messageService.getMessagesByMonthYear(monthYear);
        } else {
            // Only get employee's own messages for that month
            User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            if (currentUser.getEmployee() == null) {
                return ResponseEntity.ok(List.of());
            }
            messages = messageService.getMessagesByEmployeeIdAndMonthYear(currentUser.getEmployee().getId(), monthYear);
        }

        return ResponseEntity.ok(messages);
    }
}
