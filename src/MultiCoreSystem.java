import java.io.*;
import java.util.*;

public class MultiCoreSystem extends Thread {
    private static final String INSTRUCTIONS_FILE = "src/Program_2.txt"; // Path to the instructions file
    private static int tempCounter = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SharedMemory sharedMemory = new SharedMemory();
        SlaveCore[] slaveCores = { new SlaveCore(sharedMemory), new SlaveCore(sharedMemory) };
        Queue<Integer> readyQueue = new LinkedList<>();

        List<Process> allProcesses = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(INSTRUCTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Process process = processInstruction(line.trim(), allProcesses, sharedMemory, scanner);
                if (process != null) {
                    readyQueue.add(process.getPCB().getProcessId());
                    System.out.println("Task added to readyQueue: " + line.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the instructions file: " + e.getMessage());
            return;
        }

        System.out.println("Initial Ready Queue: " + readyQueue);

        MasterCore master = new MasterCore(readyQueue, slaveCores, sharedMemory, allProcesses);
        master.enhancedScheduleWithCompletionTracking();
    }

    private static Process processInstruction(String instruction, List<Process> allProcesses, SharedMemory sharedMemory, Scanner scanner) {
        String[] tokens = instruction.split("\\s+");
        String operation = tokens[0].toLowerCase();
        Process newProcess = null;
    
        switch (operation) {
            case "assign" -> {
                if (tokens[2].equals("input")) {
                    if (tokens.length == 3) {
                        // Direct user input
                        System.out.print("Enter value for " + tokens[1] + ": ");
                        int userInput = scanner.nextInt();
                        sharedMemory.write(tokens[1], userInput); // Store input in shared memory
                        newProcess = new Process("assign", tokens[1], null, null, generateProcessId(), 100);
                        allProcesses.add(newProcess);
                    } else if (tokens.length == 6) {
                        // Computed input (e.g., assign c input add a b)
                        String op = tokens[3].toLowerCase();
                        String operand1Name = tokens[4];
                        String operand2Name = tokens[5];
                        newProcess = new Process(op, tokens[1], operand1Name, operand2Name, generateProcessId(), 100);
                        allProcesses.add(newProcess);
                        System.out.println("Task added for computed input: " + instruction);
                    } else {
                        System.out.println("Invalid instruction format: " + instruction);
                    }
                } else if (tokens.length == 3) {
                    // Direct value assignment (e.g., assign a 5)
                    try {
                        int value = Integer.parseInt(tokens[2]);
                        sharedMemory.write(tokens[1], value); // Store value in shared memory
                        newProcess = new Process("assign", tokens[1], null, null, generateProcessId(), 100);
                        allProcesses.add(newProcess);
                        System.out.println("Task added for direct assignment: " + instruction);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid value for assignment: " + tokens[2]);
                    }
                } else {
                    System.out.println("Invalid instruction format: " + instruction);
                }
            }
            case "print" -> {
                String varName = tokens[1];
                newProcess = new Process("print", varName, null, null, generateProcessId(), 100);
                allProcesses.add(newProcess);
            }
            default -> System.out.println("Unknown instruction: " + instruction);
        }
    
        return newProcess;
    }
    
    
    

    private static int generateProcessId() {
        return (int) (Math.random() * 1000);
    }
}
