package com.Atipera.zadanie.github.client;

import com.Atipera.zadanie.github.Response;
import com.Atipera.zadanie.github.domain.Branches;
import com.Atipera.zadanie.github.domain.Repos;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class RepoClient {

    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final String AUTHORIZATION_HEADER = "authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String X_GITHUB_API_VERSION_HEADER = "X-GitHub-Api-Version";
    private static final String API_VERSION = "2022-11-28";
    private static final String ACCEPT_HEADER = "accept";
    private static final String ACCEPT_VALUE = "Accept: application/json";
    private static final String TYPE_HEADER = "type";
    private static final String TYPE_VALUE = "owner";
    private static final String USER_NOT_FOUND_STATUS = "404";
    private static final String USER_NOT_FOUND_MESSAGE = "User does not exist";

    @Value("${user.authenticationToken}")
    private String token;

    private final ObjectMapper objectMapper;
    private HttpClient client = HttpClient.newHttpClient();

    @Autowired
    public RepoClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Response> getList(String username) throws IOException, InterruptedException {
        List<Response> resultData = new ArrayList<>();
        try {
            HttpRequest request = createRequestForUserRepos(username);
            HttpResponse<String> clientResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Repos[] repos = objectMapper.readValue(clientResponse.body(), Repos[].class);
            for (Repos repo : repos) {
                Response response = createResponseForRepo(repo);
                resultData.add(response);
            }
        } catch (MismatchedInputException e) {
            Response response = createResponseForUserNotFound();
            resultData.add(response);
        }
        return resultData;
    }

    private HttpRequest createRequestForUserRepos(String username) {
        return HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_API_URL + "/users/" + username + "/repos"))
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .header(X_GITHUB_API_VERSION_HEADER, API_VERSION)
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(TYPE_HEADER, TYPE_VALUE)
                .build();
    }

    private Response createResponseForRepo(Repos repo) throws IOException, InterruptedException {
        Branches[] branches = getBranchList(repo.owner().login(), repo.name());
        return Response.builder()
                .repositoryName(repo.name())
                .owner(repo.owner().login())
                .branch(branches[0].name())
                .sha(branches[0].commit().sha())
                .build();
    }

    private Response createResponseForUserNotFound() {
        return Response.builder()
                .status(USER_NOT_FOUND_STATUS)
                .message(USER_NOT_FOUND_MESSAGE)
                .build();
    }

    private Branches[] getBranchList(String owner, String repo) throws IOException, InterruptedException {
        HttpRequest request = createRequestForRepoBranches(owner, repo);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), Branches[].class);
    }

    private HttpRequest createRequestForRepoBranches(String owner, String repo) {
        return HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_API_URL + "/repos/" + owner + "/" + repo + "/branches"))
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .build();
    }
}