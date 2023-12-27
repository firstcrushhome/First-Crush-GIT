package co.firstcrush.firstcrush;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.media.session.MediaButtonReceiver;

public class MediaButtonIntentReceiver extends BroadcastReceiver {
    public MediaButtonIntentReceiver(){
        super();
        Log.i("mylog", "init");
        //abortBroadcast();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("mylog", "receive");
        abortBroadcast();
        String intentAction = intent.getAction();
        Log.e("Intent Receiver","Inside Intent Receiver");
        if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            Toast.makeText(context, "BUTTON PRESSED1 !", Toast.LENGTH_SHORT).show();
            return;
        }
        KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event == null) {
            Toast.makeText(context, "BUTTON PRESSED2!", Toast.LENGTH_SHORT).show();
            return;
        }
        int action = event.getAction();
        if (action == KeyEvent.ACTION_DOWN) {
            // do something
            Toast.makeText(context, "BUTTON PRESSED3!", Toast.LENGTH_SHORT).show();
        }
        if(event.getAction() == KeyEvent.ACTION_UP) {
            int keycode = event.getKeyCode();
            if(keycode == KeyEvent.KEYCODE_MEDIA_NEXT) {
                Log.e("TestApp", "Next Pressed");
            } else if(keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                Log.e("TestApp", "Previous pressed");
            } else if(keycode == KeyEvent.KEYCODE_HEADSETHOOK) {
                Log.e("TestApp", "Head Set Hook pressed");
            }
        }
        abortBroadcast();
    }
}
