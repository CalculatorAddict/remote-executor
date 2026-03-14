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
        UUID requestId = localExecutionService.submitExecution("sleep 5",1);
        Execution execution = localExecutionService.getExecution(requestId);
        assertEquals(ExecutionStatus.IN_PROGRESS, execution.getStatus());
    }

    @Test
    void submitExecution_cpuExceedsTotal_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            localExecutionService.submitExecution("ls",6);
        });
    }

    @Test
    void submitExecution_blankCommand_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            localExecutionService.submitExecution("",1);
        });
    }

    @Test
    void scheduleExecutions_smallJobsRunWhenLargeJobBlocks() throws InterruptedException {
        localExecutionService.submitExecution("sleep 5", 3);
        UUID id2 = localExecutionService.submitExecution("ls", 2);
        UUID id3 = localExecutionService.submitExecution("ls", 1);
        Thread.sleep(500);
        Execution execution2 = localExecutionService.getExecution(id2);
        Execution execution3 = localExecutionService.getExecution(id3);
        assertEquals(ExecutionStatus.QUEUED, execution2.getStatus());
        assertNotEquals(ExecutionStatus.QUEUED, execution3.getStatus());
    }

    @Test
    void submitExecution_jobFinishes_statusIsFinished() throws InterruptedException {
        UUID id = localExecutionService.submitExecution("echo hello",1);
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