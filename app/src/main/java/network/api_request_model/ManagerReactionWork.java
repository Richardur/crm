package network.api_request_model;

import com.google.gson.annotations.SerializedName;

public class ManagerReactionWork {
    @SerializedName("reactionWorkID")
    private Integer reactionWorkID;

    @SerializedName("reactionWorkManagerID")
    private Integer reactionWorkManagerID;

    @SerializedName("reactionWorkManageName")
    private String reactionWorkManageName;

    @SerializedName("reactionWorkDoneByID")
    private Integer reactionWorkDoneByID;

    @SerializedName("reactionWorkDoneByName")
    private String reactionWorkDoneByName;

    @SerializedName("reactionWorkTerm")
    private String reactionWorkTerm; // Using String to simplify, convert to Date or Timestamp as needed

    @SerializedName("reactionWorkStart")
    private String reactionWorkStart; // Same as above

    @SerializedName("reactionWorkDone")
    private String reactionWorkDone; // Same as above

    @SerializedName("reactionWorkStarted")
    private String reactionWorkStarted; // Same as above

    @SerializedName("reactionWorkReviewed")
    private String reactionWorkReviewed; // Same as above

    @SerializedName("reactionWorkDelayed")
    private Integer reactionWorkDelayed;

    @SerializedName("reactionWorkPriority")
    private String reactionWorkPriority;

    @SerializedName("reactionWorkForCustomerID")
    private Integer reactionWorkForCustomerID;

    @SerializedName("reactionWorkForCustomerName")
    private String reactionWorkForCustomerName;

    @SerializedName("reactionWorkForCustomerCode")
    private String reactionWorkForCustomerCode;

    @SerializedName("reactionWorkActionID")
    private Integer reactionWorkActionID;

    @SerializedName("reactionWorkActionName")
    private String reactionWorkActionName;

    @SerializedName("reactionWorkNote")
    private String reactionWorkNote;

    @SerializedName("reactionWorkRemindID")
    private Integer reactionWorkRemindID;

    @SerializedName("reactionWorkRemindTime")
    private String reactionWorkRemindTime; // Using String to simplify, convert to Date or Timestamp as needed

    @SerializedName("reactionWorkRemindDateTime")
    private String reactionWorkRemindDateTime; // Same as above

    // Getters and Setters

    public Integer getReactionWorkID() {
        return reactionWorkID;
    }

    public void setReactionWorkID(Integer reactionWorkID) {
        this.reactionWorkID = reactionWorkID;
    }

    public Integer getReactionWorkManagerID() {
        return reactionWorkManagerID;
    }

    public void setReactionWorkManagerID(Integer reactionWorkManagerID) {
        this.reactionWorkManagerID = reactionWorkManagerID;
    }

    public String getReactionWorkManageName() {
        return reactionWorkManageName;
    }

    public void setReactionWorkManageName(String reactionWorkManageName) {
        this.reactionWorkManageName = reactionWorkManageName;
    }

    public Integer getReactionWorkDoneByID() {
        return reactionWorkDoneByID;
    }

    public void setReactionWorkDoneByID(Integer reactionWorkDoneByID) {
        this.reactionWorkDoneByID = reactionWorkDoneByID;
    }

    public String getReactionWorkDoneByName() {
        return reactionWorkDoneByName;
    }

    public void setReactionWorkDoneByName(String reactionWorkDoneByName) {
        this.reactionWorkDoneByName = reactionWorkDoneByName;
    }

    public String getReactionWorkTerm() {
        return reactionWorkTerm;
    }

    public void setReactionWorkTerm(String reactionWorkTerm) {
        this.reactionWorkTerm = reactionWorkTerm;
    }

    public String getReactionWorkStart() {
        return reactionWorkStart;
    }

    public void setReactionWorkStart(String reactionWorkStart) {
        this.reactionWorkStart = reactionWorkStart;
    }

    public String getReactionWorkDone() {
        return reactionWorkDone;
    }

    public void setReactionWorkDone(String reactionWorkDone) {
        this.reactionWorkDone = reactionWorkDone;
    }

    public String getReactionWorkStarted() {
        return reactionWorkStarted;
    }

    public void setReactionWorkStarted(String reactionWorkStarted) {
        this.reactionWorkStarted = reactionWorkStarted;
    }

    public String getReactionWorkReviewed() {
        return reactionWorkReviewed;
    }

    public void setReactionWorkReviewed(String reactionWorkReviewed) {
        this.reactionWorkReviewed = reactionWorkReviewed;
    }

    public Integer getReactionWorkDelayed() {
        return reactionWorkDelayed;
    }

    public void setReactionWorkDelayed(Integer reactionWorkDelayed) {
        this.reactionWorkDelayed = reactionWorkDelayed;
    }

    public String getReactionWorkPriority() {
        return reactionWorkPriority;
    }

    public void setReactionWorkPriority(String reactionWorkPriority) {
        this.reactionWorkPriority = reactionWorkPriority;
    }

    public Integer getReactionWorkForCustomerID() {
        return reactionWorkForCustomerID;
    }

    public void setReactionWorkForCustomerID(Integer reactionWorkForCustomerID) {
        this.reactionWorkForCustomerID = reactionWorkForCustomerID;
    }

    public String getReactionWorkForCustomerName() {
        return reactionWorkForCustomerName;
    }

    public void setReactionWorkForCustomerName(String reactionWorkForCustomerName) {
        this.reactionWorkForCustomerName = reactionWorkForCustomerName;
    }

    public String getReactionWorkForCustomerCode() {
        return reactionWorkForCustomerCode;
    }

    public void setReactionWorkForCustomerCode(String reactionWorkForCustomerCode) {
        this.reactionWorkForCustomerCode = reactionWorkForCustomerCode;
    }

    public Integer getReactionWorkActionID() {
        return reactionWorkActionID;
    }

    public void setReactionWorkActionID(Integer reactionWorkActionID) {
        this.reactionWorkActionID = reactionWorkActionID;
    }

    public String getReactionWorkActionName() {
        return reactionWorkActionName;
    }

    public void setReactionWorkActionName(String reactionWorkActionName) {
        this.reactionWorkActionName = reactionWorkActionName;
    }

    public String getReactionWorkNote() {
        return reactionWorkNote;
    }

    public void setReactionWorkNote(String reactionWorkNote) {
        this.reactionWorkNote = reactionWorkNote;
    }

    public Integer getReactionWorkRemindID() {
        return reactionWorkRemindID;
    }

    public void setReactionWorkRemindID(Integer reactionWorkRemindID) {
        this.reactionWorkRemindID = reactionWorkRemindID;
    }

    public String getReactionWorkRemindTime() {
        return reactionWorkRemindTime;
    }

    public void setReactionWorkRemindTime(String reactionWorkRemindTime) {
        this.reactionWorkRemindTime = reactionWorkRemindTime;
    }

    public String getReactionWorkRemindDateTime() {
        return reactionWorkRemindDateTime;
    }

    public void setReactionWorkRemindDateTime(String reactionWorkRemindDateTime) {
        this.reactionWorkRemindDateTime = reactionWorkRemindDateTime;
    }

}
