package com.syndarin.erdi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.syndarin.erdi.R;
import com.syndarin.erdi.events.OnDialogCustomEventListener;

public class StoreNumberDialog extends Dialog implements android.view.View.OnClickListener{

	private EditText editStoreNumber;
	private Resources resources;
	private OnDialogCustomEventListener onDialogCustomEventListener;
	
	public StoreNumberDialog(Context context, OnDialogCustomEventListener onDialogCustomEventListener) {
		super(context);
		// TODO Auto-generated constructor stub
		this.onDialogCustomEventListener=onDialogCustomEventListener;
		this.setContentView(R.layout.store_dialog);
		
		this.setCancelable(false);
		
		Button buttonEnterStoreNumber=(Button)findViewById(R.id.buttonEnterStoreNumber);
		buttonEnterStoreNumber.setOnClickListener(this);
		
		this.editStoreNumber=(EditText)findViewById(R.id.editStoreNumber);
		
		this.resources=getContext().getResources();
		this.setTitle(resources.getString(R.string.titleFirstLaunchMasterDialog));
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.buttonEnterStoreNumber){
			
			String storeNumberString=editStoreNumber.getText().toString();
			
			if(storeNumberString.equals("")){
				String message=resources.getString(R.string.messageEmptyStoreNumber);
				Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
			}else{
				try{
					int storeNumber=Integer.parseInt(storeNumberString);
					onDialogCustomEventListener.onEnterStoreNumber(storeNumber);
					this.dismiss();
				}catch(NumberFormatException nfe){
					String message=resources.getString(R.string.messageInvalidFormatStoreNumber);
					Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
				}
			}
		}		
	}
}
