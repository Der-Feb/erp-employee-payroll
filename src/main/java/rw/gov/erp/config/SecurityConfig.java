package rw.gov.erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import rw.gov.erp.repository.UserRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        // Admin-only endpoints
                        .requestMatchers("/api/employees/**").hasRole("ADMIN")
                        .requestMatchers("/api/deductions/**").hasRole("ADMIN")
                        .requestMatchers("/api/payroll/generate").hasRole("ADMIN")
                        .requestMatchers("/api/payroll/approve").hasRole("ADMIN")
                        .requestMatchers("/api/payroll/payslips").hasRole("ADMIN")
                        // Employee endpoints (only for authenticated users)
                        .requestMatchers("/api/messages/employee/**").authenticated()
                        .requestMatchers("/api/messages/month-year/**").authenticated()
                        .requestMatchers("/api/payroll/payslips/employee/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            rw.gov.erp.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return User.withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
}
