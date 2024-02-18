package com.Atipera.zadanie;

import com.Atipera.zadanie.github.Response;
import com.Atipera.zadanie.github.client.RepoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController("/")
public class Controller {
    private final RepoClient repoClient;

    @Autowired
    public Controller(RepoClient repoClient) {
        this.repoClient = repoClient;
    }

    @ResponseBody
    @GetMapping(value = "getList/{username}", produces = "application/json")
    List<Response> getRepoList(@PathVariable String username) throws IOException, InterruptedException {
        return repoClient.getList(username);
    }
}
