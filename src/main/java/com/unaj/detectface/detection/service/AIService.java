package com.unaj.detectface.detection.service;

public interface AIService {
    String detectEmotion(byte[] imageBytes);
    boolean detectCellPhoneUse(byte[] imageBytes);
    double getConfidence(byte[] imageBytes);
}
