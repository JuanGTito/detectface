package com.unaj.detectface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DetectfaceApplication {

    public static void main(String[] args) {
        // Inicializar OpenCV mediante OpenPNP para cargar las librerías nativas automáticamente
        nu.pattern.OpenCV.loadShared();
        
        SpringApplication.run(DetectfaceApplication.class, args);
    }

}
