package com.syndarin.erdi.events;

import java.util.ArrayList;

import com.syndarin.erdi.entities.SaluteCategory;
import com.syndarin.erdi.entities.SaluteModel;

public interface OnParserProgressEventsListener {
	
	void onParsingSuccess(ArrayList<SaluteCategory> categories, ArrayList<SaluteModel> models);
	void onParsingError(String message);

}
