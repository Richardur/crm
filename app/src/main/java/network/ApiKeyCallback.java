package network;

public interface ApiKeyCallback {
    public interface OnApiKeyRetrieved {
        void onApiKeyReceived(String apiKey);
    }
}
