package com.example.remoteexecutor.controller;

import com.example.remoteexecutor.model.Execution;
import com.example.remoteexecutor.service.ExecutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/executions")
public class ExecutionController {

    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping
    public ResponseEntity<?> submit(@RequestBody ExecutionRequest request) {
        UUID id = executionService.submitExecution(request.getCommand(), request.getCpuCount());
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStatus(@PathVariable UUID id) {
        Execution execution = executionService.getExecution(id);
        return new ResponseEntity<>(execution.getStatus(), HttpStatus.OK);
    }
}