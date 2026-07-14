package com.unaj.detectface.device.service;

import com.unaj.detectface.device.dto.DeviceCreateDto;
import com.unaj.detectface.device.dto.DeviceDto;

import java.util.List;

public interface DeviceService {
    List<DeviceDto> findAll();
    DeviceDto findById(Long id);
    DeviceDto create(DeviceCreateDto deviceCreateDto);
    DeviceDto update(Long id, DeviceCreateDto deviceCreateDto);
    void delete(Long id);
}
