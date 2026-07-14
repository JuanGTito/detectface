package com.unaj.detectface.detection.mapper;

import com.unaj.detectface.detection.dto.DetectionDto;
import com.unaj.detectface.detection.entity.Detection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DetectionMapper {
    @Mapping(source = "device.id", target = "deviceId")
    @Mapping(source = "device.nombre", target = "deviceNombre")
    DetectionDto toDto(Detection detection);
}
