package network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import network.GoogleCalendarService;
import network.GoogleCalendarServiceSingleton;

public class FetchCalendarEventsTask extends AsyncTask<Void, Void, List<Event>> {
    private WeakReference<Context> contextRef;
    private DateTime startTime;
    private DateTime endTime;
    private GoogleCalendarServiceSingleton calendarServiceSingleton;
    private OnCalendarEventsFetchedListener listener;

    public FetchCalendarEventsTask(Context context, DateTime startTime, DateTime endTime,
                                   GoogleCalendarServiceSingleton calendarServiceSingleton,
                                   OnCalendarEventsFetchedListener listener) {
        this.contextRef = new WeakReference<>(context);
        this.startTime = startTime;
        this.endTime = endTime;
        this.calendarServiceSingleton = calendarServiceSingleton;
        this.listener = listener;
    }

    @Override
    protected List<Event> doInBackground(Void... voids) {
        Context context = contextRef.get();
        if (context == null) {
            return null;
        }

        try {
            GoogleCalendarService calendarService = calendarServiceSingleton.getCalendarService();

            if (calendarService == null) {
                // Handle the case where the calendar service is not initialized
                Log.d ("Calendar Service", "Calendar Service is not initialized");
                return null;
            }

            // Fetch Google Calendar events
            Events events = calendarService.getCalendarEvents(startTime, endTime);
            return events.getItems();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately in your app
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Event> events) {
        if (listener != null) {
            listener.onCalendarEventsFetched(events);
        }
    }

    public interface OnCalendarEventsFetchedListener {
        void onCalendarEventsFetched(List<Event> events);
    }
}
