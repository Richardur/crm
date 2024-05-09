package network;

import com.google.gson.annotations.SerializedName;

import network.api_request_model.ManagerReactionWork;
import network.api_request_model.ManagerReactionWorkInPlan;

public class ManagerReactionUpdateRequest {
    @SerializedName("userId")
    private String userId;

    @SerializedName("apiKey")
    private String apiKey;

    @SerializedName("putType")
    private String putType;

    @SerializedName("action")
    private String action;

    @SerializedName("ManagerReactionInPlanHeader")
    private ManagerReactionWorkInPlan.ManagerReactionInPlanHeader managerReactionInPlanHeader;

    @SerializedName("limit")
    private String limit;

}




