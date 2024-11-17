public class Task {
    private String operation;
    private int operand1;
    private int operand2;
    private PCB pcb;

    public Task(String operation, int operand1, int operand2, int processId, int memoryBoundary) {
        this.operation = operation;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.pcb = new PCB(processId, memoryBoundary);
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
                default:
                    throw new IllegalArgumentException("Invalid operation: " + operation);
            }
            pcb.incrementProgramCounter();
        } catch (ArithmeticException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
