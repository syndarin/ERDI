package com.syndarin.erdi.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.syndarin.erdi.R;
import com.syndarin.erdi.entities.SaluteCategory;

public class CategoriesListAdapter extends BaseAdapter {

	private ArrayList<SaluteCategory> categories;
	
	public CategoriesListAdapter(ArrayList<SaluteCategory> categories) {
		this.categories = categories;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.categories.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.categories.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return this.categories.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Context context=parent.getContext();
		LayoutInflater inflater=LayoutInflater.from(context);
		ViewGroup view=(ViewGroup)inflater.inflate(R.layout.list_element, null);
		TextView text=(TextView)view.findViewById(R.id.listElementText);
		text.setText(this.categories.get(position).getTitle());
		return view;
	}

}
