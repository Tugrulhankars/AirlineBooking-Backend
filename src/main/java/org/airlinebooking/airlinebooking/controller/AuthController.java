package org.airlinebooking.airlinebooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.airlinebooking.airlinebooking.dtos.LoginRequest;
import org.airlinebooking.airlinebooking.dtos.LoginResponse;
import org.airlinebooking.airlinebooking.dtos.RegistrationRequest;
import org.airlinebooking.airlinebooking.dtos.Response;
import org.airlinebooking.airlinebooking.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Response<?>> register(@Valid @RequestBody RegistrationRequest registrationRequest){
        return ResponseEntity.ok(authService.register(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
