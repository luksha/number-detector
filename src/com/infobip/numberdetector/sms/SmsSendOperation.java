package com.infobip.numberdetector.sms;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import com.infobip.numberdetector.OperationEventHandler;

import com.infobip.push.PushNotificationManager;

public class SmsSendOperation {

	SmsSentBroadcastReceiver smsSentBroadcastReceiver = null;

	public static final String TAG = "SmsSendOperation";
	public static final String KEYWORD = "dragan";
	public static final String SHORTCODE = "+385997894508";
	PushNotificationManager pushManager;
    OperationEventHandler eventHandler;
    Context mContext;
	
	public SmsSendOperation(OperationEventHandler eventHandler, Context context, PushNotificationManager pushManager) {
        this.eventHandler = eventHandler;
		this.mContext = context;
		this.pushManager = pushManager;
		smsSentBroadcastReceiver = new SmsSentBroadcastReceiver(smsSentHandler);
	}

	public void run() {
		sendSms();
	}



    Handler smsSentHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			//Handle SMS send fail message
			switch (msg.arg1){

	            case Activity.RESULT_OK:
	            	Log.d("SMS sent successfully.", "SmsSendOperation");
	                eventHandler.onComplete();
	                break;
	                
	            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	            	reportFailure("SmsManager.RESULT_ERROR_GENERIC_FAILURE");
	                break;
	                
	            case SmsManager.RESULT_ERROR_NO_SERVICE:
	            	reportFailure("SmsManager.RESULT_ERROR_NO_SERVICE");
	                break;
	            
	            case SmsManager.RESULT_ERROR_NULL_PDU:
	            	reportFailure("SmsManager.RESULT_ERROR_NULL_PDU");
	                break;
	                
	            case SmsManager.RESULT_ERROR_RADIO_OFF:
	            	reportFailure("SmsManager.RESULT_ERROR_RADIO_OFF");
	                break;

                default:
                    Log.d("SMS not sent.", "SmsSendOperation");
                    reportFailure("Unknown error");
                    break;
	        }

			cleanUp();

		}
	};
	
	private void reportFailure(String errorCode) {
		Log.d(TAG, "SmsManager returned error when sending SMS message:" + errorCode);
		eventHandler.onFail();
	}

    private void sendSms(){
        SmsMessageSender smsMessageSender = new SmsMessageSender(mContext, smsSentBroadcastReceiver);

        String shortCode = SHORTCODE;
        String smsBody = KEYWORD + " " + pushManager.getDeviceId();

        //instruction.setMessage("CENT1" + (instruction.getMessage().substring(5)));

        // Short code is manually set
        //Log.d("SHORTCODE IS MANUALLY SET!");
        //shortCode = "5556";
        //smsBody = "TEST";

        Log.d("Sending SMS to the number: '" + shortCode + "' with text: '" + smsBody + "'", TAG);
        smsMessageSender.sendSMS(shortCode, smsBody);
    }

    private void cleanUp(){
        mContext.getApplicationContext().unregisterReceiver(smsSentBroadcastReceiver);
    }
}
