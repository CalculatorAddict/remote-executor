package com.example.remoteexecutor.controller;

import com.example.remoteexecutor.model.Execution;
import com.example.remoteexecutor.service.ExecutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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

    @GetMapping
    public ResponseEntity<?> getAllExecutions() {
        Map<UUID, Execution> allExecutions = executionService.getAllExecutions();
        return new ResponseEntity<>(allExecutions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExecution(@PathVariable UUID id) {
        Execution execution = executionService.getExecution(id);
        return new ResponseEntity<>(execution, HttpStatus.OK);
    }
}