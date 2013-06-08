package org.edx.TetherClient;

import android.app.Application;
import android.util.Log;

import com.urbanairship.UAirship;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.CustomPushNotificationBuilder;
import com.urbanairship.push.PushManager;


/**
 * Created by mchang on 6/7/13.
 */
public class MainApplication extends Application {

    public String AuthToken;

    public String getState() {
        return AuthToken;
    }
    public void setState(String s) {
        AuthToken = s;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);

        // TODO: put in your real keys
        options.inProduction = false;
        options.developmentAppKey = "";
        options.developmentAppSecret = "";
        options.gcmSender = "";

        Logger.logLevel = Log.VERBOSE;

        UAirship.takeOff(this, options);
        PushManager.enablePush();

        String apid = PushManager.shared().getAPID();
        Logger.info("My Application onCreate - App APID: " + apid);

        // receivers
        PushManager.shared().setIntentReceiver(IntentReceiver.class);
    }
}
