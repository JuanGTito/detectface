package com.unaj.detectface.notification.repository;

import com.unaj.detectface.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUsuarioId(Long usuarioId);
}
