package com.inventory.inventory.api.controller;

import com.inventory.inventory.api.service.AIService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/query")
@RestController
public class QueryController {


    private final AIService aiservice;

    public QueryController(AIService aiservice){
        this.aiservice = aiservice;
    }

    public record QueryRequest(String question) {

    }

    @PostMapping
    public String executeQuery(@RequestBody QueryRequest request){
        return aiservice.getSQLQuery(request.question());
    }
}
