package org.airlinebooking.airlinebooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.airlinebooking.airlinebooking.dtos.Response;
import org.airlinebooking.airlinebooking.dtos.RoleDTO;
import org.airlinebooking.airlinebooking.services.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<?>> createRole(@Valid @RequestBody RoleDTO roleDTO){
        return ResponseEntity.ok(roleService.createRole(roleDTO));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<?>> updateRole( @Valid @RequestBody RoleDTO roleDTO){
        return ResponseEntity.ok(roleService.update(roleDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PILOT')")
    public  ResponseEntity<Response<?>> getAllRoles(){
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}

