import java.io.*;
import java.util.*;

public class MultiCoreSystem {
    public static void main(String[] args) {
        // Include Program_1.txt alongside Program_2.txt and Program_3.txt
        String[] inputFiles = {"src/program_1.txt", "src/Program_2.txt", "src/Program_3.txt"};
        SharedMemory sharedMemory = new SharedMemory();
        SlaveCore[] slaveCores = {new SlaveCore(sharedMemory), new SlaveCore(sharedMemory)};
        Queue<Process> readyQueue = new LinkedList<>();
        List<Process> allProcesses = new ArrayList<>();

        int processIdCounter = 1;

        for (String fileName : inputFiles) {
            addProcess(fileName, readyQueue, allProcesses, sharedMemory, processIdCounter++);
        }

        System.out.println("Initial Ready Queue: " + readyQueue.size() + " processes");

        MasterCore masterCore = new MasterCore(readyQueue, slaveCores, sharedMemory);
        masterCore.enhancedScheduleWithCompletionTracking();
    }

    public static void addProcess(String fileName, Queue<Process> readyQueue, List<Process> allProcesses, SharedMemory sharedMemory, int processIdCounter) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<String> instructions = new ArrayList<>();
            String line;

            // Read lines from the file, skipping empty lines
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) { // Skip empty lines
                    instructions.add(line.trim());
                }
            }

            // Create a new Process with a unique process ID
            Process process = new Process(processIdCounter, instructions, 100);
            readyQueue.add(process); // Add the process to the ready queue
            allProcesses.add(process); // Keep track of all processes

            System.out.println("Added Process " + processIdCounter + " from file: " + fileName);
        } catch (IOException e) {
            System.out.println("Error reading file " + fileName + ": " + e.getMessage());
        }
    }
}
