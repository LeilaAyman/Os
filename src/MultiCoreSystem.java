import java.io.*;
import java.util.*;

public class MultiCoreSystem {
    public static void main(String[] args) {
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
        masterCore.ScheduleWithCompletionTracking();
    }

    public static void addProcess(String fileName, Queue<Process> readyQueue, List<Process> allProcesses, SharedMemory sharedMemory, int processIdCounter) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<String> instructions = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) { 
                    instructions.add(line.trim());
                }
            }
           // Process process = new Process(processIdCounter, 100);
            Process process = new Process(processIdCounter, instructions, 100);
            readyQueue.add(process); 
            allProcesses.add(process); 

            System.out.println("Added Process " + processIdCounter + " from file: " + fileName);
        } //try {},
        catch (IOException e) {
            System.out.println("Error reading file " + fileName + ": " + e.getMessage());
        }
    }
}
