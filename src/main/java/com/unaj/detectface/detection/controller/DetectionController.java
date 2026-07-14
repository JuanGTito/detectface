package com.unaj.detectface.detection.controller;

import com.unaj.detectface.common.ApiResponse;
import com.unaj.detectface.detection.dto.DetectionCreateDto;
import com.unaj.detectface.detection.dto.DetectionDto;
import com.unaj.detectface.detection.service.DetectionService;
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
@RequestMapping("/detections")
@RequiredArgsConstructor
public class DetectionController {

    private final DetectionService detectionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'GERENTE', 'PSICOLOGIA')")
    public ResponseEntity<ApiResponse<List<DetectionDto>>> getAll() {
        List<DetectionDto> detections = detectionService.findAll();
        return ResponseEntity.ok(ApiResponse.success(detections, "Detecciones obtenidas con éxito"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'GERENTE', 'PSICOLOGIA')")
    public ResponseEntity<ApiResponse<DetectionDto>> getById(@PathVariable Long id) {
        DetectionDto detection = detectionService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(detection, "Detección obtenida con éxito"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public ResponseEntity<ApiResponse<DetectionDto>> create(@Valid @RequestBody DetectionCreateDto dto) {
        DetectionDto created = detectionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Detección registrada con éxito"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DetectionDto>> update(@PathVariable Long id, @Valid @RequestBody DetectionCreateDto dto) {
        DetectionDto updated = detectionService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Detección actualizada con éxito"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        detectionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Detección eliminada con éxito"));
    }
}
