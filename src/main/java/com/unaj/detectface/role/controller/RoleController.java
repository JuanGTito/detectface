package com.unaj.detectface.role.controller;

import com.unaj.detectface.common.ApiResponse;
import com.unaj.detectface.role.dto.RoleDto;
import com.unaj.detectface.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAll() {
        List<RoleDto> roles = roleService.findAll();
        return ResponseEntity.ok(ApiResponse.success(roles, "Roles obtenidos con éxito"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> getById(@PathVariable Long id) {
        RoleDto role = roleService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(role, "Rol obtenido con éxito"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleDto>> create(@Valid @RequestBody RoleDto roleDto) {
        RoleDto created = roleService.create(roleDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Rol creado con éxito"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> update(@PathVariable Long id, @Valid @RequestBody RoleDto roleDto) {
        RoleDto updated = roleService.update(id, roleDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Rol actualizado con éxito"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Rol eliminado con éxito"));
    }
}
