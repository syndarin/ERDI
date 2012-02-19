package com.syndarin.erdi.utilities;

import android.app.Activity;
import android.view.Display;
import android.view.Surface;

// ==================================================
// CLASS-HELPER, CHECKES CURRENT SCREEN ORIENTATION
//==================================================

public class DisplayChecker {
	
	public final static int DISPLAY_LAND=0;
	public final static int DISPLAY_PORT=1;
	
	public int getDisplayOrientation(Activity activity){
		Display display=activity.getWindowManager().getDefaultDisplay();
		int rotation=display.getRotation();
		if(rotation==Surface.ROTATION_0||rotation==Surface.ROTATION_180){
			return DisplayChecker.DISPLAY_PORT;
		}else{
			return DisplayChecker.DISPLAY_LAND;
		}
	}

}
