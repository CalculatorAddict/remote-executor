# Remote Executor Service

A lightweight job execution service that runs shell commands asynchronously with CPU resource constraints.

Users submit a command with a CPU requirement via the REST API. The service queues the job, allocates CPU capacity, and executes the command asynchronously. Users can poll for status updates as the job moves through `QUEUED → IN_PROGRESS → FINISHED / FAILED`.

## Running the Service

1. Set CPU capacity in `src/main/resources/application.properties`:
```
executor.cpu.total=4
```
2. Run the application:
```
./gradlew bootRun
```
   The service starts on `http://localhost:8080`.

## API

### Submit a job
```
POST /executions
Content-Type: application/json

{
"command": "echo hello",
"cpuCount": 2
}
```
Returns `201 Created` with the job UUID.

**Example:**
```bash
curl -X POST http://localhost:8080/executions \
  -H "Content-Type: application/json" \
  -d '{"command": "echo hello", "cpuCount": 1}'
```

### Get all executions
```
GET /executions
```
Returns `200 OK` with a map of all executions keyed by UUID.

**Example:**
```bash
curl http://localhost:8080/executions
```

### Get a single execution
```
GET /executions/{id}
```
Returns `200 OK` with the full execution object including id, command, cpuCount, and status.

**Example:**
```bash
curl http://localhost:8080/executions/{id}
```

## Design Decisions

**FIFO with first-fit skip**
The scheduler maintains a FIFO queue but skips jobs that cannot fit in available CPU capacity, allowing smaller jobs behind a large blocked job to run. Skipped jobs retain their position in the queue and are not moved behind later arrivals.

**CPU as the only resource constraint**
CPU count is the sole scheduling dimension. All CPUs are treated as having equal compute power.

**Synchronised scheduling**
The scheduling loop and CPU accounting are synchronised to prevent race conditions between job submissions and completions. The invariant `cpuAvailable + sum(cpuCount of in-progress jobs) = cpuTotal` is maintained throughout.

**Swappable executor backend**
`ExecutionService` is an interface, with `LocalExecutionService` as the default implementation using `ProcessBuilder`. This makes it straightforward to swap in a Kubernetes or Docker-based executor without changing the controller or scheduling logic.

## Assumptions

- The executor is local — commands run via `ProcessBuilder` on the host machine.
- All CPUs are homogeneous with equal compute power.
- Commands are trusted — no sandboxing or input sanitisation is applied.

## Limitations

- The scheduling loop is O(n) per pass but repeated scheduling on each job completion gives O(n²) worst-case behaviour when the queue is large and most jobs are blocked. For the intended use case this is not a practical concern.
- No persistence — all job state is in-memory and lost on restart.
- No authentication or rate limiting on the API.