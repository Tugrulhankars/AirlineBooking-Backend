package org.airlinebooking.airlinebooking.services;

import org.airlinebooking.airlinebooking.dtos.LoginRequest;
import org.airlinebooking.airlinebooking.dtos.LoginResponse;
import org.airlinebooking.airlinebooking.dtos.RegistrationRequest;
import org.airlinebooking.airlinebooking.dtos.Response;
import org.springframework.stereotype.Service;

public interface AuthenticationService {
    Response<?> register(RegistrationRequest registrationRequest);
    Response<LoginResponse> login(LoginRequest loginRequest);
}
