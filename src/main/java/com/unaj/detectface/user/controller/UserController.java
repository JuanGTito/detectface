package com.unaj.detectface.user.controller;

import com.unaj.detectface.common.ApiResponse;
import com.unaj.detectface.user.dto.UserCreateDto;
import com.unaj.detectface.user.dto.UserDto;
import com.unaj.detectface.user.dto.UserUpdateDto;
import com.unaj.detectface.user.service.UserService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'GERENTE')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success(users, "Usuarios obtenidos con éxito"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'GERENTE', 'PSICOLOGIA', 'USUARIO')")
    public ResponseEntity<ApiResponse<UserDto>> getById(@PathVariable Long id) {
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "Usuario obtenido con éxito"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> create(@Valid @RequestBody UserCreateDto dto) {
        UserDto created = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Usuario creado con éxito"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserDto>> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto) {
        UserDto updated = userService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Usuario actualizado con éxito"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Usuario eliminado con éxito"));
    }
}
