package com.unaj.detectface.detection.service;

import com.unaj.detectface.detection.dto.DetectionCreateDto;
import com.unaj.detectface.detection.dto.DetectionDto;

import java.util.List;

public interface DetectionService {
    List<DetectionDto> findAll();
    DetectionDto findById(Long id);
    DetectionDto create(DetectionCreateDto detectionCreateDto);
    DetectionDto update(Long id, DetectionCreateDto detectionCreateDto);
    void delete(Long id);
}
