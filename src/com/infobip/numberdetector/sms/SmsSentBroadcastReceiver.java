package com.infobip.numberdetector.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * Broadcast receiver used as argument when performing SMS message send.
 * SmsManager will provide status result which will be used to check whether message was sent or not.
 */
public class SmsSentBroadcastReceiver extends BroadcastReceiver {
	
	private Handler handler;
	
	public SmsSentBroadcastReceiver(Handler smsSentHandler) {
		handler = smsSentHandler;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
        Message message = handler.obtainMessage();
        message.arg1 = getResultCode();
		
		handler.sendMessage(message);
    }
}
