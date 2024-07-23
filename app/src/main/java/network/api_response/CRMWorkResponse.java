package network.api_response;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import model.CRMWork;

public class CRMWorkResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Data data;

    @SerializedName("error")
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public static class Data {

        @SerializedName("CrmWork")
        private List<CRMWork> crmWorkList;

        public List<CRMWork> getCrmWorkList() {
            return crmWorkList;
        }
    }
}
