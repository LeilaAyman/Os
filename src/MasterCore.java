import java.util.Queue;

public class MasterCore {
    private Queue<Task> readyQueue;
    private SlaveCore[] slaveCores;
    private SharedMemory sharedMemory;

    public MasterCore(Queue<Task> readyQueue, SlaveCore[] slaveCores, SharedMemory sharedMemory) {
        this.readyQueue = readyQueue;
        this.slaveCores = slaveCores;
        this.sharedMemory = sharedMemory;
    }

    public void enhancedScheduleWithCompletionTracking() {
        int quantum = 2; // Time slice per core
        int coreIndex = 0; // Core rotation
        int clockCycle = 0;

        while (!readyQueue.isEmpty()) {
            System.out.println("Clock Cycle: " + clockCycle++);
            System.out.println("Ready Queue: " + readyQueue);

            for (int i = 0; i < quantum && !readyQueue.isEmpty(); i++) {
                Task task = readyQueue.poll();
                PCB pcb = task.getPCB();
                System.out.println("Master assigning task to Core " + (coreIndex + 1) + ": " + task);
                System.out.println("Task's PCB Info: " + pcb);

                slaveCores[coreIndex].assignTask(task);
                System.out.println("Task completed on Core " + (coreIndex + 1) + ": " + task);
            }
            coreIndex = (coreIndex + 1) % slaveCores.length;

            System.out.println("Memory State after Cycle " + clockCycle + ": ");
            sharedMemory.displayMemoryState();
        }
        System.out.println("All tasks completed.");
    }
}
