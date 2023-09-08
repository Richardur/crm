package model;

import java.sql.Timestamp;

public class Task {
    private int ID;
    private String workInPlanForCutomerName;
    private String workInPlanName;
    private String workInPlanNote;
    private Timestamp workInPlanTerm; // Format: "hh:mm"
    private Timestamp workInPlanDone;

    public Task(int ID, String workInPlanForCutomerName, String workInPlanName,
                String workInPlanNote, Timestamp workInPlanTerm, Timestamp workInPlanDone) {
        this.ID = ID;
        this.workInPlanForCutomerName = workInPlanForCutomerName;
        this.workInPlanName = workInPlanName;
        this.workInPlanNote = workInPlanNote;
        this.workInPlanTerm = workInPlanTerm;
        this.workInPlanDone = workInPlanDone;
    }

    // Getter methods for all fields

    public int getID() {
        return ID;
    }

    public String getWorkInPlanForCutomerName() {
        return workInPlanForCutomerName;
    }

    public String getWorkInPlanName() {
        return workInPlanName;
    }

    public String getWorkInPlanNote() {
        return workInPlanNote;
    }

    public Timestamp getWorkInPlanTerm() {
        return workInPlanTerm;
    }

    public Timestamp isWorkInPlanDone() {
        return workInPlanDone;
    }
}