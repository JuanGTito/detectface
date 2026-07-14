package com.unaj.detectface.detection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectionCreateDto {
    @NotNull(message = "El ID del dispositivo es obligatorio")
    private Long deviceId;

    @NotBlank(message = "La emoción es obligatoria")
    private String emocion;

    @NotNull(message = "El indicador de uso de celular es obligatorio")
    private Boolean usoCelular;

    @NotNull(message = "La confianza es obligatoria")
    private Double confianza;

    @NotBlank(message = "La imagen en Base64 es obligatoria")
    private String imagen;
}
