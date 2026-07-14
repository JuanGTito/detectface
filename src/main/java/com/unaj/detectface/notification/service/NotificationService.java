package com.unaj.detectface.notification.service;

import com.unaj.detectface.notification.dto.NotificationCreateDto;
import com.unaj.detectface.notification.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> findAll();
    List<NotificationDto> findByUsuarioId(Long usuarioId);
    NotificationDto findById(Long id);
    NotificationDto create(NotificationCreateDto notificationCreateDto);
    NotificationDto update(Long id, NotificationCreateDto notificationCreateDto);
    NotificationDto markAsRead(Long id);
    void delete(Long id);
}
