package model;

import com.google.gson.annotations.SerializedName;

public class CRMWork {

    @SerializedName("CRMWorkID")
    private int CRMWorkID;

    @SerializedName("CRMWorkName")
    private String CRMWorkName;

    @SerializedName("CRMWorkType")
    private String CRMWorkType;

    @SerializedName("CRMWorkPriority")
    private int CRMWorkPriority;

    @SerializedName("CRMWorkFormat")
    private String CRMWorkFormat;

    @SerializedName("CRMWorkRemindTime")
    private String CRMWorkRemindTime;

    @SerializedName("CRMWorkCategory")
    private String CRMWorkCategory;

    // Getters and setters
    public int getCRMWorkID() {
        return CRMWorkID;
    }

    public void setCRMWorkID(int CRMWorkID) {
        this.CRMWorkID = CRMWorkID;
    }

    public String getCRMWorkName() {
        return CRMWorkName;
    }

    public void setCRMWorkName(String CRMWorkName) {
        this.CRMWorkName = CRMWorkName;
    }

    public String getCRMWorkType() {
        return CRMWorkType;
    }

    public void setCRMWorkType(String CRMWorkType) {
        this.CRMWorkType = CRMWorkType;
    }

    public int getCRMWorkPriority() {
        return CRMWorkPriority;
    }

    public void setCRMWorkPriority(int CRMWorkPriority) {
        this.CRMWorkPriority = CRMWorkPriority;
    }

    public String getCRMWorkFormat() {
        return CRMWorkFormat;
    }

    public void setCRMWorkFormat(String CRMWorkFormat) {
        this.CRMWorkFormat = CRMWorkFormat;
    }

    public String getCRMWorkRemindTime() {
        return CRMWorkRemindTime;
    }

    public void setCRMWorkRemindTime(String CRMWorkRemindTime) {
        this.CRMWorkRemindTime = CRMWorkRemindTime;
    }

    public String getCRMWorkCategory() {
        return CRMWorkCategory;
    }

    public void setCRMWorkCategory(String CRMWorkCategory) {
        this.CRMWorkCategory = CRMWorkCategory;
    }
}
