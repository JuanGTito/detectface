package com.unaj.detectface.device.service;

import com.unaj.detectface.device.dto.DeviceCreateDto;
import com.unaj.detectface.device.dto.DeviceDto;
import com.unaj.detectface.device.entity.Device;
import com.unaj.detectface.device.mapper.DeviceMapper;
import com.unaj.detectface.device.repository.DeviceRepository;
import com.unaj.detectface.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DeviceDto> findAll() {
        return deviceRepository.findAll().stream()
                .map(deviceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceDto findById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo no encontrado con ID: " + id));
        return deviceMapper.toDto(device);
    }

    @Override
    public DeviceDto create(DeviceCreateDto dto) {
        Device device = Device.builder()
                .nombre(dto.getNombre())
                .tipo(dto.getTipo())
                .ip(dto.getIp())
                .puerto(dto.getPuerto())
                .usuario(dto.getUsuario())
                .password(dto.getPassword())
                .rtspUrl(dto.getRtspUrl())
                .indiceWebcam(dto.getIndiceWebcam())
                .estado(dto.getEstado())
                .build();

        Device saved = deviceRepository.save(device);
        return deviceMapper.toDto(saved);
    }

    @Override
    public DeviceDto update(Long id, DeviceCreateDto dto) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo no encontrado con ID: " + id));

        device.setNombre(dto.getNombre());
        device.setTipo(dto.getTipo());
        device.setIp(dto.getIp());
        device.setPuerto(dto.getPuerto());
        device.setUsuario(dto.getUsuario());
        device.setPassword(dto.getPassword());
        device.setRtspUrl(dto.getRtspUrl());
        device.setIndiceWebcam(dto.getIndiceWebcam());
        device.setEstado(dto.getEstado());

        Device updated = deviceRepository.save(device);
        return deviceMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo no encontrado con ID: " + id));
        deviceRepository.delete(device);
    }
}
