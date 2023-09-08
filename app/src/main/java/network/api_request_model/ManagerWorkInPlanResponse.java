package network.api_request_model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ManagerWorkInPlanResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<ManagerWorkInPlan> managerWorkInPlanList;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<ManagerWorkInPlan> getManagerWorkInPlanList() {
        return managerWorkInPlanList;
    }
}