package com.syndarin.erdi.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.syndarin.erdi.ERDIActivity;
import com.syndarin.erdi.R;
import com.syndarin.erdi.entities.SaluteModel;

public class CatalogueGridAdapter extends BaseAdapter {
	
	private ArrayList<SaluteModel> models=null;

	public CatalogueGridAdapter(ArrayList<SaluteModel> models) {
		super();
		this.models = models;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return models.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.models.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return this.models.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SaluteModel item=this.models.get(position);
		
		Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.grid_item, null);

        TextView model_name = (TextView)vg.findViewById(R.id.grid_item_model_name);
        ImageView model_image = (ImageView)vg.findViewById(R.id.grid_item_model_icon);
        TextView model_price=(TextView)vg.findViewById(R.id.grid_item_model_price);
        
        model_name.setText(item.getCode()+" "+item.getTitle());
        Bitmap modelBitmap=BitmapFactory.decodeFile(ERDIActivity.STORAGE_ROOT+"/preview/"+item.getPreviewFile());
        if(modelBitmap!=null){
        	model_image.setImageBitmap(modelBitmap);
        }else{
        	model_image.setImageResource(R.drawable.stub);
        }
        model_price.setText(context.getResources().getString(R.string.gridLabelPrice)+" "+item.getPrice()+" "+context.getResources().getString(R.string.gridLabelCurrency));

        return vg;
	}
	
	


}
