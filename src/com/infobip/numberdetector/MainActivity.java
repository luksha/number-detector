package com.infobip.numberdetector;

import com.infobip.numberdetector.util.*;

import com.infobip.numberdetector.sms.SmsSendOperation;
import com.infobip.push.PushNotificationManager;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	PushNotificationManager pushManager;
	private final String TAG = "MainActivity";
	private EditText editTextPhoneNumber;
	private static final String MSISDN = "msisdn";
	
	private final String SERVER_HOST = "http://infobip.cer.co.rs/";
	private final String URL_SUBSCRIBE = "subscribe.php";
	private final String URL_UNSUBSCRIBE = "unsubscribe.php";
	
	private final String SENDER_ID = "447903975137";
	private final String APPLICATION_ID = "1f7740a7a111";
	private final String APPLICATION_SECRET = "2fc88a7bafd4";
	
	ProgressDialog progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate() started.");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((Button)findViewById(R.id.buttonDetectNumber)).setOnClickListener(mDetectNumberButtonListener);
		editTextPhoneNumber = ((EditText)findViewById(R.id.editText1));
		editTextPhoneNumber.setVisibility(View.GONE);
		
		if(!TextUtils.isEmpty(getIntent().getStringExtra(MSISDN))){
			editTextPhoneNumber.setText(getIntent().getStringExtra(MSISDN));
		}
		
		progress = new ProgressDialog(this);
		
		pushManager = new PushNotificationManager(MainActivity.this);
		pushManager.initialize(SENDER_ID, APPLICATION_ID, APPLICATION_SECRET);
		pushManager.overrideDefaultMessageHandling(true);
		pushManager.setDebugModeEnabled(true);
		if(!pushManager.isRegistered()){
			pushManager.register();
			Log.d(TAG, "Device ID: " + pushManager.getDeviceId());
			subscribeOnServer();
		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent() started.");
		setIntent(intent);
		progress.dismiss();
		editTextPhoneNumber.setVisibility(View.VISIBLE);
		editTextPhoneNumber.setText(intent.getStringExtra(MSISDN));
		super.onNewIntent(intent);
	}
	
	private OperationEventHandler smsSentEventHandler = new OperationEventHandler() {
		
		@Override
		public void onComplete() {
			Log.d(TAG, "SMS sending successful.");
            //TODO onComplete
		}
		
		@Override
		public void onFail() {
			Log.d(TAG, "SMS sending failed.");
			//TODO onFail
		}
		
		@Override
		public void onCancel() {
            Log.d(TAG, "SMS sending canceled.");
            //TODO onCancel
		}
	};
	
	OnClickListener mDetectNumberButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.d(TAG, "User clicked Number Detect Button.");
			progress.setTitle("Loading");
			progress.setMessage("Wait while loading...");
			progress.show();
			progress.setCancelable(false);
			SmsSendOperation sendSms = new SmsSendOperation(smsSentEventHandler, MainActivity.this, pushManager);
			sendSms.run();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void subscribeOnServer() {
		Thread networkThread = new Thread(new Runnable() {

            @Override
            public void run() {
            	//PushNotificationManager pushManger = new PushNotificationManager(MainActivity.this);
                //Get JSON from Centili server
            	InfobipRestClient restClient = new InfobipRestClient(SERVER_HOST + URL_SUBSCRIBE);
                restClient.addParam("device_id", pushManager.getDeviceId());
	            try {
					restClient.execute(InfobipRestClient.POST);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            int response = restClient.getResponseCode();
	            
	            if(response == 200){
	            	Log.d(TAG, "Successfully SUBSCRIBED with device id: " + pushManager.getDeviceId());
	            }
	            

            }
        });

        networkThread.start();
    }
	
	public void unsubscribeOnServer() {
		Thread networkThread = new Thread(new Runnable() {

            @Override
            public void run() {
                //Get JSON from Centili server
            	InfobipRestClient restClient = new InfobipRestClient(SERVER_HOST + URL_UNSUBSCRIBE);
                restClient.addParam("device_id", pushManager.getDeviceId());
	            try {
					restClient.execute(InfobipRestClient.POST);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            int response = restClient.getResponseCode();

	            if(response == 200){
	            	Log.d(TAG, "Successfully UNSUBSCRIBED with device id: " + pushManager.getDeviceId());
	            }

            }
        });

        networkThread.start();
    }

}
