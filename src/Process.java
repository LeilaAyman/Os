import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Process {
    private final int processId;
    private final List<String> instructions;
    private final PCB pcb;

    public Process(int processId, List<String> instructions, int memoryBoundary) {
        this.processId = processId;
        this.instructions = instructions;
        this.pcb = new PCB(processId, memoryBoundary);
    }

    public PCB getPCB() {
        return pcb;
    }

    public int getProcessId() {
        return processId;
    }

    public int getTotalInstructions() {
        return instructions.size();
    }

    public String getCurrentInstruction() {
        return pcb.getProgramCounter() < instructions.size() ? instructions.get(pcb.getProgramCounter()) : "None";
    }

    public boolean executeNextInstructions(SharedMemory sharedMemory) {
        if (pcb.isCompleted(instructions.size())) {
            return false;
        }

        String instruction = instructions.get(pcb.getProgramCounter());
        handleInstruction(instruction, sharedMemory);
        pcb.incrementProgramCounter();

        return pcb.getProgramCounter() < instructions.size();
    }

    private void handleInstruction(String instruction, SharedMemory sharedMemory) {
        String[] tokens = instruction.split("\\s+");
        switch (tokens[0].toLowerCase()) {
            case "assign" -> handleAssign(tokens, sharedMemory);
            case "print" -> handlePrint(tokens, sharedMemory);
            default -> System.out.println("Unknown instruction: " + instruction);
        }
    }

    private void handleAssign(String[] tokens, SharedMemory sharedMemory) {
        if (tokens.length == 5) {
            // Handle operations like `assign c add a b`, `assign c multiply a b`, or `assign c divide a b`
            String operation = tokens[2].toLowerCase();
            String operand1Name = tokens[3] + " (Process " + processId + ")";
            String operand2Name = tokens[4] + " (Process " + processId + ")";
            Integer operand1 = sharedMemory.read(operand1Name);
            Integer operand2 = sharedMemory.read(operand2Name);
    
            if (operand1 != null && operand2 != null) {
                int result = switch (operation) {
                    case "add" -> operand1 + operand2;
                    case "subtract" -> operand1 - operand2;
                    case "multiply" -> operand1 * operand2;
                    case "divide" -> {
                        if (operand2 == 0) {
                            System.out.println("Error: Division by zero is not allowed.");
                            yield 0; // Return 0 or handle this case as per your logic
                        } else {
                            yield operand1 / operand2;
                        }
                    }
                    default -> {
                        System.out.println("Invalid operation: " + operation);
                        yield 0;
                    }
                };
                sharedMemory.write(tokens[1] + " (Process " + processId + ")", result);
                System.out.println("[SharedMemory] Updated: " + tokens[1] + " (Process " + processId + ") = " + result);
            } else {
                System.out.println("Operands not found or undefined for computation: " + String.join(" ", tokens));
            }
        } else if (tokens.length == 3 && tokens[2].equals("input")) {
            // Handle direct input assignment with error handling
            boolean validInput = false;
            int value = 0;
    
            while (!validInput) {
                try {
                    synchronized (System.in) {
                        System.out.print("Enter value for " + tokens[1] + " (Process " + processId + "): ");
                        Scanner scanner = new Scanner(System.in);
                        value = scanner.nextInt();
                        validInput = true; // Valid input received
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter an integer value.");
                    // Clear the scanner buffer
                    Scanner scanner = new Scanner(System.in);
                }
            }
    
            sharedMemory.write(tokens[1] + " (Process " + processId + ")", value);
            System.out.println("[SharedMemory] Updated: " + tokens[1] + " (Process " + processId + ") = " + value);
        } else {
            System.out.println("Invalid instruction format: " + String.join(" ", tokens));
        }
    }
    
    private void handlePrint(String[] tokens, SharedMemory sharedMemory) {
        String variableName = tokens[1] + " (Process " + processId + ")";
        Integer value = sharedMemory.read(variableName);
        System.out.println(variableName + " = " + (value != null ? value : "null"));
    }

    public boolean isCompleted() {
        return pcb.isCompleted(instructions.size());
    }
    
    

    
}
