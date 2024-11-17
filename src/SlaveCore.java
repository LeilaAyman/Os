public class SlaveCore extends Thread {
    private SharedMemory sharedMemory;

    public SlaveCore(SharedMemory sharedMemory) {
        this.sharedMemory = sharedMemory;
    }

    public void assignTask(Task task) {
        executeTask(task);
    }

    public void executeTask(Task task) {
        try {
            System.out.println("Core executing task: " + task);
            task.execute();
            System.out.println("Task execution complete.");
        } catch (Exception e) {
            System.out.println("Core encountered an error while executing task: " + e.getMessage());
        }
    }
}
