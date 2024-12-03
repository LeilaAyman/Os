public class SlaveCore extends Thread {
    private final SharedMemory sharedMemory;
    private boolean active;

    public SlaveCore(SharedMemory sharedMemory) {
        this.sharedMemory = sharedMemory;
        this.active = false;
    }

    public void assignProcess(Process prc) {
        active = true;
        boolean processActive = prc.executeNextInstructions(sharedMemory); // Execute up to 2 instructions
        active = processActive; // Update the status based on process state
    }

    public boolean isActive() {
        return active;
    }
}
