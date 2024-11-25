public class SlaveCore extends Thread {
    private SharedMemory sharedMemory;

    public SlaveCore(SharedMemory sharedMemory) {
        this.sharedMemory = sharedMemory;
    }

    public void assignProcess(Process prc) {
        executeProcess(prc);
    }

    public void executeProcess(Process prc) {
        try {
            System.out.println("Core executing task: " + prc);
            prc.execute();
            System.out.println("Task execution complete.");
        } catch (Exception e) {
            System.out.println("Core encountered an error while executing task: " + e.getMessage());
        }
    }
}
