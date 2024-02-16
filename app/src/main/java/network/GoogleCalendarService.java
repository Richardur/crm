package network;

import android.content.Context;

import com.aiva.aivacrm.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Aiva CRM";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);

    public static Boolean getIsInitialized() {
        return isInitialized;
    }

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
        initializeService();
    }

    public void setApiKey(String apiKey) {
        // Set the apiKey in the calendarService when it becomes available
        userAccountName = apiKey;
    }

    private Calendar initializeCalendar() throws GeneralSecurityException, IOException {
        // Initialize the Google Calendar API with OAuth 2.0 credentials
        HttpTransport httpTransport = null;
        try{
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (
                GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        credential = GoogleAccountCredential.usingOAuth2(
                context, Collections.singleton(CalendarScopes.CALENDAR));


        calendarService = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        return calendarService;
    }






    private void initializeService() {


        try {
            // Initialize HTTP transport using GoogleNetHttpTransport
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // Create a Gson instance for JSON processing
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

            // Configure OkHttp with logging interceptor (optional)
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            OkHttpClient httpClient = httpClientBuilder.build();

            // Retrieve the access token from Google Sign-In
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context); // Replace 'context' with your actual context
            String accessToken = null;
            if (account != null) {
                accessToken = account.getIdToken();
                userAccountName = account.getEmail();// or account.getServerAuthCode() depending on your needs
            }

            // Now you can manually make HTTP requests to the Google Calendar API
            String calendarApiUrl = "https://www.googleapis.com/calendar/v3/calendars/primary/events";
            Request request = new Request.Builder()
                    .url(calendarApiUrl)
                    .header("Authorization", "Bearer " + accessToken) // Use the obtained access token
                    .build();

            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                isInitialized = true;
                if (responseBody != null) {
                    String json = responseBody.string();
                    // Process the JSON response using your Gson instance
                    // For example, you can deserialize it into a Java object
                    //YourEventClass event = gson.fromJson(json, YourEventClass.class);
                }
            } else {
                // Handle the error response
                String errorMessage = response.message();
                int errorCode = response.code();
                // Handle the error as needed
            }

            // ...
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public List<Event> getCalendarEvents() throws IOException {
        if (calendarService == null) {
            throw new IllegalStateException("Calendar service is not initialized.");
        }

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = calendarService.events().list("primary")
                .setMaxResults(10) // You can adjust the number of events to retrieve
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
            Events events = calendarService.events().list("primary")
                    .setMaxResults(10) // You can adjust the number of events to retrieve
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
}
