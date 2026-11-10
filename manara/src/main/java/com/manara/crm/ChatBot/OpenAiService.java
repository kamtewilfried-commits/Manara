package com.manara.crm.ChatBot;

import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService {

    private final OpenAIClient openAIClient;

    @Value("${app.chat.model:gpt-5.2}")
    private String model;

    public OpenAiService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public String askChatGPT(String userMessage) {
        try {
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model(model)
                    .instructions("""
                            Tu es un assistant pour un centre communautaire.
                            Réponds en français.
                            Sois clair, poli et utile.
                            Si la question concerne les activités, sessions, horaires, inscriptions ou services du centre,
                            réponds de manière concise.
                            """)
                    .input(userMessage)
                    .build();

            Response response = openAIClient.responses().create(params);

            StringBuilder result = new StringBuilder();

            response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .flatMap(content -> content.outputText().stream())
                    .forEach(outputText -> result.append(outputText.text()));

            String text = result.toString().trim();

            if (text.isBlank()) {
                return "Je n’ai pas pu générer une réponse pour le moment.";
            }

            return text;

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la communication avec OpenAI : " + e.getMessage();
        }
    }
}