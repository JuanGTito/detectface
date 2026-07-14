package com.unaj.detectface.notification.dto;

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
public class NotificationDto {
    private Long id;
    private Long usuarioId;
    private String usuarioEmail;
    private String titulo;
    private String mensaje;
    private boolean leido;
    private LocalDateTime fecha;
}
