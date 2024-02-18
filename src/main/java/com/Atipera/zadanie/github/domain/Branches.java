package com.Atipera.zadanie.github.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Branches(String name, Commit commit) {
}
