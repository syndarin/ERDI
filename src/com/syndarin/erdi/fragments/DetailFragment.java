package com.syndarin.erdi.fragments;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.syndarin.erdi.ERDIActivity;
import com.syndarin.erdi.R;
import com.syndarin.erdi.db.DatabaseManager;
import com.syndarin.erdi.dialogs.VideoDialog;
import com.syndarin.erdi.entities.SaluteModel;
import com.syndarin.erdi.events.OnDetailedViewTransition;
import com.syndarin.erdi.synchronizer.PictureDownloader;

//==========================================
//FRAGMENT WITH DETAILED INFO
//==========================================

public class DetailFragment extends Fragment implements OnClickListener {

	private final static int PICTURE_BIG=350;
	private final static int PICTURE_SMALL=250;
	
	private SaluteModel model;
	private ImageButton buttonFavourite;
	private DatabaseManager dbManager;
	private OnDetailedViewTransition onDetailedViewTransition;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.detailed_view, container, false);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		this.dbManager=new DatabaseManager(this.getView().getContext());
		TextView descriptionTextView=(TextView)this.getView().findViewById(R.id.detailedViewDescription);
		descriptionTextView.setText(this.model.getDesription());
		
		TextView titleTextView=(TextView)this.getView().findViewById(R.id.detailedTitle);
		titleTextView.setText(this.model.getTitle());
		TextView timeTextView=(TextView)this.getView().findViewById(R.id.detailedTime);
		timeTextView.setText(Integer.valueOf(this.model.getTime()).toString());
		TextView shotsTextView=(TextView)this.getView().findViewById(R.id.detailedShots);
		shotsTextView.setText(Integer.valueOf(this.model.getShots()).toString());
		TextView priceTextView=(TextView)this.getView().findViewById(R.id.detailedPrice);
		priceTextView.setText(Double.valueOf(this.model.getPrice()).toString());
		ImageButton buttonNextModel=(ImageButton)this.getView().findViewById(R.id.buttonDetailedMoveNext);
		buttonNextModel.setOnClickListener(this);
		ImageButton buttonPreviousModel=(ImageButton)this.getView().findViewById(R.id.buttonDetailedMovePrevious);
		buttonPreviousModel.setOnClickListener(this);
		ImageButton buttonCloseSelf=(ImageButton)this.getView().findViewById(R.id.buttonDetaildeBack);
		buttonCloseSelf.setOnClickListener(this);
		ImageButton buttonPlayVideo=(ImageButton)this.getView().findViewById(R.id.buttonPlayVideo);
		buttonPlayVideo.setOnClickListener(this);
		
		DisplayMetrics metrics=new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		ImageView modelIcon=(ImageView)this.getView().findViewById(R.id.detailedViewIcon);
		modelIcon.setOnClickListener(this);
		String bitmapPath=this.model.getPictureFile();
		if(bitmapPath!=null&!bitmapPath.equals("")){
			File pictureFile=new File(ERDIActivity.STORAGE_ROOT+PictureDownloader.LOCAL_PATH+this.model.getPictureFile());
			if(pictureFile.exists()&&pictureFile.length()>0){
				Bitmap modelBitmap=BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
				if(metrics.heightPixels>650){
					modelIcon.setImageBitmap(this.getResizedBitmap(modelBitmap, PICTURE_BIG, PICTURE_BIG));
				}else{
					modelIcon.setImageBitmap(this.getResizedBitmap(modelBitmap, PICTURE_SMALL, PICTURE_SMALL));
				}
				
			}
			
		}
		
		this.buttonFavourite=(ImageButton)this.getView().findViewById(R.id.detailedButtonFav);
		this.buttonFavourite.setOnClickListener(this);
		if(this.model.isFavourite()){
			this.buttonFavourite.setImageResource(R.drawable.fav_enabled);
		}else{
			this.buttonFavourite.setImageResource(R.drawable.fav_disabled);
		}

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int sender=v.getId();
		switch(sender){
		case R.id.buttonDetaildeBack:
			this.onDetailedViewTransition.detailedBack();
			break;
		case R.id.detailedViewIcon:
			toggleFavourite();
			break;
		case R.id.detailedButtonFav:
			toggleFavourite();
			break;
		case R.id.buttonDetailedMoveNext:
			this.onDetailedViewTransition.detailedMoveToNext(this.model);
			break;
		case R.id.buttonDetailedMovePrevious:
			this.onDetailedViewTransition.detailedMoveToPrevious(this.model);
			break;
		case R.id.buttonPlayVideo:
			if(this.model.getVideoFile()!=null&&!this.model.getVideoFile().equals("")){
				VideoDialog dialog=new VideoDialog(getActivity(), this.model.getVideoFile());
				dialog.show();
			}
			break;
		default:
			break;
		}
	}
	
	private void toggleFavourite(){
		if(!this.model.isFavourite()){
			this.model.setFavourite(true);
			this.buttonFavourite.setImageResource(R.drawable.fav_enabled);
			this.dbManager.setModelAsFavourite(this.model.getId());
		}else{
			this.model.setFavourite(false);
			this.buttonFavourite.setImageResource(R.drawable.fav_disabled);
			this.dbManager.setModelAsDefault(this.model.getId());
		}
	}
	
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		 
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);
		// RECREATE THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}

	public SaluteModel getModel() {
		return model;
	}

	public void setModel(SaluteModel model) {
		this.model = model;
	}
	
	public void setOnDetailedViewTransition(
			OnDetailedViewTransition onDetailedViewTransition) {
		this.onDetailedViewTransition = onDetailedViewTransition;
	}

	

}
