package com.unaj.detectface.camera.service;

import org.springframework.stereotype.Service;

@Service
public class CameraServiceImpl implements CameraService {
    @Override
    public void startCapture(Long deviceId) {
        // Placeholder - OpenCV se integrará posteriormente
    }

    @Override
    public void stopCapture(Long deviceId) {
        // Placeholder - OpenCV se integrará posteriormente
    }

    @Override
    public boolean isCapturing(Long deviceId) {
        return false;
    }
}
