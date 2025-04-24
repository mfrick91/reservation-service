package com.valantic.fsa.llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenAIClient {

	private static final String OPENAI_API_KEY = "OPENAI_API_KEY";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private String apiKey;
    private HttpClient client;
    private ObjectMapper mapper;

    public OpenAIClient() {
    	this(System.getenv(OPENAI_API_KEY));
    }
    
    public OpenAIClient(String apiKey) {
        this.apiKey = apiKey;
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public String ask(String userPrompt) {
    	HttpResponse<String> response = null;
        try {
            Map<String, Object> body = Map.of(
//                "model", "gpt-3.5-turbo",
                "model", "gpt-4",
                "messages", new Object[] {
                    Map.of("role", "user", "content", userPrompt)
                },
                "temperature", 0.2
            );

            String json = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Map<?, ?> jsonMap = mapper.readValue(response.body(), Map.class);
            return (String) ((Map<?, ?>)((Map<?, ?>)((java.util.List<?>) jsonMap.get("choices")).get(0)).get("message")).get("content");

        } catch (Exception e) {
        	String message = e.getMessage();
        	if (response != null) {
        		message = response.body();
        	}
        	throw new OpenAIException(message);
        }
    }
    
    public static class OpenAIException extends RuntimeException {
        
		private static final long serialVersionUID = -9219302228793007023L;

		public OpenAIException(String message) {
    		super(message);
    	}
    	
    }
	
}
