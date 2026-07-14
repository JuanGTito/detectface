package com.unaj.detectface.device.controller;

import com.unaj.detectface.common.ApiResponse;
import com.unaj.detectface.device.dto.DeviceCreateDto;
import com.unaj.detectface.device.dto.DeviceDto;
import com.unaj.detectface.device.service.DeviceService;
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
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'GERENTE')")
    public ResponseEntity<ApiResponse<List<DeviceDto>>> getAll() {
        List<DeviceDto> devices = deviceService.findAll();
        return ResponseEntity.ok(ApiResponse.success(devices, "Dispositivos obtenidos con éxito"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'GERENTE')")
    public ResponseEntity<ApiResponse<DeviceDto>> getById(@PathVariable Long id) {
        DeviceDto device = deviceService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(device, "Dispositivo obtenido con éxito"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeviceDto>> create(@Valid @RequestBody DeviceCreateDto dto) {
        DeviceDto created = deviceService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Dispositivo creado con éxito"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeviceDto>> update(@PathVariable Long id, @Valid @RequestBody DeviceCreateDto dto) {
        DeviceDto updated = deviceService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Dispositivo actualizado con éxito"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Dispositivo eliminado con éxito"));
    }
}
