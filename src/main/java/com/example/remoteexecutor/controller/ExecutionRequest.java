package com.example.remoteexecutor.controller;

public class ExecutionRequest {
    private String command;
    private int cpuCount;

    public ExecutionRequest() {}

    public ExecutionRequest(String command, int cpuCount) {
        this.command = command;
        this.cpuCount = cpuCount;
    }

    public String getCommand() {
        return command;
    }

    public int getCpuCount() {
        return cpuCount;
    }
}
