package com.unaj.detectface.notification.service;

import com.unaj.detectface.exception.ResourceNotFoundException;
import com.unaj.detectface.notification.dto.NotificationCreateDto;
import com.unaj.detectface.notification.dto.NotificationDto;
import com.unaj.detectface.notification.entity.Notification;
import com.unaj.detectface.notification.mapper.NotificationMapper;
import com.unaj.detectface.notification.repository.NotificationRepository;
import com.unaj.detectface.user.entity.User;
import com.unaj.detectface.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> findAll() {
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> findByUsuarioId(Long usuarioId) {
        return notificationRepository.findByUsuarioId(usuarioId).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDto findById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));
        return notificationMapper.toDto(notification);
    }

    @Override
    public NotificationDto create(NotificationCreateDto dto) {
        User usuario = userRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        Notification notification = Notification.builder()
                .usuario(usuario)
                .titulo(dto.getTitulo())
                .mensaje(dto.getMensaje())
                .leido(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationDto result = notificationMapper.toDto(saved);

        // Enviar por WebSocket a los suscriptores
        try {
            messagingTemplate.convertAndSend("/topic/notifications", result);
            messagingTemplate.convertAndSend("/topic/notifications-" + usuario.getId(), result);
        } catch (Exception e) {
            // Ignorar errores en ambiente de desarrollo sin broker activo
        }

        return result;
    }

    @Override
    public NotificationDto update(Long id, NotificationCreateDto dto) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));

        User usuario = userRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        notification.setUsuario(usuario);
        notification.setTitulo(dto.getTitulo());
        notification.setMensaje(dto.getMensaje());

        Notification updated = notificationRepository.save(notification);
        return notificationMapper.toDto(updated);
    }

    @Override
    public NotificationDto markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));

        notification.setLeido(true);
        Notification updated = notificationRepository.save(notification);
        return notificationMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));
        notificationRepository.delete(notification);
    }
}
