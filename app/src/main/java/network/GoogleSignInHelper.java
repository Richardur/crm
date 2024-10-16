package network;

import android.content.res.Resources;

import com.aiva.aivacrm.R;

public class GoogleSignInHelper {
    public String getClientId(Resources resources) {
        return  resources.getString(R.string.google_client_id);
    }
}
