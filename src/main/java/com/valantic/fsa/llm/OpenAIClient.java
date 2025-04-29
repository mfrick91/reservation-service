package com.valantic.fsa.llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A client for the OpenAI API.
 * 
 * @author M. Frick
 */
public class OpenAIClient {

    /**
     * The default OpenAI model to use.
     */
//	public static final String DEFAULT_MODEL = "gpt-3.5-turbo";
	public static final String DEFAULT_MODEL = "gpt-4o";
    
	/**
     * The default OpenAI API key.
     */
	public static final String DEFAULT_API_KEY = "OPENAI_API_KEY";

	/**
     * The default OpenAI API URL.  
     */
    public static final String DEFAULT_API_URL = "https://api.openai.com/v1/chat/completions";

    /**
     * The OpenAI model to use.
     */ 
    private String model;

    /**
     * The OpenAI API key.
     */
    private String apiKey;

    /**
     * The OpenAI API URL.
     */
    private String apiUrl;

    private HttpClient client;
    private ObjectMapper mapper;

    /**
     * Constructs a new OpenAIClient with the default model.
     */
    public OpenAIClient() {
    	this(DEFAULT_MODEL);
	}
    
    /**
     * Constructs a new OpenAIClient with the given model.
     * 
     * @param model the model to use
     */
    public OpenAIClient(String model) {
    	this(model, DEFAULT_API_URL);
    }

    /**
     * Constructs a new OpenAIClient with the given model and API URL.
     * 
     * @param model the model to use
     * @param apiUrl the API URL to use
     */
    public OpenAIClient(String model, String apiUrl) {
    	this(model, apiUrl, System.getenv(DEFAULT_API_KEY));
    }
    
    /**
     * Constructs a new OpenAIClient with the given model, API URL and API key.
     * 
     * @param model the model to use
     * @param apiUrl the API URL to use
     * @param apiKey the API key to use
     */
    public OpenAIClient(String model, String apiUrl, String apiKey) {
    	this.model = model;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    /**
     * Asks the OpenAI API with the given user prompt.
     * 
     * @param userPrompt the user prompt to ask the OpenAI API with
     * @return the response from the OpenAI API
     */ 
    public String ask(String userPrompt) {
    	HttpResponse<String> response = null;
        try {
            Map<String, Object> body = Map.of(
                "model", model,
                "messages", new Object[] {
                    Map.of("role", "user", "content", userPrompt)
                }
            );

            String json = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Map<?, ?> jsonMap = mapper.readValue(response.body(), Map.class);
            return (String) ((Map<?, ?>)((Map<?, ?>)((List<?>) jsonMap.get("choices")).get(0)).get("message")).get("content");

        } catch (Exception e) {
        	String message = e.getMessage();
        	if (response != null) {
        		message = response.body();
        	}
        	throw new OpenAIException(message);
        }
    }
    
    /**
     * Exception thrown when the OpenAI API returns an error.
     * 
     * @author M. Frick
     */
    public static class OpenAIException extends RuntimeException {
        
		private static final long serialVersionUID = -9219302228793007023L;

		/**
		 * Constructs a new OpenAIException with the given message.
		 * 
		 * @param message the message to throw
		 */
		public OpenAIException(String message) {
    		super(message);
    	}
    	
    }
	
}
