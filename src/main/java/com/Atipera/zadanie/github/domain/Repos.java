package com.Atipera.zadanie.github.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Repos(String name, Owner owner) {
}


