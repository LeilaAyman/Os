public class Process {
    private final String operation;
    private final String varName;
    private final String operand1Name;
    private final String operand2Name;
    private final PCB pcb;

    public Process(String operation, String varName, int value, int processId, int memoryBoundary) {
        this.operation = operation;
        this.varName = varName;
        this.operand1Name = null;
        this.operand2Name = null;
        this.pcb = new PCB(processId, memoryBoundary);
    }

    public Process(String operation, String varName, String operand1Name, String operand2Name, int processId, int memoryBoundary) {
        this.operation = operation;
        this.varName = varName;
        this.operand1Name = operand1Name;
        this.operand2Name = operand2Name;
        this.pcb = new PCB(processId, memoryBoundary);
    }

    public PCB getPCB() {
        return pcb;
    }

    public void execute(SharedMemory sharedMemory) {
        try {
            System.out.println("Executing operation: " + operation + " on variable: " + varName);
    
            switch (operation.toLowerCase()) {
                case "assign" -> {
                    if (operand1Name == null && operand2Name == null) {
                        // Direct value assignment from user input
                        Integer directValue = sharedMemory.read(varName);
                        if (directValue != null) {
                            sharedMemory.write(varName, directValue);
                            System.out.println("Memory updated: " + varName + " = " + directValue);
                        } else {
                            System.out.println("Error: Direct value for variable " + varName + " is not set.");
                        }
                    }
                }
                case "add", "subtract", "multiply", "divide" -> {
                    // Fetch operands from memory
                    Integer operand1 = sharedMemory.read(operand1Name);
                    Integer operand2 = sharedMemory.read(operand2Name);
    
                    if (operand1 == null || operand2 == null) {
                        System.out.println("Error: Operands not found in memory for operation: " + operation);
                        return;
                    }
    
                    int result = switch (operation) {
                        case "add" -> operand1 + operand2;
                        case "subtract" -> operand1 - operand2;
                        case "multiply" -> operand1 * operand2;
                        case "divide" -> operand2 != 0 ? operand1 / operand2 : 0;
                        default -> throw new IllegalArgumentException("Unknown operation: " + operation);
                    };
    
                    sharedMemory.write(varName, result);
                    System.out.println("Memory updated: " + varName + " = " + result);
                }
                case "print" -> {
                    // Print the value of the variable
                    Integer value = sharedMemory.read(varName);
                    if (value != null) {
                        System.out.println(varName + " = " + value);
                    } else {
                        System.out.println(varName + " = null (not computed yet)");
                    }
                }
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            }
    
            pcb.incrementProgramCounter();
        } catch (Exception e) {
            System.out.println("Error executing process: " + e.getMessage());
        }
    }
    
    

    
}
