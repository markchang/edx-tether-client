package org.edx.TetherClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by mchang on 6/7/13.
 */
public class IntentReceiver extends BroadcastReceiver {

    private static final String logTag = "TetherReceiver";
    public static final String host_url = "http://192.168.1.12:8000";


    public static String APID_UPDATED_ACTION_SUFFIX = ".apid.updated";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(logTag, "Received intent: " + intent.toString());
        String action = intent.getAction();

        if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {

            int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);

            Log.i(logTag, "Received push notification. Alert: "
                    + intent.getStringExtra(PushManager.EXTRA_ALERT)
                    + " [NotificationID="+id+"]");

            logPushExtras(intent);

        } else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {

            Log.i(logTag, "User clicked notification. Message: " + intent.getStringExtra(PushManager.EXTRA_ALERT));

            logPushExtras(intent);
            String AuthToken = ((MainApplication)UAirship.shared().getApplicationContext()).getState();
            String url = host_url + getPayloadUrl(intent) + "?token=" + AuthToken;

            if( url != null )  {
                Intent browser_intent = new Intent(Intent.ACTION_VIEW);
                browser_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                browser_intent.setData(Uri.parse(url));
                UAirship.shared().getApplicationContext().startActivity(browser_intent);
            }

        } else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
            Log.i(logTag, "Registration complete. APID:" + intent.getStringExtra(PushManager.EXTRA_APID)
                    + ". Valid: " + intent.getBooleanExtra(PushManager.EXTRA_REGISTRATION_VALID, false));

            // Notify any app-specific listeners
            Intent launch = new Intent(UAirship.getPackageName() + APID_UPDATED_ACTION_SUFFIX);
            UAirship.shared().getApplicationContext().sendBroadcast(launch);

        } else if (action.equals(PushManager.ACTION_GCM_DELETED_MESSAGES)) {
            Log.i(logTag, "The GCM service deleted "+intent.getStringExtra(PushManager.EXTRA_GCM_TOTAL_DELETED)+" messages.");
        }

    }

    /**
     * Log the values sent in the payload's "extra" dictionary.
     *
     * @param intent A PushManager.ACTION_NOTIFICATION_OPENED or ACTION_PUSH_RECEIVED intent.
     */
    private void logPushExtras(Intent intent) {
        Set<String> keys = intent.getExtras().keySet();
        for (String key : keys) {

            //ignore standard C2DM extra keys
            List<String> ignoredKeys = (List<String>) Arrays.asList(
                    "collapse_key",//c2dm collapse key
                    "from",//c2dm sender
                    PushManager.EXTRA_NOTIFICATION_ID,//int id of generated notification (ACTION_PUSH_RECEIVED only)
                    PushManager.EXTRA_PUSH_ID,//internal UA push id
                    PushManager.EXTRA_ALERT);//ignore alert
            if (ignoredKeys.contains(key)) {
                continue;
            }
            Log.i(logTag, "Push Notification Extra: ["+key+" : " + intent.getStringExtra(key) + "]");
        }
    }

    private String getPayloadUrl(Intent intent) {
        Set<String> keys = intent.getExtras().keySet();
        for (String key : keys) {

            if(key.equals("payload")) {
                return intent.getStringExtra(key);
            }
        }

        return null;
    }
}
