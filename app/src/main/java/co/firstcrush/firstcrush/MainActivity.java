package co.firstcrush.firstcrush;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.onesignal.OSNotificationAction;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;
import com.onesignal.OSNotificationOpenedResult;

import static co.firstcrush.firstcrush.R.id.navigation_home;
import static co.firstcrush.firstcrush.R.mipmap.icon;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private BottomNavigationView navigation;
    private static boolean activityStarted;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private ProgressBar progressBar;
    View decorView;

    private String mCurrentTab;

    private final BottomNavigationView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                androidx.fragment.app.Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = HomeFragment.newInstance();
                        break;
                    case R.id.search:
                        selectedFragment = SearchFragment.newInstance();
                        break;
                    case R.id.navigation_radio:
                        selectedFragment = RadioFragment.newInstance();
                        break;
                    case R.id.navigation_profile:
                        selectedFragment = ProfileFragment.newInstance();
                        break;
                    case R.id.navigation_notifications:
                        selectedFragment = NotificationsFragment.newInstance();
                        break;
                }
                androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        OneSignal.initWithContext(this);
        OneSignal.setNotificationOpenedHandler(
                result -> {
                    String actionId = result.getAction().getActionId();
                    OSNotificationAction.ActionType type = result.getAction().getType(); // "ActionTaken" | "Opened"

                    String title = result.getNotification().getTitle();
                });
        if (activityStarted
                && getIntent() != null
                && (getIntent().getFlags() & Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
            finish();
            return;
        }
        activityStarted = true;
        setContentView(R.layout.activity_main);

        //Add Bottom Navigation View
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        //first fragment - one time only
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, HomeFragment.newInstance());
        transaction.commit();
        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(icon);
        //builder.setLargeIcon(Bitmap.createBitmap(largeicon));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This fires when a notification is opened by tapping on it or one is received while the app is running.
    private abstract class ExampleNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {
        public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
            try {
                if (additionalData != null) {
                    if (additionalData.has("actionSelected"))
                        Log.d("OneSignalExample", "OneSignal notification button with id " + additionalData.getString("actionSelected") + " pressed");
                    if (   activityStarted
                            && getIntent() != null
                            && (getIntent().getFlags() & Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
                        finish();
                        return;
                    }
                }
                Log.d("OneSignalExample", "Full additionalData:\n" + additionalData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
