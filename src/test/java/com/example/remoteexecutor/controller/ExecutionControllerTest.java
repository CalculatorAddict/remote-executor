package com.example.remoteexecutor.controller;

import com.example.remoteexecutor.model.Execution;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ExecutionControllerTest {

    private final TestRestTemplate restTemplate;

    @Autowired
    public ExecutionControllerTest(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Test
    void submitExecution_validJob_returns201() {
        ExecutionRequest request = new ExecutionRequest("echo hello", 1);
        ResponseEntity<UUID> response = restTemplate.postForEntity("/executions", request, UUID.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void submitExecution_blankCommand_returns400() {
        ExecutionRequest request = new ExecutionRequest("", 1);
        ResponseEntity<String> response = restTemplate.postForEntity("/executions", request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void submitExecution_cpuExceedsTotal_returns400() {
        ExecutionRequest request = new ExecutionRequest("echo hello", 999);
        ResponseEntity<String> response = restTemplate.postForEntity("/executions", request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getExecution_validId_returns200WithStatus() {
        ExecutionRequest request = new ExecutionRequest("echo hello", 1);
        UUID id = restTemplate.postForObject("/executions", request, UUID.class);
        ResponseEntity<Execution> response = restTemplate.getForEntity("/executions/" + id, Execution.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getExecution_unknownId_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/executions/" + UUID.randomUUID(), String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAllExecutions_returns200() {
        restTemplate.postForEntity("/executions", new ExecutionRequest("echo hello", 1), UUID.class);
        restTemplate.postForEntity("/executions", new ExecutionRequest("echo hello", 1), UUID.class);
        ResponseEntity<String> response = restTemplate.getForEntity("/executions", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}