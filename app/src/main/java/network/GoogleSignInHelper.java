package network;

import android.content.res.Resources;
import android.util.Log;

import com.aiva.aivacrm.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Scanner;

public class GoogleSignInHelper {
    private static final String TAG = "GoogleSignInHelper";

    public String getClientId(Resources resources) {
        return resources.getString(R.string.google_client_id);
    }
}
