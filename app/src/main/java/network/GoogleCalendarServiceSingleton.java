package network;

import android.content.Context;

public class GoogleCalendarServiceSingleton {
    private static GoogleCalendarServiceSingleton instance;
    private static Context context;
    private static GoogleCalendarService calendarService;

    public GoogleCalendarServiceSingleton(Context context) {
        // Do not initialize the context here
        calendarService = new GoogleCalendarService(context);
    }

    public static GoogleCalendarServiceSingleton getInstance(Context context) {
        if (instance == null) {
            synchronized (GoogleCalendarServiceSingleton.class) {
                if (instance == null) {
                    instance = new GoogleCalendarServiceSingleton(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public static GoogleCalendarService getCalendarService() {
        return calendarService;
    }

    public static void setApiKey(String apiKey) {
        if (calendarService != null) {
            calendarService.setApiKey(apiKey);

        }
    }
    public static Boolean getIsInitialized() {
        if (calendarService != null) {
            return calendarService.getIsInitialized();
        }
        return false;
    }
}
