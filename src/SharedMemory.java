import java.util.concurrent.ConcurrentHashMap;

public class SharedMemory {
    private final ConcurrentHashMap<String, Integer> memory;

    public SharedMemory() {
        memory = new ConcurrentHashMap<>();
    }

    public synchronized void write(String key, int value) {
        memory.put(key, value);
        System.out.println("[SharedMemory] Updated: " + key + " = " + value);
    }
    
    public synchronized Integer read(String key) {
        Integer value = memory.get(key);
        System.out.println("[SharedMemory] Read: " + key + " = " + (value != null ? value : "null"));
        return value;
    }
    
    public void displayMemoryState() {
        System.out.println(memory);
    }
}