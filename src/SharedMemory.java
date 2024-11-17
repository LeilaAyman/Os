import java.util.HashMap;
import java.util.Map;

public class SharedMemory {
    private final Map<String, Integer> memory = new HashMap<>();

    public synchronized void write(String key, int value) {
        memory.put(key, value);
    }

    public synchronized int read(String key) {
        return memory.getOrDefault(key, 0);
    }

    public synchronized void displayMemoryState() {
        System.out.println("Current Memory State: " + memory);
    }
}
