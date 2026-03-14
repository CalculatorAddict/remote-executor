package com.example.remoteexecutor.service;

import com.example.remoteexecutor.model.Execution;

import java.util.Map;
import java.util.UUID;

public interface ExecutionService {
    UUID submitExecution(String command, int cpuCount);

    Execution getExecution(UUID executionId);

    Map<UUID, Execution> getAllExecutions();
}
