package com.herts.codeexplainer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    // Reads the API key from application.properties
    @Value("${openai.api.key}")
    private String apiKey;

    // Dummy mode: true = return placeholder explanation (no API call)
    // Set this to false in application.properties once you have credits.
    @Value("${openai.api.dummy:true}")
    private boolean dummyMode;

    private final RestTemplate restTemplate;

    public OpenAiService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Generates an explanation for the given Java code.
     * In dummy mode, returns a placeholder.
     * In live mode, calls the OpenAI API.
     */
    public String explain(String code) {
        if (dummyMode) {
            return getDummyExplanation();
        }
        return getRealExplanation(code);
    }

    /**
     * Calls the OpenAI Chat Completions API (GPT-4o mini or GPT-4o)
     * with a prompt designed for line‑by‑line pedagogical explanation.
     */
    private String getRealExplanation(String code) {
        String url = "https://api.openai.com/v1/chat/completions";

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Build the "messages" array
        List<Map<String, String>> messages = new ArrayList<>();

        // System message: tells the AI how to behave
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
                "You are a helpful Java programming tutor. " +
                        "Explain the following code line by line in simple, clear language. " +
                        "Use a structured format: show each line of code followed by its explanation. " +
                        "If there are any potential pitfalls, mention them. " +
                        "If you are unsure about anything, say so honestly.");
        messages.add(systemMessage);

        // User message: the actual code to explain
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "Please explain this Java code:\n\n" + code);
        messages.add(userMessage);

        // Build the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");   // cheap and fast; change to "gpt-4o" if needed
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3);       // lower = more factual

        // Create the HTTP entity
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Send the POST request
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        // Extract the explanation text from the response
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }
        }

        return "Sorry, no explanation could be generated.";
    }

    /**
     * Returns a safe placeholder explanation while the API is unavailable.
     */
    private String getDummyExplanation() {
        return "Line 1: class Animal {  -- Defines a class named Animal.\n" +
                "Line 2:     String name;  -- Declares a String variable called name.\n" +
                "Line 3:     Animal(String name) {  -- Constructor that takes a name parameter.\n" +
                "Line 4:         this.name = name;  -- Assigns the parameter to the instance variable.\n" +
                "Line 5:     }\n" +
                "Line 6:     void makeSound() {  -- Defines a method that prints a sound.\n" +
                "Line 7:         System.out.println(\"Some sound\");\n" +
                "Line 8:     }\n" +
                "Line 9: }\n" +
                "\n(This is a placeholder explanation. Real AI explanations will appear once the API is connected.)";
    }
}