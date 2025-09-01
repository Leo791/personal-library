package com.github.leo791.personal_library.model.dto;

import java.util.Map;

public class ErrorResponse {
    private String error;
    private String isbn;
    private String nextStep;
    private Map<String, String> links;

    public ErrorResponse(String error, String isbn, String nextStep, Map<String, String> links) {
        this.error = error;
        this.isbn = isbn;
        this.nextStep = nextStep;
        this.links = links;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
