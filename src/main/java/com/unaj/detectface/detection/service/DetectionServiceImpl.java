package com.unaj.detectface.detection.service;

import com.unaj.detectface.detection.dto.DetectionCreateDto;
import com.unaj.detectface.detection.dto.DetectionDto;
import com.unaj.detectface.detection.entity.Detection;
import com.unaj.detectface.detection.mapper.DetectionMapper;
import com.unaj.detectface.detection.repository.DetectionRepository;
import com.unaj.detectface.device.entity.Device;
import com.unaj.detectface.device.repository.DeviceRepository;
import com.unaj.detectface.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DetectionServiceImpl implements DetectionService {

    private final DetectionRepository detectionRepository;
    private final DeviceRepository deviceRepository;
    private final DetectionMapper detectionMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<DetectionDto> findAll() {
        return detectionRepository.findAll().stream()
                .map(detectionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DetectionDto findById(Long id) {
        Detection detection = detectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detección no encontrada con ID: " + id));
        return detectionMapper.toDto(detection);
    }

    @Override
    public DetectionDto create(DetectionCreateDto dto) {
        Device device = deviceRepository.findById(dto.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo no encontrado con ID: " + dto.getDeviceId()));

        Detection detection = Detection.builder()
                .device(device)
                .emocion(dto.getEmocion())
                .usoCelular(dto.getUsoCelular())
                .confianza(dto.getConfianza())
                .imagen(dto.getImagen())
                .build();

        Detection saved = detectionRepository.save(detection);
        DetectionDto result = detectionMapper.toDto(saved);

        // Enviar vía WebSocket en tiempo real
        try {
            messagingTemplate.convertAndSend("/topic/detections", result);
        } catch (Exception e) {
            // Ignorar errores de websocket en arranque/testeo
        }

        return result;
    }

    @Override
    public DetectionDto update(Long id, DetectionCreateDto dto) {
        Detection detection = detectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detección no encontrada con ID: " + id));

        Device device = deviceRepository.findById(dto.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo no encontrado con ID: " + dto.getDeviceId()));

        detection.setDevice(device);
        detection.setEmocion(dto.getEmocion());
        detection.setUsoCelular(dto.getUsoCelular());
        detection.setConfianza(dto.getConfianza());
        detection.setImagen(dto.getImagen());

        Detection updated = detectionRepository.save(detection);
        return detectionMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        Detection detection = detectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detección no encontrada con ID: " + id));
        detectionRepository.delete(detection);
    }
}
