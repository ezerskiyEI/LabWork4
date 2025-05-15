package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class BulkOperationRequest {
    @NotEmpty(message = "Identifiers list cannot be empty")
    private List<String> identifiers;

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }
}