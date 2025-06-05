package com.example.petservice.entity;

public enum Species {
    PERRO("Perro"),
    GATO("Gato"),
    CONEJO("Conejo"),
    HAMSTER("Hámster"),
    AVE("Ave"),
    PEZ("Pez"),
    REPTIL("Reptil"),
    OTRO("Otro");

    private final String description;

    Species(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
