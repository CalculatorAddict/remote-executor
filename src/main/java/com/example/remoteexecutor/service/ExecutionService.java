package com.example.remoteexecutor.service;

import com.example.remoteexecutor.controller.ExecutionRequest;
import com.example.remoteexecutor.model.Execution;

import java.util.UUID;

public interface ExecutionService {
    UUID submitExecution(ExecutionRequest executionRequest);

    Execution getExecution(UUID executionId);
}
