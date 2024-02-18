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

    @Value("${user.authenticationToken}")
    private String token;

    @Autowired
    public RepoClient() {
    }

    ObjectMapper objectMapper = new ObjectMapper();
    HttpClient client = HttpClient.newHttpClient();

    public List<Response> getList(String username) throws IOException, InterruptedException {
        List<Response> resultData = new ArrayList<>();
        Response response;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/users/" + username + "/repos"))
                    .header("authorization", "Bearer " + token)
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .header("accept", "Accept: application/json")
                    .header("type", "owner")
                    .build();
            HttpResponse<String> clientResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Repos[] data = objectMapper.readValue(clientResponse.body(), Repos[].class);
            for (Repos repos : data) {
                Branches[] branch = getBranchList(repos.owner().login(), repos.name());
                response = Response.builder()
                        .repositoryName(repos.name())
                        .owner(repos.owner().login())
                        .branch(branch[0].name())
                        .sha(branch[0].commit().sha())
                        .build();
                resultData.add(response);
            }

        } catch (MismatchedInputException e) {
            response = Response.builder()
                    .status("404")
                    .message("User does not exist")
                    .build();
            resultData.add(response);
        }
        return resultData;
    }

    private Branches[] getBranchList(String owner, String repo) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/" + owner + "/" + repo + "/branches"))
                .header("authorization", "Bearer " + token)
                .header("accept", "Accept: application/json")
                .build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), Branches[].class);
    }

}