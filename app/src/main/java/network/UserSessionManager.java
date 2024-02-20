package network;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {
    public static boolean userSignedIn = false;

    private static final String PREF_NAME = "UserSessionPref";
    private static final String KEY_API_KEY = "apiKey";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "userId";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    public static void saveUsername(Context context, String username) {
        init(context);
        editor.putString(KEY_USERNAME, username);
        editor.commit(); // Use commit to ensure the data is saved immediately
    }
    public static void saveApiKey(Context context, String apiKey) {
        init(context);
        editor.putString(KEY_API_KEY, apiKey);
        editor.commit(); // Use commit to ensure the data is saved immediately
    }
    public static void saveUserId(Context context, String userId) {
        init(context);
        editor.putString(KEY_USER_ID, userId);
        editor.commit(); // Use commit to ensure the data is saved immediately
    }

    public static String getApiKey(Context context) {
        init(context);
        return sharedPreferences.getString(KEY_API_KEY, null);
    }
    public static String getUsername(Context context) {
        init(context);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }
    public static String getUserId(Context context) {
        init(context);
        return sharedPreferences.getString(KEY_USER_ID, null);
    }
}
