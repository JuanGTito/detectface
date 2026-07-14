package com.unaj.detectface.detection.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Collections;

@Service
public class AIServiceImpl implements AIService {

    private static final Logger log = LoggerFactory.getLogger(AIServiceImpl.class);

    private OrtEnvironment env;
    private OrtSession yoloSession;
    private OrtSession emotionSession;
    private CascadeClassifier faceClassifier;

    private final String[] EMOCIONES = {"angry", "disgust", "fear", "happy", "sad", "surprise", "neutral"};
    private final int CLASE_CELULAR = 67; // ID de "cell phone" en COCO

    @PostConstruct
    public void init() {
        try {
            log.info("[+] Inicializando modelos de Inteligencia Artificial (ONNX)...");
            
            // Cargar las librerías nativas de OpenCV
            nu.pattern.OpenCV.loadShared();
            
            this.env = OrtEnvironment.getEnvironment();

            // Cargar archivos de recursos a archivos temporales para ONNX / OpenCV
            String yoloPath = loadResourceToTempFile("models/yolov8n.onnx");
            String emotionPath = loadResourceToTempFile("models/emotion_model.onnx");
            String cascadePath = loadResourceToTempFile("models/haarcascade_frontalface_default.xml");

            // Crear las sesiones ONNX
            this.yoloSession = env.createSession(yoloPath, new OrtSession.SessionOptions());
            this.emotionSession = env.createSession(emotionPath, new OrtSession.SessionOptions());

            // Crear el detector facial de OpenCV
            this.faceClassifier = new CascadeClassifier(cascadePath);

            if (faceClassifier.empty()) {
                throw new RuntimeException("No se pudo cargar el clasificador Haar Cascade para rostros.");
            }

            log.info("[+] Modelos ONNX y Clasificador Haar Cascade cargados con éxito.");
        } catch (Exception e) {
            log.error("[-] Error al inicializar los modelos de IA: ", e);
            throw new RuntimeException("Error crítico al cargar modelos de detección", e);
        }
    }

    @Override
    public String detectEmotion(byte[] imageBytes) {
        MatOfByte matOfByte = new MatOfByte(imageBytes);
        Mat img = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR);
        
        if (img.empty()) {
            matOfByte.release();
            return "DESCONOCIDA";
        }

        Mat gray = new Mat();
        Mat faceROI = null;
        Mat faceResized = null;
        MatOfRect faces = new MatOfRect();

        try {
            // 1. Convertir a escala de grises
            Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

            // 2. Detectar rostros
            faceClassifier.detectMultiScale(gray, faces);
            Rect[] facesArray = faces.toArray();

            if (facesArray.length == 0) {
                return "NEUTRAL"; // Fallback si no detecta rostros
            }

            // 3. Tomar el primer rostro detectado
            Rect faceRect = facesArray[0];
            faceROI = new Mat(gray, faceRect);

            // 4. Redimensionar a 48x48
            faceResized = new Mat();
            Imgproc.resize(faceROI, faceResized, new Size(48, 48));

            // 5. Normalizar [0-255] -> [0.0-1.0] e introducir al buffer
            float[] floatData = new float[48 * 48];
            for (int i = 0; i < 48; i++) {
                for (int j = 0; j < 48; j++) {
                    floatData[i * 48 + j] = (float) (faceResized.get(i, j)[0] / 255.0);
                }
            }

            FloatBuffer buffer = FloatBuffer.wrap(floatData);
            long[] shape = new long[]{1, 48, 48, 1};

            // 6. Inferencia ONNX
            try (OnnxTensor inputTensor = OnnxTensor.createTensor(env, buffer, shape)) {
                try (OrtSession.Result results = emotionSession.run(Collections.singletonMap("inputs", inputTensor))) {
                    float[][] output = (float[][]) results.get(0).getValue();

                    // Buscar el índice con mayor probabilidad (ArgMax)
                    int maxIdx = 0;
                    float maxVal = output[0][0];
                    for (int i = 1; i < output[0].length; i++) {
                        if (output[0][i] > maxVal) {
                            maxVal = output[0][i];
                            maxIdx = i;
                        }
                    }
                    return EMOCIONES[maxIdx].toUpperCase();
                }
            }
        } catch (Exception e) {
            log.error("[-] Error en la inferencia de emoción: ", e);
            return "DESCONOCIDA";
        } finally {
            // Liberar memoria nativa de OpenCV
            if (faceResized != null) faceResized.release();
            if (faceROI != null) faceROI.release();
            gray.release();
            img.release();
            faces.release();
            matOfByte.release();
        }
    }

    @Override
    public boolean detectCellPhoneUse(byte[] imageBytes) {
        MatOfByte matOfByte = new MatOfByte(imageBytes);
        Mat img = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR);

        if (img.empty()) {
            matOfByte.release();
            return false;
        }

        Mat rgb = new Mat();
        Mat resized = new Mat();

        try {
            // 1. Convertir BGR a RGB
            Imgproc.cvtColor(img, rgb, Imgproc.COLOR_BGR2RGB);

            // 2. Redimensionar a 640x640 para YOLOv8
            Imgproc.resize(rgb, resized, new Size(640, 640));

            // 3. Formatear a BCHW [1, 3, 640, 640] y normalizar [0.0 - 1.0]
            float[] floatData = new float[3 * 640 * 640];
            for (int c = 0; c < 3; c++) {
                for (int h = 0; h < 640; h++) {
                    for (int w = 0; w < 640; w++) {
                        double[] pixel = resized.get(h, w);
                        floatData[c * 640 * 640 + h * 640 + w] = (float) (pixel[c] / 255.0);
                    }
                }
            }

            FloatBuffer buffer = FloatBuffer.wrap(floatData);
            long[] shape = new long[]{1, 3, 640, 640};

            // 4. Inferencia ONNX
            try (OnnxTensor inputTensor = OnnxTensor.createTensor(env, buffer, shape)) {
                try (OrtSession.Result results = yoloSession.run(Collections.singletonMap("images", inputTensor))) {
                    // Output de YOLOv8 es [1][84][8400]
                    float[][][] output = (float[][][]) results.get(0).getValue();
                    float[][] data = output[0]; // [84][8400]

                    // Buscar la clase 67 ("cell phone")
                    for (int col = 0; col < 8400; col++) {
                        float score = data[4 + CLASE_CELULAR][col];
                        if (score > 0.45f) { // Umbral de confianza 45%
                            return true; // Celular detectado
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("[-] Error en la inferencia de YOLOv8 (celular): ", e);
        } finally {
            // Liberar memoria nativa de OpenCV
            rgb.release();
            resized.release();
            img.release();
            matOfByte.release();
        }
        return false;
    }

    @Override
    public double getConfidence(byte[] imageBytes) {
        // Retorna un valor por defecto o la precisión calculada
        return 1.0;
    }

    private String loadResourceToTempFile(String resourcePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (InputStream is = resource.getInputStream()) {
            // Crear archivo temporal con nombre único basado en el recurso
            File tempFile = File.createTempFile("model-", "-" + new File(resourcePath).getName());
            tempFile.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            log.info("[+] Recurso {} copiado a archivo temporal en: {}", resourcePath, tempFile.getAbsolutePath());
            return tempFile.getAbsolutePath();
        }
    }

    @PreDestroy
    public void cleanup() {
        log.info("[+] Cerrando sesiones ONNX y liberando recursos...");
        try {
            if (yoloSession != null) yoloSession.close();
            if (emotionSession != null) emotionSession.close();
            if (env != null) env.close();
            log.info("[+] Recursos de IA liberados correctamente.");
        } catch (Exception e) {
            log.error("[-] Error al liberar recursos de ONNX: ", e);
        }
    }
}
