import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class MasterCore {
    private final Queue<Process> readyQueue;
    private final SlaveCore[] slaveCores;
    private final SharedMemory sharedMemory;
    private int completedProcesses = 0; 
    private final String schedulingAlgorithm = "Round Robin"; 

    public MasterCore(Queue<Process> readyQueue, SlaveCore[] slaveCores, SharedMemory sharedMemory) {
        this.readyQueue = readyQueue;
        this.slaveCores = slaveCores;
        this.sharedMemory = sharedMemory;
    }

    public void ScheduleWithCompletionTracking() {
        AtomicInteger clockCycle = new AtomicInteger(0);
       // clockCycle.set(0);
    
        Process[] currentProcesses = new Process[slaveCores.length];
        int[] remainingCycles = new int[slaveCores.length];
    
        while (!readyQueue.isEmpty() || areCoresActive(currentProcesses)) {
            System.out.println("\nClock Cycle: " + clockCycle.get());
           // ScheduleWithCompletionTracking();
    
            Thread[] coreThreads = new Thread[slaveCores.length];
    
            for (int i = 0; i < slaveCores.length; i++) {
                final int coreIndex = i;
    
                coreThreads[i] = new Thread(() -> {
                    Process currentProcess = currentProcesses[coreIndex];
    
                    if (currentProcess == null || remainingCycles[coreIndex] == 0) {
                        synchronized (readyQueue) {
                            if (!readyQueue.isEmpty()) {
                               // clockCycle.incrementAndGet();
                                currentProcess = readyQueue.poll();
                                currentProcesses[coreIndex] = currentProcess;
                                remainingCycles[coreIndex] = 2; 
                            }
                        }
                    }
    
                    if (currentProcess != null) {
                        synchronized (System.out) {
                            System.out.println("Core " + (coreIndex + 1) + ": Executing Process " 
                                + currentProcess.getProcessId() + " - " 
                                + currentProcess.getCurrentInstruction());
                               // + currentProcess.getPCB().getProgramCounter();
                        }
    
                        boolean hasMoreInstructions = currentProcess.executeNextInstructions(sharedMemory);
                        remainingCycles[coreIndex]--; 
    
                        if (!hasMoreInstructions) {
                           // ++completedProcesses;
                            synchronized (System.out) {
                                System.out.println("Process " + currentProcess.getProcessId() + " has completed execution.");
                            }
                            completedProcesses++; 
                            currentProcesses[coreIndex] = null;
                            remainingCycles[coreIndex] = 0;
                            //remainingCycles[clockCycle] = 0;

                        } else if (remainingCycles[coreIndex] == 0) {
                            //readyQueue.add(currentProcess);
                            synchronized (readyQueue) {
                                if (!currentProcess.isCompleted()) {
                                    readyQueue.add(currentProcess);
                                  //  remainingCycles[coreIndex] = 2;
                                }
                                currentProcesses[coreIndex] = null; 
                            }
                        }
                    }
                });
               // coreThreads[i].setDaemon(true);
                coreThreads[i].start();
            }


            // for (Thread thread : coreThreads) {
            //     try {
            //         thread.join();
            //         thread.sleep(1000);
            //     } catch (InterruptedException e) {
            //         ScheduleWithCompletionTracking();
            //         System.out.println("Error waiting for core thread: " + e.getMessage());
            //     }
            // }

    
            for (Thread thread : coreThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    System.out.println("Error waiting for core thread: " + e.getMessage());
                }
            }


    
            printReadyQueueIds();
            sharedMemory.displayMemoryState(); 
            clockCycle.incrementAndGet();
        }
    
        System.out.println("\nAll processes have completed execution.");
        displayExecutionSummary(clockCycle.get()); 
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
               // return Process;
            }
        }
        return false;
    }
}
