package org.airlinebooking.airlinebooking.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.airlinebooking.airlinebooking.dtos.LoginRequest;
import org.airlinebooking.airlinebooking.dtos.LoginResponse;
import org.airlinebooking.airlinebooking.dtos.RegistrationRequest;
import org.airlinebooking.airlinebooking.dtos.Response;
import org.airlinebooking.airlinebooking.entities.Role;
import org.airlinebooking.airlinebooking.entities.User;
import org.airlinebooking.airlinebooking.enums.AuthMethod;
import org.airlinebooking.airlinebooking.exceptions.BadRequestException;
import org.airlinebooking.airlinebooking.exceptions.NotFoundException;
import org.airlinebooking.airlinebooking.repositories.RoleRepository;
import org.airlinebooking.airlinebooking.repositories.UserRepository;
import org.airlinebooking.airlinebooking.security.JwtUtils;
import org.airlinebooking.airlinebooking.services.AuthenticationService;
import org.airlinebooking.airlinebooking.services.EmailNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;
    private final EmailNotificationService emailNotificationService;

    @Override
    public Response<?> register(RegistrationRequest registrationRequest) {
        log.info("Inside Register");

        if (userRepository.existsByEmail(registrationRequest.getEmail())){
            throw new BadRequestException("Email already exist");
        }

        List<Role> userRoles;
        if (registrationRequest.getRoles() != null && !registrationRequest.getRoles().isEmpty()){
            userRoles = registrationRequest.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName.toUpperCase())
                            .orElseThrow(()-> new NotFoundException("Role " + roleName + "Not Found")))
                    .toList();
        }else{
            Role defaultRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(()-> new NotFoundException("Role CUSTOMER DOESN'T EXISTS"));
            userRoles = List.of(defaultRole);
        }

        User userToSave = new User();
        userToSave.setName(registrationRequest.getName());
        userToSave.setEmail(registrationRequest.getEmail());
        userToSave.setPhoneNumber(registrationRequest.getPhoneNumber());
        userToSave.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        userToSave.setRoles(userRoles);
        userToSave.setCreatedAt(LocalDateTime.now());
        userToSave.setUpdatedAt(LocalDateTime.now());
        userToSave.setProvider(AuthMethod.LOCAL);
        userToSave.setActive(true);

        User savedUser = userRepository.save(userToSave);

        emailNotificationService.sendWelcomeEmail(savedUser);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("user registered sucessfully")
                .build();

    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {
        log.info("Inside login()");
        User user=userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()->new NotFoundException("Email Not Found"));

        if (!user.isActive()){
            throw new NotFoundException("Acount Not Active, Please reach Out to Customer Care...");

        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadRequestException("Invalid Password");
        }
        String token = jwtUtils.generateToken(user.getEmail());

        List<String > roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setRoles(roleNames);

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login Successful")
                .data(loginResponse)
                .build();
    }
}
