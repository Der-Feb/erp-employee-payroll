package rw.gov.erp.service;

import rw.gov.erp.dto.LoginRequest;
import rw.gov.erp.dto.SignupRequest;
import rw.gov.erp.dto.AuthResponse;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse createEmployeeByAdmin(SignupRequest request);
    AuthResponse login(LoginRequest request);
}
