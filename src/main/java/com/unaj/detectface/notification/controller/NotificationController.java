package com.unaj.detectface.notification.controller;

import com.unaj.detectface.common.ApiResponse;
import com.unaj.detectface.notification.dto.NotificationCreateDto;
import com.unaj.detectface.notification.dto.NotificationDto;
import com.unaj.detectface.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'GERENTE')")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getAll() {
        List<NotificationDto> notifications = notificationService.findAll();
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notificaciones obtenidas con éxito"));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getByUserId(@PathVariable Long userId) {
        List<NotificationDto> notifications = notificationService.findByUsuarioId(userId);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notificaciones del usuario obtenidas con éxito"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @notificationServiceImpl.findById(#id).usuarioId == authentication.principal.id")
    public ResponseEntity<ApiResponse<NotificationDto>> getById(@PathVariable Long id) {
        NotificationDto notification = notificationService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(notification, "Notificación obtenida con éxito"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'GERENTE')")
    public ResponseEntity<ApiResponse<NotificationDto>> create(@Valid @RequestBody NotificationCreateDto dto) {
        NotificationDto created = notificationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Notificación creada con éxito"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationDto>> update(@PathVariable Long id, @Valid @RequestBody NotificationCreateDto dto) {
        NotificationDto updated = notificationService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Notificación actualizada con éxito"));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasRole('ADMIN') or @notificationServiceImpl.findById(#id).usuarioId == authentication.principal.id")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(@PathVariable Long id) {
        NotificationDto updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(updated, "Notificación marcada como leída"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Notificación eliminada con éxito"));
    }
}
