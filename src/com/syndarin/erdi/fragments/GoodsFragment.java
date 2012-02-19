package com.syndarin.erdi.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.syndarin.erdi.R;
import com.syndarin.erdi.adapters.ModelsPagerAdapter;
import com.syndarin.erdi.entities.SaluteModel;
import com.syndarin.erdi.events.OnDetailedInfoRequestListener;

// ==========================================
// FRAGMENT WITH GRID OF GOODS
// ==========================================

public class GoodsFragment extends Fragment implements OnItemClickListener, OnPageChangeListener{

	private final int MODELS_MAX=9;
	private final int MODELS_MIN=6;
	
	private ArrayList<SaluteModel> models;
	private String categoryDescription;
	private OnDetailedInfoRequestListener onDetailedInfoRequestListener;
	private int modelsOnScreen;
	private ViewPager modelsPager;
	private int currentPage;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.models_grid, container, false);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		TextView categoryDescription=(TextView)this.getView().findViewById(R.id.categoryDescriptionTextView);
		
		DisplayMetrics metrics=new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		this.modelsOnScreen=(metrics.heightPixels>650) ? MODELS_MAX:MODELS_MIN;
		if(metrics.heightPixels>650){
			this.modelsOnScreen=MODELS_MAX;
			categoryDescription.setTextSize(20);
		}else{
			this.modelsOnScreen=MODELS_MIN;
			categoryDescription.setTextSize(16);
		}
		
		if(this.models!=null&&this.models.size()>0){
			modelsPager=(ViewPager)this.getView().findViewById(R.id.modelsPager);
			modelsPager.setAdapter(new ModelsPagerAdapter(this.models, this, this.modelsOnScreen));
			modelsPager.setOnPageChangeListener(this);
			modelsPager.setCurrentItem(currentPage);
			categoryDescription.setText(this.categoryDescription);
		}else{
			String message=getActivity().getResources().getString(R.string.messageNoGoodsInCategory);
			categoryDescription.setText(message);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		this.onDetailedInfoRequestListener.onDetailedInfoRequest(arg3, this.currentPage);
	}
	
	//===================================================================
	// GETTERS & SETTERS
	//===================================================================

	public ArrayList<SaluteModel> getModels() {
		return models;
	}

	public void setModels(ArrayList<SaluteModel> models) {
		this.models = models;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}

	public OnDetailedInfoRequestListener getOnDetailedInfoRequestListener() {
		return onDetailedInfoRequestListener;
	}

	public void setOnDetailedInfoRequestListener(
			OnDetailedInfoRequestListener onDetailedInfoRequestListener) {
		this.onDetailedInfoRequestListener = onDetailedInfoRequestListener;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		Log.i("PAGER", "page selected " + arg0);
        currentPage = arg0;
		
	}

	
	

	
}
