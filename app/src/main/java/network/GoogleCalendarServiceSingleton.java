package network;

import android.content.Context;

public class GoogleCalendarServiceSingleton {
    private static GoogleCalendarServiceSingleton instance;
    private final GoogleCalendarService calendarService;

    private GoogleCalendarServiceSingleton(Context context) {
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
}
