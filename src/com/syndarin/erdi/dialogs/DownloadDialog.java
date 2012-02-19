package com.syndarin.erdi.dialogs;

import com.syndarin.erdi.R;
import com.syndarin.erdi.events.OnDialogCustomEventListener;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DownloadDialog extends Dialog implements android.view.View.OnClickListener {

	private OnDialogCustomEventListener onDialogCustomEventListener;

	public DownloadDialog(Context context, OnDialogCustomEventListener onDialogCustomEventListener) {
		super(context);
		// TODO Auto-generated constructor stub
		this.onDialogCustomEventListener = onDialogCustomEventListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_dialog);
		Button buttonStart = (Button) findViewById(R.id.buttonDownloadNow);
		buttonStart.setOnClickListener(this);
		Button buttonLater = (Button) findViewById(R.id.buttonDownloadLater);
		buttonLater.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.buttonDownloadNow:
			this.onDialogCustomEventListener.startDownload();
			break;
		case R.id.buttonDownloadLater:
			break;
		default:
			break;
		}
		dismiss();
	}

}
