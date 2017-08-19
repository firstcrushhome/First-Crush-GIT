package co.firstcrush.firstcrush;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import static co.firstcrush.firstcrush.R.mipmap.icon;


public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private static boolean activityStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this).setNotificationOpenedHandler(new ExampleNotificationOpenedHandler()).init();
        if (   activityStarted
                && getIntent() != null
                && (getIntent().getFlags() & Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
            finish();
            return;
        }
        activityStarted = true;
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.web1);
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/534.36 (KHTML, like Gecko) Chrome/13.0.766.0 Safari/534.36");
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://www.firstcrush.co");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(icon);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ( webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    // This fires when a notification is opened by tapping on it or one is received while the app is running.
    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
            try {
                if (additionalData != null) {
                    if (additionalData.has("actionSelected"))
                        Log.d("OneSignalExample", "OneSignal notification button with id " + additionalData.getString("actionSelected") + " pressed");
                }
                Log.d("OneSignalExample", "Full additionalData:\n" + additionalData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
