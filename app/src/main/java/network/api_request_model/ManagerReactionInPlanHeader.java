package network.api_request_model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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