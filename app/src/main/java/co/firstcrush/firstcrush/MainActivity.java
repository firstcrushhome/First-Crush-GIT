package co.firstcrush.firstcrush;

import static android.content.ContentValues.TAG;

import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.onesignal.OSNotificationAction;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;
import com.onesignal.OSNotificationOpenedResult;

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
    private MediaSession.Token mSessionToken;

    private MediaController mController;


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

    public MainActivity() {
        mSessionToken = null;
        mController = null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        MediaSession session = new MediaSession(this, "MusicService");
        session.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        PlaybackState state = new PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY)
                .setState(PlaybackState.STATE_STOPPED, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 0)
                .build();
        MediaSessionManager manager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);

        session.setActive(true);

        final BroadcastReceiver myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equalsIgnoreCase("android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED")) {
                    Log.d(TAG,"Bluetooth connect");
                }
                String intentAction = intent.getAction();
                if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                    return;
                }
                KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (event == null) {
                    return;
                }

                int action = event.getAction();
                if (event.getKeyCode() == 126 || event.getKeyCode() == 127) {
                    // do something
                    if (action == KeyEvent.ACTION_DOWN) {

                        Toast.makeText(context,"BUTTON PRESSED!", Toast.LENGTH_LONG).show();

                        if(event.isLongPress()==true)
                        {
                            Toast.makeText(context,"Finally long press worked!!", Toast.LENGTH_LONG).show();

                        }


                    }


                }
            }
        };

        OneSignal.initWithContext(this);
        OneSignal.setAppId("ea063994-c980-468b-8895-fcdd9dd93cf4");

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();
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
        navigation = findViewById(R.id.navigation);
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
        // ATTENTION: This was auto-generated to handle app links.

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width=854;
        int height = 480;
        Rational aspectRatio = new Rational(width, height);
        PictureInPictureParams.Builder pictureInPictureParamsBuilder;
        pictureInPictureParamsBuilder=new PictureInPictureParams.Builder();
        pictureInPictureParamsBuilder.setAspectRatio(aspectRatio);
        enterPictureInPictureMode(pictureInPictureParamsBuilder.build());
        //final Rect sourceRectHint = new Rect();
        //enterPictureInPictureMode(new PictureInPictureParams.Builder().setAspectRatio(aspectRatio).setSourceRectHint(sourceRectHint).setAutoEnterEnabled(true).build());

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                // return false;
                // Update based on @Rene comment below:
                return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                // return false;
                // Update based on @Rene comment below:
                return super.onKeyDown(keyCode, event);
        }
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
