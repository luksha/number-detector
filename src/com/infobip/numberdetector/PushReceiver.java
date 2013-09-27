package com.infobip.numberdetector;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.infobip.push.AbstractPushReceiver;
import com.infobip.push.PushNotification;

public class PushReceiver extends AbstractPushReceiver {

    @Override
    public void onRegistered(Context context) {
        Toast.makeText(context, "Successfully registered.", Toast.LENGTH_SHORT).show();
        //TODO subscribe on server
        
    }

    @Override
    protected void onRegistrationRefreshed(Context context) {
        Toast.makeText(context, "Registration is refreshed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNotificationReceived(PushNotification notification, Context context) {
    	Log.d("PushReceiver","Received notification: " + notification.getMessage());
        Toast.makeText(context, "Received notification: " + notification.getMessage(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msisdn", notification.getMessage());
        context.startActivity(intent);
    }

    @Override
    protected void onNotificationOpened(PushNotification notification, Context context) {
        Toast.makeText(context, "Notification opened.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUnregistered(Context context) {
        Toast.makeText(context, "Successfully unregistered.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int reason, Context context) {
        Toast.makeText(context, "Error occurred.", Toast.LENGTH_SHORT).show();
    }
}
