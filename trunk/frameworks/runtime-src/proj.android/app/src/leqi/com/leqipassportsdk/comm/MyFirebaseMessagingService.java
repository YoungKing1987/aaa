package leqi.com.leqipassportsdk.comm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
//import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import leqi.com.leqipassportsdk.leqipass;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.i(leqipass.Tag, "From:"+remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.i(leqipass.Tag, "Message data payload: " + remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.i(leqipass.Tag, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

    }


}
