package com.infobip.numberdetector.sms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

public class SmsMessageSender {
	
	private Context context;
	private SmsSentBroadcastReceiver receiver;
	
	public SmsMessageSender(Context context, SmsSentBroadcastReceiver receiver) {
		this.context = context;
		this.receiver = receiver;
	}
	
	//---sends an SMS message to another device---
    public void sendSMS(String phoneNumber, String message)
    {        
        String SENT = "NUMBER-DETECTOR-SMS-SENT";
 
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
 
        //when the SMS has been sent
        context.getApplicationContext().registerReceiver(receiver, new IntentFilter(SENT));      
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, null);        
    }
}
