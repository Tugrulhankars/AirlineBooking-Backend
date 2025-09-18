package org.airlinebooking.airlinebooking.services;

import org.airlinebooking.airlinebooking.dtos.Response;
import org.airlinebooking.airlinebooking.dtos.UserDTO;
import org.airlinebooking.airlinebooking.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User currentUser();

    Response<?> updateMyAccount(UserDTO userDTO);

    Response<List<UserDTO>> getAllPilots();

    Response<UserDTO> getAccountDetails();
}
