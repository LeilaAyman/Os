import java.util.*;

public class MultiCoreSystem {
    private static int tempCounter = 0; // To generate unique temp variable names

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SharedMemory sharedMemory = new SharedMemory();
        SlaveCore[] slaveCores = { new SlaveCore(sharedMemory), new SlaveCore(sharedMemory) };
        Queue<Integer> readyQueue = new LinkedList<>();

        List<String> instructions = Arrays.asList(
            "assign a input",
            "assign b input",
            "assign z add a b",
            "print z",
            "assign x multiply a b",
            "print x",
            "assign y divide a b",
            "print y",
            "assign p subtract a b",
            "print p"
        );

        List<Process> allProcesses = new ArrayList<>();
        for (String instruction : instructions) {
            Process process = processInstruction(instruction.trim(), allProcesses, sharedMemory, scanner);
            if (process != null) {
                readyQueue.add(process.getPCB().getProcessId());
                System.out.println("Task added to readyQueue: " + instruction);
            }
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
                if (tokens.length == 3) {
                    String varName = tokens[1];
                    String value = tokens[2];

                    if (value.equals("input")) {
                        System.out.print("Enter value for " + varName + ": ");
                        int userInput = scanner.nextInt();
                        newProcess = new Process("assign", varName, userInput, generateProcessId(), 100);
                        sharedMemory.write(varName, userInput);
                        allProcesses.add(newProcess);
                        System.out.println("Created assignment task: " + varName + " = " + userInput);
                    } else if (value.startsWith("temp")) {
                        Integer tempValue = sharedMemory.read(value);
                        if (tempValue != null) {
                            newProcess = new Process("assign", varName, tempValue, generateProcessId(), 100);
                            allProcesses.add(newProcess);
                            System.out.println("Assigned temp value: " + value + " to " + varName);
                        } else {
                            System.out.println("Temp variable " + value + " not found in shared memory.");
                        }
                    } else {
                        try {
                            int numericValue = Integer.parseInt(value);
                            newProcess = new Process("assign", varName, numericValue, generateProcessId(), 100);
                            sharedMemory.write(varName, numericValue);
                            allProcesses.add(newProcess);
                            System.out.println("Created direct assignment task: " + instruction);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid numeric value: " + value);
                        }
                    }
                } else if (tokens.length == 4) {
                    String varName = tokens[1];
                    String op = tokens[2].toLowerCase();
                    String operand1Name = tokens[3];
                    String operand2Name = tokens[4];

                    Integer operand1 = sharedMemory.read(operand1Name);
                    Integer operand2 = sharedMemory.read(operand2Name);

                    if (operand1 != null && operand2 != null) {
                        String tempVarName = "temp" + (++tempCounter);
                        Process arithmeticProcess = new Process(op, tempVarName, operand1, operand2, generateProcessId(), 100);
                        allProcesses.add(arithmeticProcess);
                        sharedMemory.write(tempVarName, 0);
                        System.out.println("Created " + op + " task: " + operand1Name + ", " + operand2Name + " with result stored in " + tempVarName);

                        newProcess = new Process("assign", varName, 0, generateProcessId(), 100);
                        allProcesses.add(newProcess);
                        System.out.println("Created assignment task: " + varName + " = " + tempVarName);
                    } else {
                        System.out.println("Operands not found for operation: " + op + " on " + varName);
                    }
                }
            }
            case "add", "subtract", "multiply", "divide" -> {
                if (tokens.length == 3) {
                    String operand1Name = tokens[1];
                    String operand2Name = tokens[2];

                    Integer operand1 = sharedMemory.read(operand1Name);
                    Integer operand2 = sharedMemory.read(operand2Name);

                    if (operand1 != null && operand2 != null) {
                        String tempVarName = "temp" + (++tempCounter);
                        newProcess = new Process(operation, tempVarName, operand1, operand2, generateProcessId(), 100);
                        allProcesses.add(newProcess);
                        sharedMemory.write(tempVarName, 0);
                        System.out.println("Created " + operation + " task: " + operand1Name + ", " + operand2Name + " with result stored in " + tempVarName);
                    } else {
                        System.out.println("Operands not found for operation: " + operation);
                    }
                }
            }
            case "print" -> {
                if (tokens.length == 2) {
                    String varName = tokens[1];
                    newProcess = new Process("print", varName, 0, generateProcessId(), 100);
                    allProcesses.add(newProcess);
                    System.out.println("Created print task: " + instruction);
                }
            }
            default -> System.out.println("Unknown instruction: " + instruction);
        }

        return newProcess;
    }

    private static int generateProcessId() {
        return (int) (Math.random() * 1000);
    }
}
