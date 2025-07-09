package com.example.identityservice.dto.response;

public class PermissionResponse {
    private String name;
    private String description;

    // Constructor
    public PermissionResponse() {}

    public PermissionResponse(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Builder pattern
    public static PermissionResponseBuilder builder() {
        return new PermissionResponseBuilder();
    }

    public static class PermissionResponseBuilder {
        private String name;
        private String description;

        public PermissionResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PermissionResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PermissionResponse build() {
            return new PermissionResponse(name, description);
        }
    }
}
