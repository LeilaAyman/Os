public class PCB {
    private int processId;
    private int programCounter;
    private int memoryBoundary;

    public PCB(int processId, int memoryBoundary) {
        this.processId = processId;
        this.memoryBoundary = memoryBoundary;
        this.programCounter = 0;
    }

    public int getProcessId() {
        return processId;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void incrementProgramCounter() {
        this.programCounter++;
    }

    @Override
    public String toString() {
        return "PCB [Process ID: " + processId + ", Program Counter: " + programCounter + ", Memory Boundary: " + memoryBoundary + "]";
    }
}
