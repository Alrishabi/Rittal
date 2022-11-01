package sd.rittal.app.notificationModule;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import sd.rittal.app.R;
import sd.rittal.app.activities.HomeActivity;

public class RittalFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = RittalFirebaseMessagingService.class.getSimpleName().toUpperCase();
    private NotificationUtils notificationUtils;
    String lang = Locale.getDefault().getLanguage();
//fJs1msZjSP8:APA91bF9P2gzg_SoSrnxf3LCLshdzS6s43TfcWGLfQCX6P_7dJvqq-ws6kn5bkhaTnkskjdXrZObIEiJF2mgR8hpqpQdFvEld3RPODhdoC4DVOCpryDAg3xbLihGkUYny1z6_KvegUna
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("TAG", "From: " + remoteMessage.getFrom());
        Intent intent;
        if (remoteMessage.getNotification() != null) {
            intent = new Intent(this,HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                    PendingIntent.FLAG_ONE_SHOT);

            // Initiating Notification
            NotificationCompat.Builder notificatinBuilder = new NotificationCompat.Builder(this);
            notificatinBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
            Log.d("BODY",remoteMessage.getNotification().getBody());
            //
            JSONObject object = null;
            String type = "";
            String Service = "";
            String text = "";
            String en_text = "";
            String title = "";
            String en_title = "";
            String url = "";
            try {
                object = new JSONObject(remoteMessage.getNotification().getBody());

//                Service= object.getString("Service");
//                text= object.getString("text");
//                en_text= object.getString("en_text");
//                title= object.getString("title");
//                en_title= object.getString("en_title");
//                url= object.getString("url");
                type = "CUSTOM";

            } catch (JSONException e) {
                e.printStackTrace();
                type = "STANDER";
            }
            notificatinBuilder.setSmallIcon(R.mipmap.ic_launcher);

            Log.d("NOTOFICATION TYPE", type);

            if(type.equals("CUSTOM")){
//                if(lang.equals("ar")){
//                    notificatinBuilder.setContentText(text);
//                    notificatinBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
//
//                }else{
//                    notificatinBuilder.setContentText(en_text);
//                    notificatinBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(en_text));
//                }
//                intent = new Intent(this,HomeActivity.class);
//                intent.putExtra("Service",Service);
//                intent.putExtra("text",text);
//                intent.putExtra("en_text",en_text);
//                intent.putExtra("title",title);
//                intent.putExtra("en_title",en_title);
//                intent.putExtra("url",url);
            }else {

                    notificatinBuilder.setContentText(remoteMessage.getNotification().getBody());
                    notificatinBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));


            }
            notificatinBuilder.setContentIntent(pendingIntent);
            notificatinBuilder.setAutoCancel(true);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificatinBuilder.setSound(alarmSound);


            //Displaying Notification
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificatinBuilder.setChannelId("sd.rittal.consumer");
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(
                            "sd.rittal.consumer",
                            getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_DEFAULT
                    );
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(channel);
                    }
                }

                notificationManager.notify(0,notificatinBuilder.build());
        }

//        if (remoteMessage.getData().size() > 0) {
//            JSONObject obj;
//            JSONObject data = null;
//            try {
//                obj = new JSONObject(remoteMessage.getData().toString());
//                data = obj.getJSONObject("data");
//                Log.e("TAG", "Recive: " + data.toString());
//                //String process = data.getString("process");
//                JSONObject payload = data.getJSONObject("payload");
//                String order_id = payload.getString("order_id");
//                Intent intent;
//                intent = new Intent(this,MainActivity.class);
//                int key = 0;
//                if(payload.getString("process").equals("new_offer")){
//                    intent = new Intent(this,OffersDetailsActivity.class);
//                    intent.putExtra("process",payload.getString("process"));
//                    intent.putExtra("offer_id",order_id);
//                    key = Integer.parseInt(order_id);
//
//                }
//
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//
//                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
//                        PendingIntent.FLAG_ONE_SHOT);
//
//                //Initiating Notification
//                NotificationCompat.Builder notificatinBuilder = new NotificationCompat.Builder(this);
//                notificatinBuilder.setContentTitle(data.getString("title"));
//                notificatinBuilder.setContentText(data.getString("message"));
//                notificatinBuilder.setSmallIcon(R.mipmap.ic_launcher);
//                notificatinBuilder.setContentIntent(pendingIntent);
//                notificatinBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(data.getString("message")));
//                notificatinBuilder.setAutoCancel(true);
//                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                notificatinBuilder.setSound(alarmSound);
//
//
//                //Displaying Notification
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    notificatinBuilder.setChannelId("net.dayir.app");
//                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    NotificationChannel channel = new NotificationChannel(
//                            "net.dayir.app",
//                            getString(R.string.app_name),
//                            NotificationManager.IMPORTANCE_DEFAULT
//                    );
//                    if (notificationManager != null) {
//                        notificationManager.createNotificationChannel(channel);
//                    }
//                }
//
//                notificationManager.notify(key,notificatinBuilder.build());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }


    }


    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

}
