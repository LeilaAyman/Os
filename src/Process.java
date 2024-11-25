public class Process {
    private String operation;
    private int operand1;
    private int operand2;
    private String varName;  // For variable assignment and print commands
    private PCB pcb;

    // Constructor for arithmetic operations
    public Process(String operation, int operand1, int operand2, int processId, int memoryBoundary) {
        this.operation = operation;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.pcb = new PCB(processId, memoryBoundary);
    }

    // Constructor for print and assign operations
    public Process(String operation, String varName, int value, int processId, int memoryBoundary) {
        this.operation = operation;
        this.varName = varName;
        this.operand1 = value;  // Use operand1 for storing the value
        this.pcb = new PCB(processId, memoryBoundary);
        System.out.println("Operation: " + operation + " on " + varName + " with value " + value);
    }

    public String getOperation() {
        return operation;
    }

    public PCB getPCB() {
        return pcb;
    }

    public void execute() {
        try {
            switch (operation.toLowerCase()) {
                case "add":
                    System.out.println("Result: " + (operand1 + operand2));
                    break;
                case "subtract":
                    System.out.println("Result: " + (operand1 - operand2));
                    break;
                case "multiply":
                    System.out.println("Result: " + (operand1 * operand2));
                    break;
                case "divide":
                    if (operand2 == 0) {
                        throw new ArithmeticException("Division by zero is not allowed!");
                    }
                    System.out.println("Result: " + (operand1 / operand2));
                    break;
                case "print":
                    System.out.println("Print command: " + varName); // Print the variable or result
                    break;
                case "assign":
                    System.out.println("Assigned value " + operand1 + " to " + varName);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operation: " + operation);
            }
            pcb.incrementProgramCounter();
        } catch (ArithmeticException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
