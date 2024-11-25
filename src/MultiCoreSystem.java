import java.util.*;

public class MultiCoreSystem {
    public static void main(String[] args) {
        SharedMemory sharedMemory = new SharedMemory();
        SlaveCore[] slaveCores = { new SlaveCore(sharedMemory), new SlaveCore(sharedMemory) };
        Queue<Integer> readyQueue = new LinkedList<>();  // Store process IDs

        // List of hardcoded instructions
        List<String> instructions = Arrays.asList(
            "add 5 3",
            "subtract 10 4",
            "multiply 6 7",
            "divide 8 2",
            "print result",
            "= x 5"
        );

        // List to hold all created processes (so we can retrieve them by ID)
        List<Process> allProcesses = new ArrayList<>();

        // Process each instruction and add the process ID to the queue
        for (String instruction : instructions) {
            Process process = processInstruction(instruction.trim(), allProcesses, sharedMemory);  // Process and return the created process
            if (process != null) {
                readyQueue.add(process.getPCB().getProcessId());  // Add process ID to readyQueue
            }
        }

        // Create and run the MasterCore with the readyQueue holding process IDs
        MasterCore master = new MasterCore(readyQueue, slaveCores, sharedMemory, allProcesses);
        master.enhancedScheduleWithCompletionTracking();
    }

    // Method to process each instruction and return the corresponding Process
    private static Process processInstruction(String instruction, List<Process> allProcesses, SharedMemory sharedMemory) {
        String[] tokens = instruction.split("\\s+");
        String operation = tokens[0].toLowerCase();
        Process newProcess = null;

        switch (operation) {
            case "add":
            case "subtract":
            case "multiply":
            case "divide":
                if (tokens.length == 3) {
                    // Ensure correct format for arithmetic operation
                    int operand1 = Integer.parseInt(tokens[1]);
                    int operand2 = Integer.parseInt(tokens[2]);
                    newProcess = new Process(operation, operand1, operand2, generateProcessId(), 100);
                    allProcesses.add(newProcess);

                    // Store the result in shared memory (example: result = operand1 + operand2)
                    int result = operand1 + operand2;  // Example for addition
                    sharedMemory.write("result", result); // Store result in shared memory
                }
                break;
            case "print":
                if (tokens.length == 2) {
                    // Print command, retrieve variable from memory
                    String varName = tokens[1];
                    Integer value = sharedMemory.read(varName);  // Retrieve value from shared memory
                    System.out.println("Print command: " + varName + " = " + value);
                    newProcess = new Process("print", varName, 0, generateProcessId(), 100);
                    allProcesses.add(newProcess);
                }
                break;
            case "=":
                if (tokens.length == 3) {
                    // Variable assignment
                    String varName = tokens[1];
                    int value = Integer.parseInt(tokens[2]);
                    newProcess = new Process("assign", varName, value, generateProcessId(), 100);
                    allProcesses.add(newProcess);

                    // Store in shared memory
                    sharedMemory.write(varName, value);
                }
                break;
            default:
                System.out.println("Unknown instruction: " + instruction);
        }

        return newProcess;
    }

    // Generate a unique process ID (simple version)
    private static int generateProcessId() {
        return (int) (Math.random() * 1000);
    }
}
