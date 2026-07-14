package com.unaj.detectface.device.repository;

import com.unaj.detectface.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}
