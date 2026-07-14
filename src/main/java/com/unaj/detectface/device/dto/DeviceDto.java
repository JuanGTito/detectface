package com.unaj.detectface.device.dto;

import com.unaj.detectface.device.entity.DeviceStatus;
import com.unaj.detectface.device.entity.DeviceType;
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
public class DeviceDto {
    private Long id;
    private String nombre;
    private DeviceType tipo;
    private String ip;
    private Integer puerto;
    private String usuario;
    private String password;
    private String rtspUrl;
    private Integer indiceWebcam;
    private DeviceStatus estado;
    private LocalDateTime createdAt;
}
