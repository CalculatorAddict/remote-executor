package com.example.remoteexecutor.service;

import com.example.remoteexecutor.controller.ExecutionRequest;
import com.example.remoteexecutor.model.Execution;

import java.util.UUID;

public interface ExecutionService {
    UUID submitExecution(String command, int cpuCount);

    Execution getExecution(UUID executionId);
}
