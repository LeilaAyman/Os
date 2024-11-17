import java.util.LinkedList;
import java.util.Queue;

public class MultiCoreSystem {
    public static void main(String[] args) {
        SharedMemory sharedMemory = new SharedMemory();
        Queue<Task> readyQueue = new LinkedList<>();
        SlaveCore[] slaveCores = {new SlaveCore(sharedMemory), new SlaveCore(sharedMemory)};

        readyQueue.add(new Task("add", 5, 3, 1, 100));
        readyQueue.add(new Task("subtract", 10, 4, 2, 100));
        readyQueue.add(new Task("multiply", 6, 7, 3, 100));
        readyQueue.add(new Task("divide", 8, 2, 4, 100));
        readyQueue.add(new Task("divide", 8, 0, 5, 100));

        MasterCore master = new MasterCore(readyQueue, slaveCores, sharedMemory);
        master.enhancedScheduleWithCompletionTracking();
    }
}
