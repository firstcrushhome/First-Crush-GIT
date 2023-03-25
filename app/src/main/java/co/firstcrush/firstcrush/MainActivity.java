package co.firstcrush.firstcrush;

import static android.content.ContentValues.TAG;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.app.NotificationCompat;

import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.onesignal.OSNotificationAction;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import static co.firstcrush.firstcrush.R.mipmap.icon;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private static boolean activityStarted;
    private ComponentName mRemoteControlResponder;
    private AudioManager am;
    MediaButtonIntentReceiver mMediaButtonReceiver = new MediaButtonIntentReceiver();
    IntentFilter mediaFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
    Callback callback;
    MediaPlayer mPlayer;
    MediaController mediaC;
    androidx.fragment.app.Fragment selectedFragment = null;
    private NotificationManager mNotificationManager;


    private final BottomNavigationView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

                if(item.getItemId() != navigation.getSelectedItemId()) {
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
                } else return false;

            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        //Bluetooth Device Connectivity
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        //Initiate Media & Audio Session
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mRemoteControlResponder = new ComponentName(getPackageName(),
                MediaButtonIntentReceiver.class.getName());
        mediaFilter.setPriority(2139999999);
        registerReceiver(mMediaButtonReceiver, mediaFilter);
        am.setMode(AudioManager.MODE_NORMAL);


        MediaSession session = new MediaSession(getApplicationContext(), "FirstCrush");
        session.setCallback(callback);
       // MediaController controller = session.getController();

      //  MediaMetadata controllerMetadata= controller.getMetadata();
//        MediaDescription description = controllerMetadata.getDescription();

        PlaybackState state = new PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY |
                        PlaybackState.ACTION_PAUSE |
                        PlaybackState.ACTION_SKIP_TO_NEXT|
                        PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackState.STATE_PLAYING, 0, 0, SystemClock.elapsedRealtime())
                .build();
        session.setPlaybackState(state);
        session.setActive(true);
        MediaSession.Token token = session.getSessionToken();
        MediaMetadata.Builder mediaMetaData_builder = new MediaMetadata.Builder();
        mediaMetaData_builder.putLong(MediaMetadata.METADATA_KEY_DURATION, 10 );
        session.setMetadata(mediaMetaData_builder.build());
       /* PlaybackState state = new PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PAUSE | PlaybackState.ACTION_PLAY_PAUSE|
                        PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
             .setState(PlaybackState.STATE_PLAYING, 0, 0, 0)
              .build();*/


        //Now Playing Notification
        //MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "First Crush");
        /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,"First Crush");
        mBuilder.setSmallIcon(icon);
        mBuilder.setContentTitle("description.getTitle()");
        mBuilder.setContentText("description.getDescription()");
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), largeicon));
        mBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(MediaSessionCompat.Token.fromToken(session.getSessionToken())));
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        Notification build = mBuilder.build();*/

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "FirstCrush");
        Intent ii = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, ii, PendingIntent.FLAG_IMMUTABLE);
        Notification.MediaStyle style = new Notification.MediaStyle();
        style.setMediaSession(session.getSessionToken());


        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.setBigContentTitle("Today's Bible Verse");
        bigText.setSummaryText("Text in detail");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(icon);
        mBuilder.setContentTitle("Your Title");
        mBuilder.setContentText("Your text");
        mBuilder.setStyle(bigText);
        /*mBuilder.setStyle(new NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0,1)
                .setMediaSession(session.getSessionToken()));
        session.setMetadata
                (new MediaMetadataCompat.Builder()
                        .putString(MediaMetadata.METADATA_KEY_TITLE, "TEST TITLE")
                        .putString(MediaMetadata.METADATA_KEY_ARTIST, "TEST ARTIST")
                        .build()
                );*/

        //Notification.Action actionPrev = new Notification.Action.Builder(Icon.createWithResource(this, android.R.drawable.ic_media_previous), "Previous", pr).build();
       // Notification.Action actionNext = new Notification.Action.Builder(Icon.createWithResource(this, android.R.drawable.ic_media_next), "Next", getPendingIntentNext()).build();
        //Notification.Action actionPlay = new Notification.Action.Builder(Icon.createWithResource(this, drw_play), "Play", ()).build();
        Intent intentPrev = new Intent("ACTION_PREV");
        Intent intentPlayPause = new Intent("ACTION_PLAY");
        Intent intentNext = new Intent("ACTION_NEXT)");
        PendingIntent pendingPrev = PendingIntent.getActivity(this, 0, intentPrev, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingPlayPause = PendingIntent.getActivity(this, 0, intentPlayPause, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingNext = PendingIntent.getActivity(this, 0, intentNext, PendingIntent.FLAG_IMMUTABLE);

        androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(getApplicationContext(), "FirstCrush")
                //NotificationCompat.Builder builder = new NotificationCompat.Builder( this, NOTIFICATION_CHANNEL_ID );
                .setSmallIcon(icon)
                .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle( "First Crush" )
                .setContentText("Media Playing")
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
               // .addAction(actionPrev).addAction(actionPlay).addAction(actionNext)
                .setOngoing(true).setBadgeIconType(androidx.core.app.NotificationCompat.BADGE_ICON_NONE)
                .setOnlyAlertOnce(true)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1)
                        .setMediaSession(MediaSessionCompat.Token.fromToken(session.getSessionToken())))
                .addAction(android.R.drawable.ic_media_previous, "Prev", pendingPrev)
                .addAction(android.R.drawable.ic_media_play, "Play", pendingPlayPause)
                .addAction(android.R.drawable.ic_media_next, "Next", pendingNext);
                //.setStyle(bigText)


        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        String channelId = "FirstCrush";
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "FirstCrush`",
                NotificationManager.IMPORTANCE_HIGH);
        mNotificationManager.createNotificationChannel(channel);
        mBuilder.setChannelId(channelId);

        mNotificationManager.notify(1, builder.build());




        //Media Session Callback Implementation

       callback = new Callback() {

            @Override
            public boolean onMediaButtonEvent(final Intent mediaButtonEvent) {
               // Toast.makeText(getApplicationContext(), "Inside Broadcast", Toast.LENGTH_SHORT).show();
                final String intentAction = mediaButtonEvent.getAction();
                if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                    final KeyEvent event = mediaButtonEvent.getParcelableExtra(
                            Intent.EXTRA_KEY_EVENT);
                    if (event == null) {
                        return super.onMediaButtonEvent(mediaButtonEvent);
                    }
                    final int keycode = event.getKeyCode();
                    final int action = event.getAction();
                    if (event.getRepeatCount() == 0 && action == KeyEvent.ACTION_DOWN) {
                        switch (keycode) {
                            // Do what you want in here
                            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                                Toast.makeText(getApplicationContext(), "Play Pause called", Toast.LENGTH_SHORT).show();

                                break;
                            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                                //Toast.makeText(getApplicationContext(), "Pause called", Toast.LENGTH_SHORT).show();
                                onPause();
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PLAY:
                                Toast.makeText(getApplicationContext(), "Play called", Toast.LENGTH_SHORT).show();
                                onPlay();
                                break;
                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                Toast.makeText(getApplicationContext(), "Next called", Toast.LENGTH_SHORT).show();
                                onSkipToNext();
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                Toast.makeText(getApplicationContext(), "Previous called", Toast.LENGTH_SHORT).show();
                                onSkipToPrevious();
                                break;
                        }
                        startService(new Intent(getApplicationContext(), MediaButtonIntentReceiver.class));
                        return true;
                    }
                }
                return false;

            }

            //Override Methods Media Session Callback
            @Override
            public void onSkipToNext() {
                Log.e(TAG, "onSkipToNext called (media button pressed)");
                Toast.makeText(getApplicationContext(), "onSkipToNext called", Toast.LENGTH_SHORT).show();
                Log.d("MyLog", "STATE_SKIPPING_TO_NEXT");
           // Handle this button press.
                //MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().skipToNext();
                super.onSkipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                Log.e(TAG, "onSkipToPrevious called (media button pressed)");
                Toast.makeText(getApplicationContext(), "onSkipToPrevious called", Toast.LENGTH_SHORT).show();
                // Handle this button press.
                //MediaControllerCompat.getMediaController((Activity) getApplicationContext()).getTransportControls().skipToPrevious();
                //mPlayer = new MediaPlayer();
               // mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                super.onSkipToPrevious();
            }

            @Override
            public void onPause() {
                //Log.e(TAG, "onPause called (media button pressed)");
                //Toast.makeText(getApplicationContext(), "onPause called", Toast.LENGTH_SHORT).show();
                 // Pause the player.
                PlaybackState state = new PlaybackState.Builder()
                        .setActions(PlaybackState.ACTION_PLAY)
                        .setState(PlaybackState.STATE_PAUSED, 0, 0, 0)
                        .build();
                session.setPlaybackState(state);
                ((AudioManager)getSystemService(
                        Context.AUDIO_SERVICE)).requestAudioFocus(
                        focusChange -> {
                        }, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);


                session.setCallback(callback);

                super.onPause();

                }

            @Override
            public void onPlay() {
               // Log.e(TAG, "onPlay called ");
                //Toast.makeText(getApplicationContext(), "onPlay called", Toast.LENGTH_SHORT).show();
                PlaybackState state = new PlaybackState.Builder()
                        .setActions(PlaybackState.ACTION_PAUSE)
                        .setState(PlaybackState.STATE_PLAYING, 0, 0, 0)
                        .build();
                session.setPlaybackState(state);
             am.setMode(AudioManager.MODE_NORMAL);
                //callback.onPlay();
                session.setCallback(callback);
                super.onPlay();

            }

            @Override
            public void onStop() {
                Log.e(TAG, "onStop called (media button pressed)");
                // Stop and/or reset the player.
                super.onStop();
            }


        };

        mediaC = session.getController();
        session.setPlaybackState(state);
        session.setCallback(callback);
        session.setActive(true);


//The BroadcastReceiver that listens for bluetooth broadcasts
        final BroadcastReceiver BTReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Toast.makeText( getApplicationContext(), "Inside BT Broadcast", Toast.LENGTH_SHORT).show();
                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    //Do something if connected
                    Toast.makeText(getApplicationContext(), "BT Connected", Toast.LENGTH_SHORT).show();
                }
                else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    //Do something if disconnected
                    Toast.makeText(getApplicationContext(), "BT Disconnected", Toast.LENGTH_SHORT).show();
                }
                //else if...
            }
        };
        this.registerReceiver(BTReceiver, filter);



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
        int height= 480;

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

    @Override
    public void onResume() {
        super.onResume();
        am.registerMediaButtonEventReceiver(
                mRemoteControlResponder);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        am.unregisterMediaButtonEventReceiver(
                mRemoteControlResponder);
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
