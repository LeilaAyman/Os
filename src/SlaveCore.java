public class SlaveCore extends Thread {
    private final SharedMemory sharedMemory;

    public SlaveCore(SharedMemory sharedMemory) {
        this.sharedMemory = sharedMemory;
    }

    public void assignProcess(Process prc) {
        executeProcess(prc);
    }

    public void executeProcess(Process prc) {
        try {
            prc.execute(sharedMemory);
        } catch (Exception e) {
            System.out.println("Core encountered an error while executing task: " + e.getMessage());
        }
    }
}
