public class SlaveCore extends Thread {
    private final SharedMemory sharedMemory;
    private boolean active;

    public SlaveCore(SharedMemory sharedMemory) {
        this.sharedMemory = sharedMemory;
        this.active = false;
    }
    
    public void assignProcess(Process prc) {
        active = true;
        boolean processActive = prc.executeNextInstructions(sharedMemory); 
        active = processActive; 
    }

    public boolean isActive() {
        return active;
    }
}
