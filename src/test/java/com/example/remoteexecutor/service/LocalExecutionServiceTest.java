package com.example.remoteexecutor.service;

import com.example.remoteexecutor.controller.ExecutionRequest;
import com.example.remoteexecutor.model.Execution;
import com.example.remoteexecutor.model.ExecutionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LocalExecutionServiceTest {

    private LocalExecutionService localExecutionService;

    @BeforeEach
    void setUp() {
        localExecutionService = new LocalExecutionService(4);
    }

    @Test
    void submitExecution_validJob_appearsInMapAsQueued() {
        ExecutionRequest request = new ExecutionRequest("sleep 5",1);
        UUID requestId = localExecutionService.submitExecution(request);
        Execution execution = localExecutionService.getExecution(requestId);
        assertEquals(ExecutionStatus.IN_PROGRESS, execution.getStatus());
    }

    @Test
    void submitExecution_cpuExceedsTotal_throwsException() {
        ExecutionRequest request = new ExecutionRequest("ls",6);
        assertThrows(IllegalArgumentException.class, () -> {
            localExecutionService.submitExecution(request);
        });
    }

    @Test
    void submitExecution_blankCommand_throwsException() {
        ExecutionRequest request = new ExecutionRequest("",1);
        assertThrows(IllegalArgumentException.class, () -> {
            localExecutionService.submitExecution(request);
        });
    }

    @Test
    void scheduleExecutions_smallJobsRunWhenLargeJobBlocks() throws InterruptedException {
        ExecutionRequest request1 = new ExecutionRequest("sleep 5", 3);
        ExecutionRequest request2 = new ExecutionRequest("ls", 2);
        ExecutionRequest request3 = new ExecutionRequest("ls", 1);

        localExecutionService.submitExecution(request1);
        UUID id2 = localExecutionService.submitExecution(request2);
        UUID id3 = localExecutionService.submitExecution(request3);
        Thread.sleep(500);
        Execution execution2 = localExecutionService.getExecution(id2);
        Execution execution3 = localExecutionService.getExecution(id3);
        assertEquals(ExecutionStatus.QUEUED, execution2.getStatus());
        assertNotEquals(ExecutionStatus.QUEUED, execution3.getStatus());
    }

    @Test
    void submitExecution_jobFinishes_statusIsFinished() throws InterruptedException {
        ExecutionRequest request = new ExecutionRequest("echo hello",1);
        UUID id = localExecutionService.submitExecution(request);
        Thread.sleep(500);
        Execution execution = localExecutionService.getExecution(id);
        assertEquals(ExecutionStatus.FINISHED, execution.getStatus());
    }

    @Test
    void getExecution_unknownId_throwsException() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> {
           localExecutionService.getExecution(id);
        });
    }
}