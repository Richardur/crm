package network.api_request_model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ManagerReactionWorkInPlan {

    @SerializedName("success")
    private Boolean success;

    @SerializedName("data")
    private Data data;

    @SerializedName("error")
    private String error;

    public class Data {
        @SerializedName("ManagerReactionInPlanHeader")
        private List<ManagerReactionInPlanHeader> managerReactionInPlanHeaderList;
    }

    public class ManagerReactionInPlanHeader {
        @SerializedName("reactionHeaderID")
        private Integer reactionHeaderID;

        @SerializedName("reactionManagerID")
        private Integer reactionManagerID;

        @SerializedName("reactionManageName")
        private String reactionManageName;

        @SerializedName("reactionForCustomerID")
        private Integer reactionForCustomerID;

        @SerializedName("reactionForCustomerName")
        private String reactionForCustomerName;

        @SerializedName("reactionForCustomerCode")
        private String reactionForCustomerCode;

        @SerializedName("reactionForCustomerContactPersonID")
        private Integer reactionForCustomerContactPersonID;

        @SerializedName("reactionForCustomerContactPersonName")
        private String reactionForCustomerContactPersonName;

        @SerializedName("reactionByOrderID")
        private Integer reactionByOrderID;

        @SerializedName("reactionByOrderNo")
        private String reactionByOrderNo;

        @SerializedName("ManagerReactionWork")
        private List<ManagerReactionWork> managerReactionWork;

        // Getters and Setters


        public Integer getReactionHeaderID() {
            return reactionHeaderID;
        }

        public void setReactionHeaderID(Integer reactionHeaderID) {
            this.reactionHeaderID = reactionHeaderID;
        }

        public Integer getReactionManagerID() {
            return reactionManagerID;
        }

        public void setReactionManagerID(Integer reactionManagerID) {
            this.reactionManagerID = reactionManagerID;
        }

        public String getReactionManageName() {
            return reactionManageName;
        }

        public void setReactionManageName(String reactionManageName) {
            this.reactionManageName = reactionManageName;
        }

        public Integer getReactionForCustomerID() {
            return reactionForCustomerID;
        }

        public void setReactionForCustomerID(Integer reactionForCustomerID) {
            this.reactionForCustomerID = reactionForCustomerID;
        }

        public String getReactionForCustomerName() {
            return reactionForCustomerName;
        }

        public void setReactionForCustomerName(String reactionForCustomerName) {
            this.reactionForCustomerName = reactionForCustomerName;
        }

        public String getReactionForCustomerCode() {
            return reactionForCustomerCode;
        }

        public void setReactionForCustomerCode(String reactionForCustomerCode) {
            this.reactionForCustomerCode = reactionForCustomerCode;
        }

        public Integer getReactionForCustomerContactPersonID() {
            return reactionForCustomerContactPersonID;
        }

        public void setReactionForCustomerContactPersonID(Integer reactionForCustomerContactPersonID) {
            this.reactionForCustomerContactPersonID = reactionForCustomerContactPersonID;
        }

        public String getReactionForCustomerContactPersonName() {
            return reactionForCustomerContactPersonName;
        }

        public void setReactionForCustomerContactPersonName(String reactionForCustomerContactPersonName) {
            this.reactionForCustomerContactPersonName = reactionForCustomerContactPersonName;
        }

        public Integer getReactionByOrderID() {
            return reactionByOrderID;
        }

        public void setReactionByOrderID(Integer reactionByOrderID) {
            this.reactionByOrderID = reactionByOrderID;
        }

        public String getReactionByOrderNo() {
            return reactionByOrderNo;
        }

        public void setReactionByOrderNo(String reactionByOrderNo) {
            this.reactionByOrderNo = reactionByOrderNo;
        }

        public List<ManagerReactionWork> getManagerReactionWork() {
            return managerReactionWork;
        }

        public void setManagerReactionWork(List<ManagerReactionWork> managerReactionWork) {
            this.managerReactionWork = managerReactionWork;
        }
    }


    public class ManagerReactionWork{
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

}
