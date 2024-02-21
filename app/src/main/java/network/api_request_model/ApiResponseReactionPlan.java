package network.api_request_model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponseReactionPlan {
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("ManagerReactionInPlanHeader")
        private List<ManagerReactionWorkInPlan.ManagerReactionInPlanHeader> managerReactionInPlanHeaderList;

        public List<ManagerReactionWorkInPlan.ManagerReactionInPlanHeader> getManagerReactionInPlanHeaderList() {
            return managerReactionInPlanHeaderList;
        }

        public void setManagerReactionInPlanHeaderList(List<ManagerReactionWorkInPlan.ManagerReactionInPlanHeader> managerReactionInPlanHeaderList) {
            this.managerReactionInPlanHeaderList = managerReactionInPlanHeaderList;
        }
    }
}
