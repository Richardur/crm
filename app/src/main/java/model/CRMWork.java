package model;

import com.google.gson.annotations.SerializedName;

import java.util.concurrent.TimeUnit;

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

    public long getRemindTimeInMillis() {
        if (this.CRMWorkRemindTime == null) {
            // Handle the null case, perhaps return a default value or log a warning
            return 0;
        }

        String timeString = this.CRMWorkRemindTime.trim().toLowerCase();

        if (timeString.endsWith("d")) {
            String numStr = timeString.substring(0, timeString.length() - 1);
            int days = parseNumber(numStr);
            return TimeUnit.DAYS.toMillis(days);
        } else if (timeString.endsWith("h")) {
            String numStr = timeString.substring(0, timeString.length() - 1);
            int hours = parseNumber(numStr);
            return TimeUnit.HOURS.toMillis(hours);
        } else if (timeString.endsWith("min") || timeString.endsWith("m")) {  // Handles both "min" and "m"
            String numStr = timeString.replaceAll("[^0-9]", ""); // Extract digits only
            int minutes = parseNumber(numStr);
            return TimeUnit.MINUTES.toMillis(minutes);
        } else {
            // Default to 0 if format is unrecognized
            return 0;
        }
    }

    private int parseNumber(String numStr) {
        numStr = numStr.replaceAll("[^0-9]", ""); // Remove non-digit characters
        if (numStr.isEmpty()) {
            return 0; // Default value if no digits are found
        }
        return Integer.parseInt(numStr);
    }

}
