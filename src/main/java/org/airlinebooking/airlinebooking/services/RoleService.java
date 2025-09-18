package org.airlinebooking.airlinebooking.services;

import org.airlinebooking.airlinebooking.dtos.Response;
import org.airlinebooking.airlinebooking.dtos.RoleDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {
    Response<?> createRole(RoleDTO roleDTO);
    Response<?> update(RoleDTO roleDTO);
    Response<List<RoleDTO>> getAllRoles();

}
