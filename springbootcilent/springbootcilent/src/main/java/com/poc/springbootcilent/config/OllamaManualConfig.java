package com.poc.springbootcilent.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaManualConfig {

    // Provides a ChatClient.Builder using the Ollama model
    @Bean
    public ChatClient.Builder chatClientBuilder(OllamaChatModel model) {
        return ChatClient.builder(model);
    }

    // Creates and configures the Ollama API client pointing to local server
    @Bean
    public OllamaApi ollamaApi() {
        return OllamaApi.builder()
                .baseUrl("http://localhost:11434") // Local Ollama server URL
                .build();
    }

}
