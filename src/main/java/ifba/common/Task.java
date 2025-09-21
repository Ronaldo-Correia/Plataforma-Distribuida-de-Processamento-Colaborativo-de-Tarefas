package ifba.common;

public class Task {
    private String id;
    private String payload;
    private String assignedWorker;
    private int timestamp;

    public Task(String id, String payload) {
        this.id = id;
        this.payload = payload;
    }

    public String getId() { return id; }
    public String getPayload() { return payload; }

    public String getAssignedWorker() { return assignedWorker; }
    public void setAssignedWorker(String worker) { this.assignedWorker = worker; }

    public int getTimestamp() { return timestamp; }
    public void setTimestamp(int timestamp) { this.timestamp = timestamp; }
}
