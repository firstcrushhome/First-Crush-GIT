package co.firstcrush.firstcrush;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class MediaButtonIntentReceiver extends BroadcastReceiver {
    public MediaButtonIntentReceiver(){
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
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
        abortBroadcast();
    }
}
