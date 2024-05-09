package network;

import com.google.gson.annotations.SerializedName;

public class ApiResponseUpdate {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ResponseData data;

    @SerializedName("error")
    private String error;

    // Constructor, getters and setters
    public ApiResponseUpdate(boolean success, ResponseData data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ResponseData getData() {
        return data;
    }

    public void setData(ResponseData data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // Nested class to represent any structured data in the response
    public static class ResponseData {
        // Assume the data might contain details about the updated tasks
        @SerializedName("reactionHeaderID")
        private Integer reactionHeaderID;

        @SerializedName("details")
        private String details;

        public ResponseData(Integer reactionHeaderID, String details) {
            this.reactionHeaderID = reactionHeaderID;
            this.details = details;
        }

        public Integer getReactionHeaderID() {
            return reactionHeaderID;
        }

        public void setReactionHeaderID(Integer reactionHeaderID) {
            this.reactionHeaderID = reactionHeaderID;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}
