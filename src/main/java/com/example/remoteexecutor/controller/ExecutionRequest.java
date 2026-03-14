package com.example.remoteexecutor.controller;

public class ExecutionRequest {
    private String command;
    private int cpuCount;

    public String getCommand() {
        return command;
    }

    public int getCpuCount() {
        return cpuCount;
    }
}
