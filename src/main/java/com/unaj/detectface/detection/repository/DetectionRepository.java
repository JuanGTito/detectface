package com.unaj.detectface.detection.repository;

import com.unaj.detectface.detection.entity.Detection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetectionRepository extends JpaRepository<Detection, Long> {
}
