package network;

import android.content.Context;

public class GoogleCalendarServiceSingleton {
    private static GoogleCalendarServiceSingleton instance;
    private static GoogleCalendarService calendarService;

    public GoogleCalendarServiceSingleton(Context context) {
        calendarService = new GoogleCalendarService(context);
    }

    public static synchronized GoogleCalendarServiceSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new GoogleCalendarServiceSingleton(context);
        }
        return instance;
    }

    public GoogleCalendarService getCalendarService() {
        return calendarService;
    }

    public void setApiKey(String apiKey) {
        if (calendarService != null) {
            calendarService.setApiKey(apiKey);
        }
    }

    public Boolean getIsInitialized() {
        return calendarService != null && calendarService.getIsInitialized();
    }
}
