import java.util.*;

public class MultiCoreSystem {
    private static int tempCounter = 0;

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
                if (tokens[2].equals("input")) {
                    System.out.print("Enter value for " + tokens[1] + ": ");
                    int userInput = scanner.nextInt();
                    newProcess = new Process("assign", tokens[1], userInput, generateProcessId(), 100);
                    sharedMemory.write(tokens[1], userInput);
                    allProcesses.add(newProcess);
                } else {
                    String operand1Name = tokens[3];
                    String operand2Name = tokens[4];
                    Integer operand1 = sharedMemory.read(operand1Name);
                    Integer operand2 = sharedMemory.read(operand2Name);

                    if (operand1 != null && operand2 != null) {
                        int result = switch (tokens[2].toLowerCase()) {
                            case "add" -> operand1 + operand2;
                            case "subtract" -> operand1 - operand2;
                            case "multiply" -> operand1 * operand2;
                            case "divide" -> operand2 != 0 ? operand1 / operand2 : 0;
                            default -> throw new IllegalArgumentException("Unknown operation: " + tokens[2]);
                        };

                        sharedMemory.write(tokens[1], result);
                        newProcess = new Process("assign", tokens[1], result, generateProcessId(), 100);
                        allProcesses.add(newProcess);
                    }
                }
            }
            case "print" -> {
                String varName = tokens[1];
                newProcess = new Process("print", varName, 0, generateProcessId(), 100);
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
