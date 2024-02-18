package com.Atipera.zadanie.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Response(String repositoryName, String owner, String branch, String sha, String status, String message) {
}

