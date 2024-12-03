public class Process {
    private final String operation;
    private final String varName;
    private final int operand1;
    private final int operand2;
    private final PCB pcb;

    public Process(String operation, String varName, int value, int processId, int memoryBoundary) {
        this.operation = operation;
        this.varName = varName;
        this.operand1 = value;
        this.operand2 = 0;
        this.pcb = new PCB(processId, memoryBoundary);
    }

    public Process(String operation, String varName, int operand1, int operand2, int processId, int memoryBoundary) {
        this.operation = operation;
        this.varName = varName;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.pcb = new PCB(processId, memoryBoundary);
    }

    public PCB getPCB() {
        return pcb;
    }

    public void execute(SharedMemory sharedMemory) {
        try {
            int result = 0;

            System.out.println("Executing operation: " + operation + " on variable: " + varName);
            System.out.println("Operands: Operand1 = " + operand1 + ", Operand2 = " + operand2);

            switch (operation.toLowerCase()) {
                case "assign":
                    sharedMemory.write(varName, operand1);
                    System.out.println("Memory updated: " + varName + " = " + operand1);
                    break;
                case "add":
                    result = operand1 + operand2;
                    sharedMemory.write(varName, result);
                    System.out.println("Memory updated: " + varName + " = " + result);
                    break;
                case "subtract":
                    result = operand1 - operand2;
                    sharedMemory.write(varName, result);
                    System.out.println("Memory updated: " + varName + " = " + result);
                    break;
                case "multiply":
                    result = operand1 * operand2;
                    sharedMemory.write(varName, result);
                    System.out.println("Memory updated: " + varName + " = " + result);
                    break;
                case "divide":
                    if (operand2 != 0) {
                        result = operand1 / operand2;
                        sharedMemory.write(varName, result);
                        System.out.println("Memory updated: " + varName + " = " + result);
                    } else {
                        System.out.println("Division by zero is not allowed!");
                    }
                    break;
                case "print":
                    Integer value = sharedMemory.read(varName);
                    if (value != null) {
                        System.out.println(varName + " = " + value);
                    } else {
                        System.out.println(varName + " = null (not computed yet)");
                    }
                    break;
                default:
                    System.out.println("Unknown operation: " + operation);
            }

            pcb.incrementProgramCounter();
        } catch (Exception e) {
            System.out.println("Error executing process: " + e.getMessage());
        }
    }
}
