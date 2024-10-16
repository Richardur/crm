package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.util.List;

import network.api_request_model.ManagerReactionWorkInPlan;

public class ManagerReactionUpdateRequest {
    @SerializedName("userId")
    private String userId;

    @SerializedName("apiKey")
    private String apiKey;

    @SerializedName("action")
    private String action;

    @SerializedName("languageCode")
    private String languageCode;

    @SerializedName("ManagerReactionInPlanHeaderReg")
    private String managerReactionInPlanHeaderReg;

    @SerializedName("ManagerReactionWorkReg")
    private String managerReactionWorkReg;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getManagerReactionInPlanHeaderReg() {
        return managerReactionInPlanHeaderReg;
    }

    public void setManagerReactionInPlanHeaderReg(ManagerReactionWorkInPlan.ManagerReactionInPlanHeader managerReactionInPlanHeader) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setLenient().create();

        this.managerReactionInPlanHeaderReg = gson.toJson(managerReactionInPlanHeader);
    }

    public String getManagerReactionWorkReg() {
        return managerReactionWorkReg;
    }

    public void setManagerReactionWorkReg(List<ManagerReactionWorkInPlan.ManagerReactionWork> managerReactionWorkList) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setLenient().create();
        this.managerReactionWorkReg = gson.toJson(managerReactionWorkList);
    }
}
