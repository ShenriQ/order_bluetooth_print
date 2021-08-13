package core.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.app.fooddeliverysystem.R;
import com.app.urantia.UserInterface.Activities.ChatDetailsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import core.Models.User;
import core.Utils.CoreConstants;


/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 * <p>
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 * <p>
 * <intent-filter>
 * <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null)
                Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            if (remoteMessage.getNotification().getBody() != null)
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            try {
                sendNotification(jsonObject);
                Log.d(TAG, "Message Data as Json: " + jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        User.Companion.saveToken(token);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param jsonObject FCM message body received.
     */
    private void sendNotification(JSONObject jsonObject) throws JSONException {
        String title = jsonObject.getString("title");
        String messageBody =  jsonObject.getString("body");
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());//new Intent(this, SplashActivity.class);

        try {
            String msg_action =  jsonObject.getString("action");
            String channelId =  jsonObject.getString("channelId");
            String memberType = jsonObject.getString("memberType");
            if(msg_action != null && msg_action.equals("chat")) {
                intent.putExtra("channel_id", channelId);
                intent.putExtra("senderType", memberType);
            }

            Log.d(TAG, msg_action + " " + channelId + " " + memberType);
        }
        catch (Exception e) {

        }

//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.app_name);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}