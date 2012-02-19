package com.syndarin.erdi.events;

public interface OnDialogCustomEventListener {
	
	void onEnterStoreNumber(int storeNumber);
	void onRunSynchronization();
	void onRunDownload();
	void startDownload();

}
