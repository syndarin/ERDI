package com.syndarin.erdi.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.syndarin.erdi.entities.SaluteCategory;
import com.syndarin.erdi.entities.SaluteModel;
import com.syndarin.erdi.synchronizer.FileType;
import com.syndarin.erdi.synchronizer.MultimediaFile;

public class DatabaseManager {
	
	// ORDER BY contants
	public static final String ORDER_BY_PRICE_ASC=" ORDER BY "+DBHelper.MODEL_PRICE_COL+" ASC";
	public static final String ORDER_BY_PRICE_DESC=" ORDER BY "+DBHelper.MODEL_PRICE_COL+" DESC";
	public static final String ORDER_BY_SHOTS_ASC=" ORDER BY "+DBHelper.MODEL_SHOTS_COL+" ASC";
	public static final String ORDER_BY_SHOTS_DESC=" ORDER BY "+DBHelper.MODEL_SHOTS_COL+" DESC";
	public static final String ORDER_BY_TIME_ASC = " ORDER BY "+DBHelper.MODEL_TIME_COL+" ASC";
	public static final String ORDER_BY_TIME_DESC=" ORDER BY "+DBHelper.MODEL_TIME_COL+" DESC";
	
	private static final String TAG="DB Manager"; 
	private DBHelper helper;
	
	public DatabaseManager(Context context) {
		// TODO Auto-generated constructor stub
		helper=new DBHelper(context);
	}
	
	public ArrayList<MultimediaFile> getAllIncompleteFilesMD5(){
		ArrayList<MultimediaFile> result=new ArrayList<MultimediaFile>();
		SQLiteDatabase db=this.helper.getReadableDatabase();
		String[] args={Integer.valueOf(FileType.DOWNLOAD_INCOMPLETE).toString()};
		String query="SELECT * FROM files WHERE "+DBHelper.FILES_DOWNLOADED+"=?";
		Cursor cursor=db.rawQuery(query, args);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				String filename=cursor.getString(cursor.getColumnIndex(DBHelper.FILES_NAME_COL));
				Integer fileType=cursor.getInt(cursor.getColumnIndex(DBHelper.FILES_TYPE));
				String md5=cursor.getString(cursor.getColumnIndex(DBHelper.FILES_MD5));
				result.add(new MultimediaFile(filename, fileType, md5));
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public HashMap<String, Integer> getAllDownloadedFiles(){
		HashMap<String, Integer> result=new HashMap<String, Integer>();
		SQLiteDatabase db=this.helper.getReadableDatabase();
		String[] args={Integer.valueOf(FileType.DOWNLOAD_COMPLETE).toString()};
		String query="SELECT * FROM files WHERE "+DBHelper.FILES_DOWNLOADED+"=?";
		Cursor cursor=db.rawQuery(query, args);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				String filename=cursor.getString(cursor.getColumnIndex(DBHelper.FILES_NAME_COL));
				Integer fileType=cursor.getInt(cursor.getColumnIndex(DBHelper.FILES_TYPE));
				result.put(filename, fileType);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public boolean isFilesToDownload(){
		boolean result=false;
		SQLiteDatabase db=this.helper.getReadableDatabase();
		String query="SELECT * FROM files WHERE "+DBHelper.FILES_DOWNLOADED+"="+FileType.DOWNLOAD_INCOMPLETE;
		Cursor cursor=db.rawQuery(query, null);
		if(cursor.getCount()>0){
			result=true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public void storeFileForDownload(String filename, int fileType, String md5){
		SQLiteDatabase db=this.helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(DBHelper.FILES_NAME_COL, filename);
		values.put(DBHelper.FILES_TYPE, fileType);
		values.put(DBHelper.FILES_DOWNLOADED, FileType.DOWNLOAD_INCOMPLETE);
		values.put(DBHelper.FILES_MD5, md5);
		db.insert("files", null, values);
		db.close();
	}
	
	public void markFileAsIncomplete(String filename){
		SQLiteDatabase db=this.helper.getWritableDatabase();
		String[] args={filename};
		String query="UPDATE files SET "+DBHelper.FILES_DOWNLOADED+"="+FileType.DOWNLOAD_INCOMPLETE+" WHERE "+DBHelper.FILES_NAME_COL+"=?";
		db.execSQL(query, args);
		db.close();
	}
	
	public void markFileAsDownloaded(String filename){
		SQLiteDatabase db=this.helper.getWritableDatabase();
		String[] args={filename};
		String query="UPDATE files SET "+DBHelper.FILES_DOWNLOADED+"="+FileType.DOWNLOAD_COMPLETE+" WHERE "+DBHelper.FILES_NAME_COL+"=?";
		db.execSQL(query, args);
		db.close();
	}
	
	public HashMap<String, Integer> getIncompleteFiles(){
		HashMap<String, Integer> result=new HashMap<String, Integer>();
		SQLiteDatabase db=this.helper.getReadableDatabase();
		String[] args={Integer.valueOf(FileType.DOWNLOAD_INCOMPLETE).toString()};
		String query="SELECT * FROM files WHERE "+DBHelper.FILES_DOWNLOADED+"=?";
		Cursor cursor=db.rawQuery(query, args);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				String fileName=cursor.getString(cursor.getColumnIndex(DBHelper.FILES_NAME_COL));
				Integer fileType=cursor.getInt(cursor.getColumnIndex(DBHelper.FILES_TYPE));
				result.put(fileName, fileType);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public void skipFavourites(){
		SQLiteDatabase db=this.helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(DBHelper.MODEL_FAVOURITE_COL, 0);
		db.update("models", values, null, null);
		db.close();
	}
	
	public void clearFiles(){
		SQLiteDatabase db=this.helper.getWritableDatabase();
		db.delete("files", null, null);
		db.close();
		Log.i(TAG, "Files was truncated!");
	}
	
	public void storeFilesToDownload(HashMap<String, Integer> filesMap){
		SQLiteDatabase db=this.helper.getWritableDatabase();
		for(Map.Entry<String, Integer> entry:filesMap.entrySet()){
			ContentValues values=new ContentValues();
			values.put(DBHelper.FILES_NAME_COL, entry.getKey());
			values.put(DBHelper.FILES_TYPE, entry.getValue());
			db.insert("files", null, values);
		}
		db.close();
	}
	
	public HashMap<String, Integer> getFilesToDownload(){
		HashMap<String, Integer> result=new HashMap<String, Integer>();
		SQLiteDatabase db=this.helper.getReadableDatabase();
		String query="SELECT * FROM files";
		Cursor cursor=db.rawQuery(query, null);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String filename=cursor.getString(cursor.getColumnIndex(DBHelper.FILES_NAME_COL));
				Integer category=cursor.getInt(cursor.getColumnIndex(DBHelper.FILES_TYPE));
				result.put(filename, category);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public ArrayList<SaluteModel> getAllFavouriteModels(){
		ArrayList<SaluteModel> result=new ArrayList<SaluteModel>();
		String[] args={Integer.valueOf(1).toString()};
		String query="SELECT * FROM models WHERE "+DBHelper.MODEL_FAVOURITE_COL+"=?";
		SQLiteDatabase db=this.helper.getReadableDatabase();
		Cursor cursor=db.rawQuery(query, args);
		Log.w(TAG, "Cursor is created!");
		int rowCount=cursor.getCount();
		if(rowCount>0){
			cursor.moveToFirst();
			String temp;
			while(!cursor.isAfterLast()){
				SaluteModel model=new SaluteModel();
				
				int modelId=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_ID_COL));
				model.setId(modelId);
				
				int modelParentId=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_CAT_ID_COL));
				model.setParentId(modelParentId);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_CODE_COL));
				model.setCode(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_TITLE_COL));
				model.setTitle(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_DESCRIPTION_COL));
				model.setDesription(temp);
				
				int modelShots=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_SHOTS_COL));
				model.setShots(modelShots);
				
				int modelTime=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_TIME_COL));
				model.setTime(modelTime);
				
				float modelPrice=cursor.getFloat(cursor.getColumnIndex(DBHelper.MODEL_PRICE_COL));
				model.setPrice(modelPrice);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_PREVIEW_COL));
				model.setPreviewFile(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_PICTURE_COL));
				model.setPictureFile(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_VIDEO_COL));
				model.setVideoFile(temp);
				
				result.add(model);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return result;
	}
	
	
	public void setModelAsDefault(int id){
		SQLiteDatabase db=helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(DBHelper.MODEL_FAVOURITE_COL, 0);
		String[] args={Integer.valueOf(id).toString()};
		db.update("models", values, DBHelper.MODEL_ID_COL+"=?", args);
		db.close();
	}
	
	public void setModelAsFavourite(int id){
		SQLiteDatabase db=helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(DBHelper.MODEL_FAVOURITE_COL, 1);
		String[] args={Integer.valueOf(id).toString()};
		db.update("models", values, DBHelper.MODEL_ID_COL+"=?", args);
		db.close();
	}
	
	public SaluteModel getModelById(long id){
		SaluteModel model=new SaluteModel();
		
		SQLiteDatabase db=helper.getReadableDatabase();
		String[] args={Long.valueOf(id).toString()};
		
		String query="SELECT * FROM models WHERE "+DBHelper.MODEL_ID_COL+"=?";
		Cursor cursor=db.rawQuery(query, args);
		if(cursor.getCount()>0){
			String temp;
			cursor.moveToFirst();
			
			int modelId=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_ID_COL));
			model.setId(modelId);
			
			int modelParentId=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_CAT_ID_COL));
			model.setParentId(modelParentId);
			
			temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_CODE_COL));
			model.setCode(temp);
			
			temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_TITLE_COL));
			model.setTitle(temp);
			
			temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_DESCRIPTION_COL));
			model.setDesription(temp);
			
			int modelShots=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_SHOTS_COL));
			model.setShots(modelShots);
			
			int modelTime=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_TIME_COL));
			model.setTime(modelTime);
			
			float modelPrice=cursor.getFloat(cursor.getColumnIndex(DBHelper.MODEL_PRICE_COL));
			model.setPrice(modelPrice);
			
			temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_PICTURE_COL));
			model.setPictureFile(temp);
			
			temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_PREVIEW_COL));
			model.setPreviewFile(temp);
			
			temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_VIDEO_COL));
			model.setVideoFile(temp);
			
			int favourite=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_FAVOURITE_COL));
			boolean isFavourie=(favourite==0) ? false:true;
			model.setFavourite(isFavourie);
		}
		cursor.close();
		db.close();
		return model;
	}
	
	public String getCategoryDescription(long category){
		String result="";		
		SQLiteDatabase db=this.helper.getReadableDatabase();
		String[] args={Long.valueOf(category).toString()};
		String query="SELECT "+DBHelper.CAT_DESC_COL+" FROM categories WHERE "+DBHelper.CAT_ID_COL+"=?";
		Cursor cursor=db.rawQuery(query, args);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			result=cursor.getString(cursor.getColumnIndex(DBHelper.CAT_DESC_COL));
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public ArrayList<SaluteModel> getModelsByCategorySorted(long category, String sortParameters){
		ArrayList<SaluteModel> result=new ArrayList<SaluteModel>();
		
		SQLiteDatabase db=this.helper.getReadableDatabase();
		String[] args={Long.valueOf(category).toString()};
		String query="SELECT * FROM models WHERE "+DBHelper.MODEL_CAT_ID_COL+"=? "+sortParameters;
		Cursor cursor=db.rawQuery(query, args);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			String temp;
			while(!cursor.isAfterLast()){
				SaluteModel model=new SaluteModel();
				
				int modelId=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_ID_COL));
				model.setId(modelId);
				
				int modelParentId=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_CAT_ID_COL));
				model.setParentId(modelParentId);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_CODE_COL));
				model.setCode(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_TITLE_COL));
				model.setTitle(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_DESCRIPTION_COL));
				model.setDesription(temp);
				
				int modelShots=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_SHOTS_COL));
				model.setShots(modelShots);
				
				int modelTime=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_TIME_COL));
				model.setTime(modelTime);
				
				float modelPrice=cursor.getFloat(cursor.getColumnIndex(DBHelper.MODEL_PRICE_COL));
				model.setPrice(modelPrice);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_PICTURE_COL));
				model.setPictureFile(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_PREVIEW_COL));
				model.setPreviewFile(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_VIDEO_COL));
				model.setVideoFile(temp);
				
				result.add(model);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public ArrayList<SaluteModel> getModelsByCategory(long category){
		ArrayList<SaluteModel> result=new ArrayList<SaluteModel>();
		
		SQLiteDatabase db=this.helper.getReadableDatabase();
		String[] args={Long.valueOf(category).toString()};
		String query="SELECT * FROM models WHERE "+DBHelper.MODEL_CAT_ID_COL+"=?";
		Cursor cursor=db.rawQuery(query, args);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			String temp;
			while(!cursor.isAfterLast()){
				SaluteModel model=new SaluteModel();
				
				int modelId=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_ID_COL));
				model.setId(modelId);
				
				int modelParentId=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_CAT_ID_COL));
				model.setParentId(modelParentId);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_CODE_COL));
				model.setCode(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_TITLE_COL));
				model.setTitle(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_DESCRIPTION_COL));
				model.setDesription(temp);
				
				int modelShots=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_SHOTS_COL));
				model.setShots(modelShots);
				
				int modelTime=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_TIME_COL));
				model.setTime(modelTime);
				
				float modelPrice=cursor.getFloat(cursor.getColumnIndex(DBHelper.MODEL_PRICE_COL));
				model.setPrice(modelPrice);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_PICTURE_COL));
				model.setPictureFile(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_PREVIEW_COL));
				model.setPreviewFile(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_VIDEO_COL));
				model.setVideoFile(temp);
				
				result.add(model);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public void insertCategories(ArrayList<SaluteCategory> categories){
		
		SQLiteDatabase db=this.helper.getWritableDatabase();
		
		for(SaluteCategory category: categories){
			ContentValues values=new ContentValues();
			values.put(DBHelper.CAT_ID_COL, category.getId());
			values.put(DBHelper.CAT_TITLE_COL, category.getTitle());
			values.put(DBHelper.CAT_DESC_COL, category.getDescription());
			db.insert("categories", null, values);			
		}
		
		db.close();
	}
	
	public void insertModels(ArrayList<SaluteModel> models){
		
		SQLiteDatabase db=this.helper.getWritableDatabase();
		
		for(SaluteModel model:models){
			ContentValues values=new ContentValues();
			values.put(DBHelper.MODEL_ID_COL, model.getId());
			values.put(DBHelper.MODEL_CAT_ID_COL, model.getParentId());
			values.put(DBHelper.MODEL_CODE_COL, model.getCode());
			values.put(DBHelper.MODEL_TITLE_COL, model.getTitle());
			values.put(DBHelper.MODEL_DESCRIPTION_COL, model.getDesription());
			values.put(DBHelper.MODEL_SHOTS_COL, model.getShots());
			values.put(DBHelper.MODEL_TIME_COL, model.getTime());
			values.put(DBHelper.MODEL_PRICE_COL, model.getPrice());
			values.put(DBHelper.MODEL_PREVIEW_COL, model.getPreviewFile());
			values.put(DBHelper.MODEL_PICTURE_COL, model.getPictureFile());
			values.put(DBHelper.MODEL_VIDEO_COL, model.getVideoFile());
			values.put(DBHelper.MODEL_FAVOURITE_COL, 0);
			db.insert("models", null, values);
		}
		
		db.close();		
	}
	
	// function gets all info about model without favourite flag
	public ArrayList<SaluteModel> getAllModelsBaseInfo(){
		ArrayList<SaluteModel> result=new ArrayList<SaluteModel>();
		String query="SELECT * FROM models";
		SQLiteDatabase db=this.helper.getReadableDatabase();
		Cursor cursor=db.rawQuery(query, null);
		Log.w(TAG, "Cursor is created!");
		int rowCount=cursor.getCount();
		if(rowCount>0){
			cursor.moveToFirst();
			String temp;
			while(!cursor.isAfterLast()){
				SaluteModel model=new SaluteModel();
				
				int modelId=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_ID_COL));
				model.setId(modelId);
				
				int modelParentId=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_CAT_ID_COL));
				model.setParentId(modelParentId);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_CODE_COL));
				model.setCode(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_TITLE_COL));
				model.setTitle(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_DESCRIPTION_COL));
				model.setDesription(temp);
				
				int modelShots=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_SHOTS_COL));
				model.setShots(modelShots);
				
				int modelTime=cursor.getInt(cursor.getColumnIndex(DBHelper.MODEL_TIME_COL));
				model.setTime(modelTime);
				
				float modelPrice=cursor.getFloat(cursor.getColumnIndex(DBHelper.MODEL_PRICE_COL));
				model.setPrice(modelPrice);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_PREVIEW_COL));
				model.setPreviewFile(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_PICTURE_COL));
				model.setPictureFile(temp);
				
				temp=cursor.getString(cursor.getColumnIndex(DBHelper.MODEL_VIDEO_COL));
				model.setVideoFile(temp);
				
				result.add(model);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public ArrayList<SaluteCategory> getAllCategoriesFullInfo(){
		ArrayList<SaluteCategory> result=new ArrayList<SaluteCategory>();
		String query="SELECT * FROM categories;";
		SQLiteDatabase db=this.helper.getReadableDatabase();
		Cursor cursor=db.rawQuery(query, null);
		int numRows=cursor.getCount();
		if(numRows>0){
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				int categoryId=cursor.getInt(cursor.getColumnIndex(DBHelper.CAT_ID_COL));
				String categoryTitle=cursor.getString(cursor.getColumnIndex(DBHelper.CAT_TITLE_COL));
				String categoryDescription=cursor.getString(cursor.getColumnIndex(DBHelper.CAT_DESC_COL));

				result.add(new SaluteCategory(categoryId, categoryTitle, categoryDescription));
				
				cursor.moveToNext();
			}
			
		}
		cursor.close();
		db.close();
		return result;
	}
}
