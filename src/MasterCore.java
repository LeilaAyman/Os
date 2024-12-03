import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class MasterCore {
    private final Queue<Process> readyQueue;
    private final SlaveCore[] slaveCores;
    private final SharedMemory sharedMemory;
    private int completedProcesses = 0; // Track completed processes
    private final String schedulingAlgorithm = "Round Robin"; // Algorithm used

    public MasterCore(Queue<Process> readyQueue, SlaveCore[] slaveCores, SharedMemory sharedMemory) {
        this.readyQueue = readyQueue;
        this.slaveCores = slaveCores;
        this.sharedMemory = sharedMemory;
    }

    public void enhancedScheduleWithCompletionTracking() {
        AtomicInteger clockCycle = new AtomicInteger(0);
    
        // Track the current process and remaining cycles for each core
        Process[] currentProcesses = new Process[slaveCores.length];
        int[] remainingCycles = new int[slaveCores.length];
    
        while (!readyQueue.isEmpty() || areCoresActive(currentProcesses)) {
            System.out.println("\nClock Cycle: " + clockCycle.get());
    
            Thread[] coreThreads = new Thread[slaveCores.length];
    
            for (int i = 0; i < slaveCores.length; i++) {
                final int coreIndex = i;
    
                coreThreads[i] = new Thread(() -> {
                    Process currentProcess = currentProcesses[coreIndex];
    
                    // Fetch a new process if the core has no process or completed 2 cycles
                    if (currentProcess == null || remainingCycles[coreIndex] == 0) {
                        synchronized (readyQueue) {
                            if (!readyQueue.isEmpty()) {
                                currentProcess = readyQueue.poll();
                                currentProcesses[coreIndex] = currentProcess;
                                remainingCycles[coreIndex] = 2; // Reset the cycle counter
                            }
                        }
                    }
    
                    if (currentProcess != null) {
                        synchronized (System.out) {
                            System.out.println("Core " + (coreIndex + 1) + ": Executing Process " 
                                + currentProcess.getProcessId() + " - " 
                                + currentProcess.getCurrentInstruction());
                        }
    
                        boolean hasMoreInstructions = currentProcess.executeNextInstructions(sharedMemory);
                        remainingCycles[coreIndex]--; // Decrease the cycle count for this core
    
                        if (!hasMoreInstructions) {
                            synchronized (System.out) {
                                System.out.println("Process " + currentProcess.getProcessId() + " has completed execution.");
                            }
                            completedProcesses++; // Increment completed processes
                            currentProcesses[coreIndex] = null; // Clear the core as the process is done
                            remainingCycles[coreIndex] = 0; // Reset remaining cycles
                        } else if (remainingCycles[coreIndex] == 0) {
                            // Requeue the process if it has more instructions after 2 cycles
                            synchronized (readyQueue) {
                                if (!currentProcess.isCompleted()) {
                                    readyQueue.add(currentProcess);
                                }
                                currentProcesses[coreIndex] = null; // Clear the core for a new process
                            }
                        }
                    }
                });
    
                coreThreads[i].start();
            }
    
            for (Thread thread : coreThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    System.out.println("Error waiting for core thread: " + e.getMessage());
                }
            }
    
            printReadyQueueIds(); // Show updated ready queue
            sharedMemory.displayMemoryState(); // Show shared memory state
            clockCycle.incrementAndGet();
        }
    
        System.out.println("\nAll processes have completed execution.");
        displayExecutionSummary(clockCycle.get()); // Display summary at the end
    }
    
    private void displayExecutionSummary(int totalClockCycles) {
        System.out.println("\nExecution Summary:");
        System.out.println("---------------------------------");
        System.out.println("Processes Completed: " + completedProcesses);
        System.out.println("Total Clock Cycles: " + totalClockCycles);
        System.out.println("Scheduling Algorithm Used: " + schedulingAlgorithm);
    }

    private void printReadyQueueIds() {
        synchronized (readyQueue) {
            System.out.print("Ready Queue (IDs): ");
            if (readyQueue.isEmpty()) {
                System.out.println("[]");
            } else {
                System.out.print("[");
                for (Process process : readyQueue) {
                    System.out.print(process.getProcessId() + ", ");
                }
                System.out.println("\b\b]");
            }
        }
    }
    
    private boolean areCoresActive(Process[] currentProcesses) {
        for (Process process : currentProcesses) {
            if (process != null && !process.isCompleted()) {
                return true;
            }
        }
        return false;
    }
}
