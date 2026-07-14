package com.unaj.detectface.detection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectionDto {
    private Long id;
    private Long deviceId;
    private String deviceNombre;
    private String emocion;
    private Boolean usoCelular;
    private Double confianza;
    private String imagen;
    private LocalDateTime fecha;
}
