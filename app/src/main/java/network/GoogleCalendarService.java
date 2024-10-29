package network;

import android.content.Context;

import com.aiva.aivacrm.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Aiva CRM";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    private static Boolean isInitialized = false;
    private final Context context;
    private String userAccountName;
    private GoogleAccountCredential credential;
    private Calendar calendarService;

    public GoogleCalendarService(Context context) {
        this.context = context;
        try {
            calendarService = initializeCalendar();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Boolean getIsInitialized() {
        return isInitialized;
    }

    private Calendar initializeCalendar() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        credential = GoogleAccountCredential.usingOAuth2(
                        context, Collections.singleton(CalendarScopes.CALENDAR))
                .setBackOff(new ExponentialBackOff());

        // Get the last signed-in Google account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            credential.setSelectedAccountName(account.getEmail());
            userAccountName = account.getEmail();  // Save the user account name for later use
        }

        // Build the Calendar service object
        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // Set API Key (this method can be removed if API key isn't required)
    public void setApiKey(String apiKey) {
        userAccountName = apiKey;
    }
    public com.google.api.services.calendar.Calendar getCalendarService() {
        return calendarService;
    }

    public List<Event> getCalendarEvents() throws IOException {
        if (calendarService == null) {
            throw new IllegalStateException("Calendar service is not initialized.");
        }

        DateTime now = new DateTime(System.currentTimeMillis());

        // Fetch events from the primary calendar
        Events events = calendarService.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }

    public Events getCalendarEvents(DateTime startTime, DateTime endTime) throws IOException {
        if (calendarService == null) {
            throw new IllegalStateException("Calendar service is not initialized.");
        }
        if (userAccountName != null && !userAccountName.isEmpty()) {
            credential.setSelectedAccountName(userAccountName);

            // Fetch events between startTime and endTime
            Events events = calendarService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(startTime)
                    .setTimeMax(endTime)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            return events;
        } else {
            return null;
        }
    }

    // Method to initialize Gson for parsing JSON data (though Google Calendar API already handles it)
    private Gson initializeGson() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    }
}
