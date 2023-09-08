package network;

public class ApiGetRequest {
    private String userId;
    private String apiKey;
    private String getInfoType;
    private String language;
    private String action;
    private String where;
    private String limit;
    private String orderBy;

    public ApiGetRequest(String userId, String apiKey, String getInfoType, String language, String action, String where, String limit, String orderBy) {
        this.userId = userId;
        this.apiKey = apiKey;
        this.getInfoType = getInfoType;
        this.language = language;
        this.action = action;
        this.where = where;
        this.limit = limit;
        this.orderBy = orderBy;
    }
}
