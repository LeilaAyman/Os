import java.util.HashMap;
import java.util.Map;

public class SharedMemory {
    private final Map<String, Integer> memory = new HashMap<>();

    public synchronized void write(String key, int value) {
        memory.put(key, value);  // Update shared memory
    }

    public synchronized int read(String key) {
        return memory.getOrDefault(key, 0);  // Return 0 if the key doesn't exist
    }

    public synchronized void displayMemoryState() {
        System.out.println("Current Memory State: " + memory);
    }
}
