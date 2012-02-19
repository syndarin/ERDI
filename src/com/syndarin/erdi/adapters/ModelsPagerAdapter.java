package com.syndarin.erdi.adapters;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.syndarin.erdi.entities.SaluteModel;

public class ModelsPagerAdapter extends PagerAdapter {

	private final int MODELS_ON_PAGE;
	
	private ArrayList<SaluteModel> models;
	private OnItemClickListener onItemClickListener;

	public ModelsPagerAdapter(ArrayList<SaluteModel> models, OnItemClickListener onItemSelectedListener, int modelsOnPage) {
		super();
		this.models = models;
		this.onItemClickListener=onItemSelectedListener;
		this.MODELS_ON_PAGE=modelsOnPage;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub
		((ViewPager) arg0).removeView((View) arg2);

	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (int)Math.ceil(models.size()/MODELS_ON_PAGE)+1;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		// TODO Auto-generated method stub

		GridView view=new GridView(arg0.getContext());
		view.setOnItemClickListener(this.onItemClickListener);
		view.setSelector(new ColorDrawable(Color.TRANSPARENT));
		view.setNumColumns(3);
		
		int page=arg1;
		ArrayList<SaluteModel> mod=new ArrayList<SaluteModel>();
		int start_index=page*MODELS_ON_PAGE;
		int end_index=start_index+MODELS_ON_PAGE;
		if(end_index>=models.size())
			end_index=models.size();
		
		for(int i=start_index; i<end_index; i++){
			mod.add(models.get(i));
		}
		
		CatalogueGridAdapter adapter=new CatalogueGridAdapter(mod);
		view.setAdapter(adapter);

        ((ViewPager) arg0).addView(view, 0);

        return view;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == ((View) arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub

	}

}
