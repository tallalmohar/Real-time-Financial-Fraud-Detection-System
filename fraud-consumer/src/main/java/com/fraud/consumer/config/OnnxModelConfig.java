package com.fraud.consumer.config;

import ai.onnxruntime.OrtEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Slf4j
@Configuration
public class OnnxModelConfig {


    @Bean
    public OrtSession fraudDetectionModel() throws OrtException, IOException {
        // just the environment that manages native libraries
        // in basic language this is running all the ML models
        OrtEnvironment env = OrtEnvironment.getEnvironment();


        ClassPathResource modelResource = new ClassPathResource("fraud_model.onnx");
        // opens file as a stream and read the entire file into mem as byte array
        byte[] modelBytes = modelResource.getInputStream().readAllBytes();

        //takes the raw models bytes and parses them
        //loads all the neural networks weights and validates the model structure
        OrtSession session = env.createSession(modelBytes);

        log.info("Model has {} inputs and {} outputs", session.getInputInfo(), session.getOutputInfo());

        return session;
    }
}
