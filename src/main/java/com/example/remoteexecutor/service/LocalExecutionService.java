package com.example.remoteexecutor.service;

import com.example.remoteexecutor.model.Execution;
import com.example.remoteexecutor.model.ExecutionStatus;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LocalExecutionService implements ExecutionService {
    private final Map<UUID, Execution> uuidExecutionMap = new ConcurrentHashMap<>();
    private final Queue<Execution> executionQueue = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final int cpuTotal;
    private int cpuAvailable;

    public LocalExecutionService(@Value("${executor.cpu.total}") int cpuTotal) {
        this.cpuTotal = cpuTotal;
        this.cpuAvailable = cpuTotal;
    }

    @Override
    public UUID submitExecution(String command, int cpuCount){
        if (cpuCount > cpuTotal) {
            throw new IllegalArgumentException("CPU_REQUEST_EXCEEDS_CAPACITY");
        }

        UUID executionId = UUID.randomUUID();
        Execution execution = new Execution(executionId, command, cpuCount);
        executionQueue.add(execution);
        uuidExecutionMap.put(executionId, execution);

        scheduleExecutions();

        return executionId;
    }

    @Override
    public Execution getExecution(UUID executionId){
        Execution execution = uuidExecutionMap.get(executionId);
        if (execution == null) {
            throw new IllegalArgumentException("EXECUTION_NOT_FOUND");
        }
        return execution;
    }

    @Override
    public Map<UUID, Execution> getAllExecutions() {
        return uuidExecutionMap;
    }

    private synchronized void scheduleExecutions(){
        Iterator<Execution> it = executionQueue.iterator();

        while (it.hasNext() && cpuAvailable > 0){
            Execution execution = it.next();
            if (execution.getCpuCount() <= cpuAvailable){
                it.remove();
                cpuAvailable -= execution.getCpuCount();
                startExecution(execution);
            }
        }
    }

    private void startExecution(Execution execution){
        execution.setStatus(ExecutionStatus.IN_PROGRESS);
        executor.submit(() -> runCommand(execution));
    }

    private void runCommand(Execution execution){
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", execution.getCommand());
            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode == 0){
                execution.setStatus(ExecutionStatus.FINISHED);
            } else {
                execution.setStatus(ExecutionStatus.FAILED);
            }
        } catch (Exception e) {
            execution.setStatus(ExecutionStatus.FAILED);
        }

        finish(execution);
    }

    private synchronized void finish(Execution execution){
        cpuAvailable += execution.getCpuCount();
        scheduleExecutions();
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

}
