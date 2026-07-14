package com.unaj.detectface.device.dto;

import com.unaj.detectface.device.entity.DeviceStatus;
import com.unaj.detectface.device.entity.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class DeviceCreateDto {
    @NotBlank(message = "El nombre del dispositivo es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotNull(message = "El tipo de dispositivo es obligatorio")
    private DeviceType tipo;

    @Size(max = 50, message = "La dirección IP no puede superar los 50 caracteres")
    private String ip;

    private Integer puerto;

    @Size(max = 100, message = "El usuario no puede superar los 100 caracteres")
    private String usuario;

    private String password;

    private String rtspUrl;

    private Integer indiceWebcam;

    @NotNull(message = "El estado del dispositivo es obligatorio")
    private DeviceStatus estado;
}
