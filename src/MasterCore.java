import java.util.List;
import java.util.Queue;

public class MasterCore {
    private final Queue<Integer> readyQueue;
    private final SlaveCore[] slaveCores;
    private final SharedMemory sharedMemory;
    private final List<Process> allProcesses;

    public MasterCore(Queue<Integer> readyQueue, SlaveCore[] slaveCores, SharedMemory sharedMemory, List<Process> allProcesses) {
        this.readyQueue = readyQueue;
        this.slaveCores = slaveCores;
        this.sharedMemory = sharedMemory;
        this.allProcesses = allProcesses;
    }

    public void enhancedScheduleWithCompletionTracking() {
        int clockCycle = 0;
    
        while (!readyQueue.isEmpty()) {
            System.out.println("Clock Cycle: " + clockCycle++);
            System.out.println("Ready Queue: " + readyQueue);
    
            for (int i = 0; i < slaveCores.length && !readyQueue.isEmpty(); i++) {
                Integer processId = readyQueue.poll(); // Get the next process ID
                Process prc = getProcessById(processId);
    
                if (prc != null) {
                    System.out.println("Core " + (i + 1) + " is executing process: " + prc.getPCB().getProcessId());
                    slaveCores[i].assignProcess(prc);
                    try {
                        slaveCores[i].join(); // Ensure the process completes before moving to the next
                    } catch (InterruptedException e) {
                        System.out.println("Error while waiting for core completion: " + e.getMessage());
                    }
                }
            }
    
            System.out.println("Memory State after Cycle " + clockCycle + ":");
            sharedMemory.displayMemoryState(); // Display the updated memory state
        }
    
        System.out.println("All tasks completed.");
    }
    

    private Process getProcessById(Integer processId) {
        for (Process prc : allProcesses) {
            if (prc.getPCB().getProcessId() == processId) {
                return prc;
            }
        }
        return null;
    }
}
