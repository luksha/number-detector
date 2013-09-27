package com.infobip.numberdetector;

public interface OperationEventHandler {	
	public void onComplete();
	public void onFail();
	public void onCancel();
}
