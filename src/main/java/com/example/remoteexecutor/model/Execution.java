package com.example.remoteexecutor.model;

import java.util.UUID;

public class Execution {
    private final UUID id;
    private final String command;
    private final int cpuCount;
    private ExecutionStatus status;

    public Execution(UUID id, String command, int cpuCount) {
        if (command == null || command.isBlank()) {
            throw new IllegalArgumentException("COMMAND_INVALID");
        }
        if (cpuCount <= 0) {
            throw new IllegalArgumentException("CPU_REQUEST_INVALID");
        }

        this.id = id;
        this.command = command;
        this.cpuCount = cpuCount;
        this.status = ExecutionStatus.QUEUED; // default status is queued
    }

    public UUID getId() {
        return id;
    }

    public String getCommand() {
        return command;
    }

    public int getCpuCount() {
        return cpuCount;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }
}
