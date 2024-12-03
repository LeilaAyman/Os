import java.util.concurrent.ConcurrentHashMap;

public class SharedMemory {
    private final ConcurrentHashMap<String, Integer> memory;

    public SharedMemory() {
        memory = new ConcurrentHashMap<>();
    }

    public synchronized void write(String key, int value) {
        memory.put(key, value);
    }

    public synchronized Integer read(String key) {
        return memory.get(key);
    }

    public void displayMemoryState() {
        System.out.println(memory);
    }
}
