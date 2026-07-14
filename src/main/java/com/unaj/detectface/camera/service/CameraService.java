package com.unaj.detectface.camera.service;

public interface CameraService {
    void startCapture(Long deviceId);
    void stopCapture(Long deviceId);
    boolean isCapturing(Long deviceId);
}
