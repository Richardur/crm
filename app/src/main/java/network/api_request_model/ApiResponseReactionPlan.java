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

    public class Data {
        @SerializedName("ManagerReactionWork")
        private List<ManagerReactionWork> managerReactionWorkList;

        public List<ManagerReactionWork> getManagerReactionWorkList() {
            return managerReactionWorkList;
        }
    }
}
