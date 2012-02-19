package com.syndarin.erdi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private final static String DB_NAME = "ERDIDB";
	private final static int DB_VERSION = 1;
	/* fields names for table authors */
	public final static String CAT_ID_COL = "category_id";
	public final static String CAT_TITLE_COL = "category_title";
	public final static String CAT_DESC_COL = "category_description";
	/* fields names for table tracks */
	public final static String MODEL_ID_COL = "model_id";
	public final static String MODEL_CAT_ID_COL = "model_parent_id";
	public final static String MODEL_CODE_COL = "model_code";
	public final static String MODEL_TITLE_COL = "model_title";
	public final static String MODEL_DESCRIPTION_COL = "model_description";
	public final static String MODEL_SHOTS_COL = "model_shots";
	public final static String MODEL_TIME_COL = "model_time";
	public final static String MODEL_PRICE_COL = "model_price";
	public final static String MODEL_PREVIEW_COL="model_preview";
	public final static String MODEL_PICTURE_COL = "model_picture";
	public final static String MODEL_VIDEO_COL = "model_video";
	public final static String MODEL_FAVOURITE_COL = "model_favourite";
	/* table of files to download */
	public final static String FILES_NAME_COL="filename";
	public final static String FILES_TYPE="file_type";
	public final static String FILES_DOWNLOADED="downloaded";
	public final static String FILES_MD5="md5";
	

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		StringBuilder queryBuilder=new StringBuilder();
		
		queryBuilder.append("CREATE TABLE categories (");
		queryBuilder.append(CAT_ID_COL+ " INTEGER PRIMARY KEY, ");
		queryBuilder.append(CAT_TITLE_COL+ " TEXT NOT NULL, ");
		queryBuilder.append(CAT_DESC_COL + " TEXT NOT NULL);");

		String queryCreateCategories=queryBuilder.toString();
		
		queryBuilder=new StringBuilder();
		
		queryBuilder.append("CREATE TABLE models (");
		queryBuilder.append(MODEL_ID_COL+" INTEGER PRIMARY KEY, ");
		queryBuilder.append(MODEL_CAT_ID_COL+" INTEGER NOT NULL, ");
		queryBuilder.append(MODEL_CODE_COL+" TEXT, ");
		queryBuilder.append(MODEL_TITLE_COL+" TEXT, ");
		queryBuilder.append(MODEL_DESCRIPTION_COL+" TEXT, ");
		queryBuilder.append(MODEL_SHOTS_COL+" INTEGER NOT NULL, ");
		queryBuilder.append(MODEL_TIME_COL+" INTEGER NOT NULL, ");
		queryBuilder.append(MODEL_PRICE_COL+" REAL NOT NULL, ");
		queryBuilder.append(MODEL_PREVIEW_COL+" TEXT, ");
		queryBuilder.append(MODEL_PICTURE_COL+" TEXT, ");
		queryBuilder.append(MODEL_VIDEO_COL+" TEXT, ");
		queryBuilder.append(MODEL_FAVOURITE_COL+" INTEGER NOT NULL);");
		
		String queryCreateModels=queryBuilder.toString();
		
		queryBuilder=new StringBuilder();
		
		queryBuilder.append("CREATE TABLE files (");
		queryBuilder.append(FILES_NAME_COL+" TEXT NOT NULL, ");
		queryBuilder.append(FILES_TYPE+" INTEGER NOT NULL, ");
		queryBuilder.append(FILES_DOWNLOADED+" INTEGER NOT NULL, ");
		queryBuilder.append(FILES_MD5+" TEXT NOT NULL);");
		
		String queryCreateFiles=queryBuilder.toString();
		
		try {
			
			db.execSQL(queryCreateCategories);

			db.execSQL(queryCreateModels);
			
			db.execSQL(queryCreateFiles);

			Log.w("CREATING TABLES","Tables was created successfully!");
		} catch (SQLiteException e) {
			Log.e("DB ERROR", "Exception while creating tables: "+ e.getMessage());
		}

	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		/* ALLOW USING FOREIGN KEYS TO CASCADE MANIPULATIONS */
		db.execSQL("PRAGMA foreign_keys=ON;");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String deleteCategories = "DROP TABLE IF EXISTS categories";
		String deleteModels = "DROP TABLE IF EXISTS models";
		String deleteFiles="DROP TABLE IF EXISTS files";

		db.execSQL(deleteCategories);
		db.execSQL(deleteModels);
		db.execSQL(deleteFiles);

		Log.w("UPGRADING DB","Tables was dropped!");

		this.onCreate(db);

	}

}
