package com.cardgamedeck.cli.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public abstract class BaseApiService {

    protected final RestTemplate restTemplate;
    protected final String baseUrl;

    public BaseApiService(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    protected <T> T get(String endpoint, Class<T> responseType) {
        try {
            return restTemplate.getForObject(baseUrl + endpoint, responseType);
        } catch (HttpStatusCodeException e) {
            handleApiError(e);
            return null;
        } catch (RestClientException e) {
            handleConnectionError(e);
            return null;
        }
    }

    protected <T> T post(String endpoint, Object request, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Object> entity = new HttpEntity<>(request, headers);
            return restTemplate.postForObject(baseUrl + endpoint, entity, responseType);
        } catch (HttpStatusCodeException e) {
            handleApiError(e);
            return null;
        } catch (RestClientException e) {
            handleConnectionError(e);
            return null;
        }
    }

    protected void delete(String endpoint) {
        try {
            restTemplate.delete(baseUrl + endpoint);
        } catch (HttpStatusCodeException e) {
            handleApiError(e);
        } catch (RestClientException e) {
            handleConnectionError(e);
            return;
        }
    }

    protected <T> T put(String endpoint, Object request, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(request, headers);
            ResponseEntity<T> response = restTemplate.exchange(
                    baseUrl + endpoint,
                    HttpMethod.PUT,
                    entity,
                    responseType
            );
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            handleApiError(e);
            return null;
        } catch (RestClientException e) {
            handleConnectionError(e);
            return null;
        }
    }

    protected void handleApiError(HttpStatusCodeException e) {
        System.err.println("API Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
    }

    protected void handleConnectionError(RestClientException e) {
        System.err.println("Connection Error: " + e.getMessage());
        System.err.println("Please check that the API server is running and accessible.");
    }
}