package com.ayaenshasy.bayan;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ayaenshasy.bayan.utils.AppPreferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String TAG = "MyFirebaseMessagingService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e("onNewToken", token);
        AppPreferences sharedPreferences = new AppPreferences(getApplicationContext());

//        if (UtilMethods.isLogin(getApplicationContext())) {
//            sendDeviceToken(token);
//        }
        sharedPreferences.writeString(AppPreferences.DEVICE_TOKEN, token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message_data_payload: " + remoteMessage.getData());
            Map<String, String> params = remoteMessage.getData();

            JSONObject object = new JSONObject(params);
            Log.e("JSON OBJECT", object.toString());
            try {
                if (object.has("post_id")) {
                    int postId = object.getInt("post_id");
                    if (remoteMessage.getNotification() != null)
                        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), postId + "", null);
                } else if (object.has("user_id")) {
                    int userId = object.getInt("user_id");
                    if (remoteMessage.getNotification() != null)
                        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), null, userId + "");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    //This method is only generating push notification
    private void sendNotification(String messageTitle, String messageBody, String postId, String userId) {

        PendingIntent contentIntent = null;
        Intent intent = null;

        intent = new Intent(this, SplashActivity.class);
        intent.putExtra("post_id", postId);
        intent.putExtra("user_id", userId);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        contentIntent = PendingIntent.getActivity(this, 0 /* request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CharSequence tickerText = messageBody;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "M_CH_ID");

        notificationBuilder.setAutoCancel(true)
                .setSmallIcon(R.drawable.logo)
                .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(tickerText));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            String channelId = getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(channelId, messageTitle, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(messageBody);
            channel.enableVibration(true);
            channel.setSound(sound, audioAttributes);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
        }

        notificationManager.notify(0, notificationBuilder.build());
//        count++;
    }

//    private void sendDeviceToken(String deviceToken) {
//        AppSharedPreferences sharedPreferences = new AppSharedPreferences(getApplicationContext());
//
//        ServiceApi serviceApi = Creator.getClient().create(ServiceApi.class);
//        String token = sharedPreferences.readString(AppSharedPreferences.AUTHENTICATION);
//
//
//        Call<MessageResponse> call = serviceApi.sendDeviceToken(deviceToken, "Bearer " + token);
//        call.enqueue(new Callback<MessageResponse>() {
//            @Override
//            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
//                Log.d("response1 code", response.code() + "");
//                if (response.isSuccessful()) {
//                    Log.d("SuccessSavedNewToken", new Gson().toJson(response.body()));
//
//
//                } else {
//                    String errorMessage = parseError(response);
//                    Log.e("errorMessage", errorMessage + "");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MessageResponse> call, Throwable t) {
//                Log.d("onFailure2", t.getMessage() + "");
//                call.cancel();
//            }
//        });
//    }
}





