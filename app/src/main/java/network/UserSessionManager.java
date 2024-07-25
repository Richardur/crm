package network;

import android.content.Context;
import android.content.SharedPreferences;

import model.Employe;

public class UserSessionManager {
    public static boolean userSignedIn = false;

    private static final String PREF_NAME = "UserSessionPref";
    private static final String KEY_API_KEY = "apiKey";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMPLOYEE_ID = "employeeId";
    private static final String KEY_EMPLOYEE_NAME = "employeeName";
    private static final String KEY_REMEMBER_ME_USERNAME = "rememberMeUsername";
    private static final String KEY_REMEMBER_ME_PASSWORD = "rememberMePassword";

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

    public static void saveEmployeeDetails(Context context, Employe employee) {
        init(context);
        editor.putString(KEY_EMPLOYEE_ID, String.valueOf(employee.getEmployeID()));
        editor.putString(KEY_EMPLOYEE_NAME, employee.getEmployeName() + " " + employee.getEmploeerSurname());
        editor.commit(); // Commit the changes to save them
    }

    public static void saveRememberMeCredentials(Context context, String username, String password) {
        init(context);
        editor.putString(KEY_REMEMBER_ME_USERNAME, username);
        editor.putString(KEY_REMEMBER_ME_PASSWORD, password);
        editor.commit();
    }

    public static void clearRememberMeCredentials(Context context) {
        init(context);
        editor.remove(KEY_REMEMBER_ME_USERNAME);
        editor.remove(KEY_REMEMBER_ME_PASSWORD);
        editor.commit();
    }

    public static String getSavedUsername(Context context) {
        init(context);
        return sharedPreferences.getString(KEY_REMEMBER_ME_USERNAME, null);
    }

    public static String getSavedPassword(Context context) {
        init(context);
        return sharedPreferences.getString(KEY_REMEMBER_ME_PASSWORD, null);
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

    public static String getEmployeeId(Context context) {
        init(context);
        return sharedPreferences.getString(KEY_EMPLOYEE_ID, null);
    }

    public static String getEmployeeName(Context context) {
        init(context);
        return sharedPreferences.getString(KEY_EMPLOYEE_NAME, null);
    }

    public static void clearSession(Context context) {
        init(context);
        editor.clear();
        editor.commit();
    }
}
