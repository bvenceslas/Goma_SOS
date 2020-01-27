package com.android.brain.sosfind.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.android.brain.sosfind.Views.discussions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.android.brain.sosfind.*;

/**
 * Created by Brain on 13/04/2019.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        String titlesms = remoteMessage.getNotification().getTitle();
        if (titlesms.equals("cancel_booking")) {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyFirebaseMessaging.this, "" + remoteMessage.getNotification()
                            .getBody(), Toast.LENGTH_LONG).show();
                }
            });

        } else if (titlesms.equals("Arrived")) {
            showArriveNotification(remoteMessage.getNotification().getBody());
        } else if (titlesms.equals("prix")) {

            String bodymsg = remoteMessage.getNotification().getBody();
            String[] separated = bodymsg.split(":::");
            Intent intent = new Intent(this, discussions.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("message", separated[0]);
            intent.putExtra("destinateur", separated[1]);
            intent.putExtra("signal", "R");
            startActivity(intent);

        } else {

            String titlecmd = remoteMessage.getNotification().getTitle();
            String[] separated = titlecmd.split(",");
            LatLng customer_Location = new Gson()
                    .fromJson(remoteMessage.getNotification().getBody(), LatLng.class);
   /*         Intent intent = new Intent(this, activity_CustomerCall.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
            Intent intent = new Intent(this, map_chauffeur.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("lat", customer_Location.latitude);
            intent.putExtra("longs", customer_Location.longitude);
            intent.putExtra("customer", separated[0]);
            intent.putExtra("idcmd", separated[1]);
            intent.putExtra("idpassager", separated[2]);
            startActivity(intent);
        }
    }

    private void showArriveNotification(String body) {
        // Api 25 and Below
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(),
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Arrivé du Chauffeur")
                .setContentText(body)
                .setContentIntent(contentIntent)
                .setSound(Uri.parse("android.resource://"
                        + this.getPackageName() + "/" + R.raw.clakson))
        ;
        NotificationManager manager = (NotificationManager) getBaseContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }
}
