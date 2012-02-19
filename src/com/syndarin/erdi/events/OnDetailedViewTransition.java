package com.syndarin.erdi.events;

import com.syndarin.erdi.entities.SaluteModel;

public interface OnDetailedViewTransition {
	
	void detailedMoveToNext(SaluteModel model);
	void detailedMoveToPrevious(SaluteModel model);
	void detailedBack();

}
