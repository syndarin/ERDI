package com.syndarin.erdi.synchronizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.syndarin.erdi.ERDIActivity;
import com.syndarin.erdi.db.DatabaseManager;

public class FileChecker {

	private final static String TAG="Checker";
	private Context context;
	
	private FileChecker(Context context) {
		super();
		this.context = context;
	}

	public void checkMissingFiles(){
		DatabaseManager dbManager=new DatabaseManager(this.context);
		HashMap<String, Integer> allFiles=dbManager.getAllDownloadedFiles();
		
		for(Map.Entry<String, Integer> entry:allFiles.entrySet()){
			int fileType=entry.getValue();
			String filename=entry.getKey();
			String url, path;
			File checkingFile;
			InputStream is;
			switch(fileType){
			case FileType.FILETYPE_PICTURE:
				path=ERDIActivity.STORAGE_ROOT+PictureDownloader.LOCAL_PATH+filename;
				checkingFile=new File(path);
				if(!checkingFile.exists()){
					dbManager.markFileAsIncomplete(filename); 
					Log.i(TAG, "File "+filename+" marked as incomplete! Not exists!");
					break;
				}else{
					url=PictureDownloader.BASE_URL+filename;
					try {
						URL pictureUrl=new URL(url);
						is=pictureUrl.openStream();
						int streamSize=is.available();
						is.close();
						long fileSize=checkingFile.length();
						if(streamSize!=fileSize){
							dbManager.markFileAsIncomplete(filename); 
							Log.i(TAG, "File "+filename+" marked as incomplete! Sizes not equals!");
							break;
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case FileType.FILETYPE_ICON:
				break;
			case FileType.FILETYPE_VIDEO:
				break;
			}
		}		
	}
	
}
