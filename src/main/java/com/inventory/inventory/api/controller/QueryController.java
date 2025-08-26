package com.inventory.inventory.api.controller;

import com.inventory.inventory.api.service.AIService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/query")
@RestController
public class QueryController {

    private final AIService aiservice;
    private final JdbcTemplate jdbcTemplate;
    
    public QueryController(AIService aiservice, JdbcTemplate jdbcTemplate){
        this.aiservice = aiservice;
        this.jdbcTemplate = jdbcTemplate;
    }

    public record QueryRequest(String question) {}

    private List<Map<String, Object>> executeQueryAndReturnResults(String sql){
        return jdbcTemplate.queryForList(sql);
    }

    @PostMapping
    public List<Map<String, Object>> executeQuery(@RequestBody QueryRequest request){
        String sql = aiservice.getSQLQuery(request.question());
        return executeQueryAndReturnResults(sql);
    }
}
