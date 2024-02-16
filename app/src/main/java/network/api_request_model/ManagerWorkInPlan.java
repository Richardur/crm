package network.api_request_model;

import com.google.gson.annotations.SerializedName;
import java.sql.Timestamp;

public class ManagerWorkInPlan {
    @SerializedName("workInPlanID")
    private Integer workInPlanID;

    @SerializedName("workInPlanManagerID")
    private Integer workInPlanManagerID;

    @SerializedName("workInPlanManageName")
    private String workInPlanManageName;

    @SerializedName("workInPlanTerm")
    private Timestamp workInPlanTerm;

    @SerializedName("workInPlanStart")
    private Timestamp workInPlanStart;

    @SerializedName("workInPlanDone")
    private Timestamp workInPlanDone;

    @SerializedName("workInPlanStarted")
    private Timestamp workInPlanStarted;

    @SerializedName("workInPlanReviewed")
    private Timestamp workInPlanReviewed;

    @SerializedName("workInPlanDelayed")
    private Integer workInPlanDelayed;

    @SerializedName("workInPlanPriority")
    private String workInPlanPriority;

    @SerializedName("workInPlanForCustomerID")
    private String workInPlanForCustomerID;

    @SerializedName("workInPlanForCutomerName")
    private String workInPlanForCutomerName;

    @SerializedName("workInPlanForCustomerCode")
    private String workInPlanForCustomerCode;

    @SerializedName("workInPlanByOrderID")
    private Integer workInPlanByOrderID;

    @SerializedName("workInPlanByOrderNo")
    private String workInPlanByOrderNo;

    @SerializedName("workInPlanName")
    private String workInPlanName;

    @SerializedName("workInPlanNote")
    private String workInPlanNote;

    // Constructors

    public ManagerWorkInPlan() {
        // Initialize fields with default values if needed
    }

    // Getters and setters for all fields

    public Integer getWorkInPlanID() {
        return workInPlanID;
    }

    public void setWorkInPlanID(Integer workInPlanID) {
        this.workInPlanID = workInPlanID;
    }

    public Integer getWorkInPlanManagerID() {
        return workInPlanManagerID;
    }

    public void setWorkInPlanManagerID(Integer workInPlanManagerID) {
        this.workInPlanManagerID = workInPlanManagerID;
    }

    public String getWorkInPlanManageName() {
        return workInPlanManageName;
    }

    public void setWorkInPlanManageName(String workInPlanManageName) {
        this.workInPlanManageName = workInPlanManageName;
    }

    public Timestamp getWorkInPlanTerm() {
        return workInPlanTerm;
    }

    public void setWorkInPlanTerm(Timestamp workInPlanTerm) {
        this.workInPlanTerm = workInPlanTerm;
    }

    public Timestamp getWorkInPlanStart() {
        return workInPlanStart;
    }

    public void setWorkInPlanStart(Timestamp workInPlanStart) {
        this.workInPlanStart = workInPlanStart;
    }

    public Timestamp getWorkInPlanDone() {
        return workInPlanDone;
    }

    public void setWorkInPlanDone(Timestamp workInPlanDone) {
        this.workInPlanDone = workInPlanDone;
    }

    public Timestamp getWorkInPlanStarted() {
        return workInPlanStarted;
    }

    public void setWorkInPlanStarted(Timestamp workInPlanStarted) {
        this.workInPlanStarted = workInPlanStarted;
    }

    public Timestamp getWorkInPlanReviewed() {
        return workInPlanReviewed;
    }

    public void setWorkInPlanReviewed(Timestamp workInPlanReviewed) {
        this.workInPlanReviewed = workInPlanReviewed;
    }

    public Integer getWorkInPlanDelayed() {
        return workInPlanDelayed;
    }

    public void setWorkInPlanDelayed(Integer workInPlanDelayed) {
        this.workInPlanDelayed = workInPlanDelayed;
    }

    public String getWorkInPlanPriority() {
        return workInPlanPriority;
    }

    public void setWorkInPlanPriority(String workInPlanPriority) {
        this.workInPlanPriority = workInPlanPriority;
    }

    public String getWorkInPlanForCustomerID() {
        return workInPlanForCustomerID;
    }

    public void setWorkInPlanForCustomerID(String workInPlanForCustomerID) {
        this.workInPlanForCustomerID = workInPlanForCustomerID;
    }

    public String getWorkInPlanForCutomerName() {
        return workInPlanForCutomerName;
    }

    public void setWorkInPlanForCutomerName(String workInPlanForCutomerName) {
        this.workInPlanForCutomerName = workInPlanForCutomerName;
    }

    public String getWorkInPlanForCustomerCode() {
        return workInPlanForCustomerCode;
    }

    public void setWorkInPlanForCustomerCode(String workInPlanForCustomerCode) {
        this.workInPlanForCustomerCode = workInPlanForCustomerCode;
    }

    public Integer getWorkInPlanByOrderID() {
        return workInPlanByOrderID;
    }

    public void setWorkInPlanByOrderID(Integer workInPlanByOrderID) {
        this.workInPlanByOrderID = workInPlanByOrderID;
    }

    public String getWorkInPlanByOrderNo() {
        return workInPlanByOrderNo;
    }

    public void setWorkInPlanByOrderNo(String workInPlanByOrderNo) {
        this.workInPlanByOrderNo = workInPlanByOrderNo;
    }

    public String getWorkInPlanName() {
        return workInPlanName;
    }

    public void setWorkInPlanName(String workInPlanName) {
        this.workInPlanName = workInPlanName;
    }

    public String getWorkInPlanNote() {
        return workInPlanNote;
    }

    public void setWorkInPlanNote(String workInPlanNote) {
        this.workInPlanNote = workInPlanNote;
    }

    @Override
    public String toString() {
        return "ManagerWorkInPlan{" +
                "workInPlanID=" + workInPlanID +
                ", workInPlanManagerID=" + workInPlanManagerID +
                ", workInPlanManageName='" + workInPlanManageName + '\'' +
                // Include other fields here...
                '}';
    }
}