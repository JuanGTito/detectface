package com.unaj.detectface.notification.mapper;

import com.unaj.detectface.notification.dto.NotificationDto;
import com.unaj.detectface.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.email", target = "usuarioEmail")
    NotificationDto toDto(Notification notification);
}
