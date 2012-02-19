package com.syndarin.erdi.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetChecker {

	public static boolean isOnline(Context context){
		ConnectivityManager cManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo=cManager.getActiveNetworkInfo();
		return (nInfo==null) ? false:true;
	}
}
