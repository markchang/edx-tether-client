package org.edx.TetherClient;

import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.MalformedInputException;

import android.webkit.CookieManager;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String TAG = "Tether";

    public static final String host_url = "http://192.168.1.12:8000";
    public static final String browser_login_url = host_url + "/login";
    public static final String api_login_url = host_url + "/api/user/login/";
    public static final String poll_url = host_url + "/api/mobile/1234/poll/?token=123456";
    public static final String jump_to_url = host_url + "/courses/ChangX/Mobile100/Mobile_Tether/jump_to/";

    public String AuthToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // open browser to log in to edX and save the session cookie
    public void doBrowserLogin(View view) {
        Intent browser_intent = new Intent(Intent.ACTION_VIEW);
        browser_intent.setData(Uri.parse(browser_login_url));
        startActivity(browser_intent);
    }

    // open browser to log in to edX and save the session cookie
    public void doApiLogin(View view) {
        JSONObject jsonParams = new JSONObject();
        JSONObject jsonUrl = new JSONObject();
        try {
            // TODO: fill in valid email and address here
            // TODO: make a login page to capture this :)
            jsonParams.put("email", null);
            jsonParams.put("password", null);

            jsonUrl.put("url", api_login_url);

            new LoginTask().execute(jsonUrl, jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // hit the poll api to get any work elements off the queue
    public void doPoll(View view) {
        new PollTask().execute(poll_url);
    }

    // debug
    public void doDebug(View view) {

    }

    // open a specific URL
    public void openUrl(String url) {
        Intent browser_intent = new Intent(Intent.ACTION_VIEW);
        browser_intent.setData(Uri.parse(url));
        startActivity(browser_intent);
    }

    // parse a payload
    private void parseTetherPayload(String stringPayload) {
        try {
            JSONObject jsonPayload = new JSONObject(stringPayload);
            String moduleLocation = jsonPayload.getString("payload");
            String targetUrl = host_url + moduleLocation + "?token=" + AuthToken;
            Log.v(TAG, "i4x url: " + targetUrl);
            openUrl(targetUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // stash the login token
    private void stashLoginToken(String stringLoginPayload) {
        try {
            JSONObject jsonPayload = new JSONObject(stringLoginPayload);
            Boolean success = jsonPayload.getBoolean("success");
            if( success ) {
                AuthToken = jsonPayload.getString("token");
                MainApplication myApp = ( (MainApplication)getApplicationContext());
                myApp.setState(AuthToken);
                Toast.makeText(this, "Logged in!",  Toast.LENGTH_SHORT).show();
                Log.v(TAG, "Auth token: " + AuthToken);
            } else {
                Toast.makeText(this, "Error logging in",  Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing server result",  Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private class LoginTask extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject jsonUrl = params[0];
            JSONObject jsonParams = params[1];

            try {
                // Create the POST object and add the parameters
                String url = jsonUrl.getString("url");

                HttpPost httpPost = new HttpPost(url);
                StringEntity entity = new StringEntity(jsonParams.toString(), HTTP.UTF_8);
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(httpPost);

                if (response != null) {
                    int status = response.getStatusLine().getStatusCode();
                    if( status == HttpStatus.SC_OK ) {
                        try {
                            String postResult = EntityUtils.toString(response.getEntity());
                            Log.v(TAG, "POST result: " + postResult);
                            return postResult;
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }
                else {
                    Log.d(TAG, "HttpResponse was null");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String loginResult) {
            //Do something with result
            if (loginResult != null) {
                stashLoginToken(loginResult);
            }
            else {
                Log.d(TAG, "Login response was null");
            }
        }
    }

    private class RegisterTask extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject jsonUrl = params[0];
            JSONObject jsonParams = params[1];

            try {
                // Create the POST object and add the parameters
                String url = jsonUrl.getString("url");

                HttpPost httpPost = new HttpPost(url);
                StringEntity entity = new StringEntity(jsonParams.toString(), HTTP.UTF_8);
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(httpPost);

                if (response != null) {
                    int status = response.getStatusLine().getStatusCode();
                    if( status == HttpStatus.SC_OK ) {
                        try {
                            String postResult = EntityUtils.toString(response.getEntity());
                            Log.v(TAG, "POST result: " + postResult);
                            return postResult;
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }
                else {
                    Log.d(TAG, "HttpResponse was null");
                    return null;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String registerResult) {
            //Do something with result
            if (registerResult != null) {
                Log.v(TAG, "UA registration ok");
            }
            else {
                Log.d(TAG, "UA registration failed");
            }
        }
    }


    private class PollTask extends AsyncTask<String, Void, HttpResponse> {
        @Override
        protected HttpResponse doInBackground(String... params) {
            String link = params[0];
            HttpGet request = new HttpGet(link);
            AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
            try {
                return client.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                client.close();
            }
        }

        @Override
        protected void onPostExecute(HttpResponse result) {
            //Do something with result
            if (result != null) {
                int status = result.getStatusLine().getStatusCode();

                if( status == HttpStatus.SC_NOT_FOUND ) {
                    Log.v(TAG, "No poll results");
                    return;
                } else if ( status == HttpStatus.SC_OK ) {
                    try {
                        String poll_result = EntityUtils.toString(result.getEntity());
                        Log.v(TAG, "Poll result: " + poll_result);
                        parseTetherPayload(poll_result);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                result.getEntity();
            }
            else {
                Log.d(TAG, "HttpResponse was null");
            }
        }
    }
}
