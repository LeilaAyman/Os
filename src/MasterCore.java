import java.util.List;
import java.util.Queue;

public class MasterCore {
    private Queue<Integer> readyQueue;  // Queue of process IDs
    private SlaveCore[] slaveCores;
    private SharedMemory sharedMemory;
    private List<Process> allProcesses;  // List to retrieve processes by ID

    public MasterCore(Queue<Integer> readyQueue, SlaveCore[] slaveCores, SharedMemory sharedMemory, List<Process> allProcesses) {
        this.readyQueue = readyQueue;
        this.slaveCores = slaveCores;
        this.sharedMemory = sharedMemory;
        this.allProcesses = allProcesses;
    }

    public void enhancedScheduleWithCompletionTracking() {
        int quantum = 2; // Time slice per core
        int coreIndex = 0; // Core rotation (starts with Core 1)
        int clockCycle = 0;

        // While there are processes in the ready queue
        while (!readyQueue.isEmpty()) {
            System.out.println("Clock Cycle: " + clockCycle++);
            System.out.println("Ready Queue: " + readyQueue);

            // Assign tasks to cores (Round Robin: Core 1, then Core 2)
            for (int i = 0; i < quantum && !readyQueue.isEmpty(); i++) {
                Integer processId = readyQueue.poll();  // Get the process ID from the queue
                Process prc = getProcessById(processId);  // Retrieve the process by ID

                if (prc != null) {
                    PCB pcb = prc.getPCB();
                    System.out.println("Master assigning task to Core " + (coreIndex + 1) + ": " + prc);
                    System.out.println("Task's PCB Info: " + pcb);

                    // Assign the process to the selected slave core
                    slaveCores[coreIndex].assignProcess(prc);
                    System.out.println("Task completed on Core " + (coreIndex + 1) + ": " + prc);
                }

                // Move to the next core in a round-robin fashion after each task
                coreIndex = (coreIndex + 1) % slaveCores.length;
            }

            // Display the current memory state after each cycle
            System.out.println("Memory State after Cycle " + clockCycle + ": ");
            sharedMemory.displayMemoryState();
        }
        System.out.println("All tasks completed.");
    }

    // Retrieve the process by its ID from the allProcesses list
    private Process getProcessById(Integer processId) {
        for (Process prc : allProcesses) {
            if (prc.getPCB().getProcessId() == processId) {
                return prc;
            }
        }
        return null;  // Return null if the process is not found
    }
}
