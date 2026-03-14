package com.example.remoteexecutor.service;

import com.example.remoteexecutor.controller.ExecutionRequest;
import com.example.remoteexecutor.model.Execution;
import com.example.remoteexecutor.model.ExecutionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionServiceTest {

    private ExecutionService executionService;

    @BeforeEach
    void setUp() {
        executionService = new ExecutionService(4);
    }

    @Test
    void submitExecution_validJob_appearsInMapAsQueued() {
        ExecutionRequest request = new ExecutionRequest("sleep 5",1);
        UUID requestId = executionService.submitExecution(request);
        Execution execution = executionService.getExecution(requestId);
        assertEquals(ExecutionStatus.IN_PROGRESS, execution.getStatus());
    }

    @Test
    void submitExecution_cpuExceedsTotal_throwsException() {
        ExecutionRequest request = new ExecutionRequest("ls",6);
        assertThrows(IllegalArgumentException.class, () -> {
            executionService.submitExecution(request);
        });
    }

    @Test
    void submitExecution_blankCommand_throwsException() {
        ExecutionRequest request = new ExecutionRequest("",1);
        assertThrows(IllegalArgumentException.class, () -> {
            executionService.submitExecution(request);
        });
    }

    @Test
    void scheduleExecutions_smallJobsRunWhenLargeJobBlocks() throws InterruptedException {
        ExecutionRequest request1 = new ExecutionRequest("sleep 5", 3);
        ExecutionRequest request2 = new ExecutionRequest("ls", 2);
        ExecutionRequest request3 = new ExecutionRequest("ls", 1);

        executionService.submitExecution(request1);
        UUID id2 = executionService.submitExecution(request2);
        UUID id3 = executionService.submitExecution(request3);
        Thread.sleep(500);
        Execution execution2 = executionService.getExecution(id2);
        Execution execution3 = executionService.getExecution(id3);
        assertEquals(ExecutionStatus.QUEUED, execution2.getStatus());
        assertNotEquals(ExecutionStatus.QUEUED, execution3.getStatus());
    }

    @Test
    void submitExecution_jobFinishes_statusIsFinished() throws InterruptedException {
        ExecutionRequest request = new ExecutionRequest("echo hello",1);
        UUID id = executionService.submitExecution(request);
        Thread.sleep(500);
        Execution execution = executionService.getExecution(id);
        assertEquals(ExecutionStatus.FINISHED, execution.getStatus());
    }

    @Test
    void getExecution_unknownId_throwsException() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> {
           executionService.getExecution(id);
        });
    }
}