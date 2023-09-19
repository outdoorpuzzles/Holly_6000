package cz.FCBcoders.holly6000;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
   public static MutableLiveData<Boolean> newNotification = new MutableLiveData<>(false);

   @Override
   public void onMessageReceived(@NonNull RemoteMessage message) {
      String messageTitle = "";
      String messageBody = "";

      //holly6000ViewModel = new ViewModelProvider(this).get(Holly6000ViewModel.class);
      newNotification.postValue(true);

      if (message.getData().size() > 0) {

         Log.d("Log Planet", "Obsah zprávy" + message.getData());

      }

      // Check if message contains a notification payload.
      if (message.getNotification() != null) {
         messageTitle = message.getNotification().getTitle();
         messageBody = message.getNotification().getBody();

         Log.d("Log Planet", "Message Notification Body: " + message.getNotification().getBody());
      }

      sendNotification(messageTitle, messageBody);
   }

   private void sendNotification(String messageTitle, String messageBody) {
      String channelId = "myAppChannel";

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         CharSequence name = "channel_name";
         String description = "channel_description";
         int importance = NotificationManager.IMPORTANCE_DEFAULT;
         NotificationChannel channel = new NotificationChannel(channelId, name, importance);
         channel.setDescription(description);
         channel.enableVibration(true);
         channel.enableLights(true);
         // Register the channel with the system; you can't change the importance
         // or other notification behaviors after this
         NotificationManager notificationManager = getSystemService(NotificationManager.class);
         notificationManager.createNotificationChannel(channel);
      }


      Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
      notificationBuilder.setSmallIcon(R.drawable.holly6000_app_icon)
              .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.holly6000_app_icon))
              .setContentTitle(messageTitle)
              .setContentText(messageBody)
              .setStyle(new NotificationCompat.BigTextStyle()
                      .bigText(messageBody))
              .setAutoCancel(true)
              .setSound(defaultSoundUri)
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
              .setCategory(NotificationCompat.CATEGORY_MESSAGE)
              .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

      NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(0, notificationBuilder.build());
   }

}
