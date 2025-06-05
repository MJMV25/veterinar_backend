package com.example.petservice.entity;

public enum Gender {
    MACHO("Macho"),
    HEMBRA("Hembra");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}