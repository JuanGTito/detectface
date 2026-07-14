package com.unaj.detectface.device.mapper;

import com.unaj.detectface.device.dto.DeviceDto;
import com.unaj.detectface.device.entity.Device;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
    DeviceDto toDto(Device device);
    Device toEntity(DeviceDto deviceDto);
}
