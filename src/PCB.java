public class PCB {
    private final int processId;
    private int programCounter;
    private final int memoryBoundary;

    public PCB(int processId, int memoryBoundary) {
        this.processId = processId;
        this.programCounter = 0;
        this.memoryBoundary = memoryBoundary;
    }

    public int getProcessId() {
        return processId;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void incrementProgramCounter() {
        programCounter++;
    }
}
