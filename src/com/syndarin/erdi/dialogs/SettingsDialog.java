package com.syndarin.erdi.dialogs;

import com.syndarin.erdi.R;
import com.syndarin.erdi.events.OnDialogCustomEventListener;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsDialog extends Dialog implements android.view.View.OnClickListener {

	private OnDialogCustomEventListener onDialogCustomEventListener;
	private Context context;
	
	public SettingsDialog(Context context, OnDialogCustomEventListener onDialogCustomEventListener) {
		super(context);
		this.onDialogCustomEventListener=onDialogCustomEventListener;
		this.context=context;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_dialog);
		setTitle(this.context.getResources().getString(R.string.titleSettingsDialog));
		Button buttonSynchronize = (Button) findViewById(R.id.buttonSettingsSynchronize);
		buttonSynchronize.setOnClickListener(this);
		Button buttonDownload = (Button) findViewById(R.id.buttonSettingsDownload);
		buttonDownload.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.buttonSettingsSynchronize:
			this.onDialogCustomEventListener.onRunSynchronization();
			dismiss();
			break;
		case R.id.buttonSettingsDownload:
			this.onDialogCustomEventListener.onRunDownload();
			dismiss();
			break;
		default:
			break;
		}

	}

}
