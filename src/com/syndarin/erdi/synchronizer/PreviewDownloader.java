package com.syndarin.erdi.synchronizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.syndarin.erdi.ERDIActivity;

public class PreviewDownloader implements Runnable {

	private final static String TAG = "PICTURE DOWNLOADER";
	public final static String BASE_URL = "http://www.skyfire.dn.ua/sites/default/files/styles/ware-preview/public/pictures/";
	public final static String LOCAL_PATH = "/preview/";
	private final static int BYTE_BUFFER_SIZE = 1024;
	private String filename;
	private Handler handler;
	private FileOutputStream pictureOS;
	private InputStream pictureIS;
	private String md5;

	// =========================================
	// CONSTRUCTOR
	// =========================================

	public PreviewDownloader(String filename, Handler handler, String md5) {
		this.filename = filename;
		this.handler = handler;
		this.md5=md5;
	}

	// =========================================
	// OVERRIDEN METHODS
	// =========================================

	@Override
	public void run() {
		// TODO Auto-generated method stub
		File targetFile = new File(ERDIActivity.STORAGE_ROOT + LOCAL_PATH + filename);
		if (targetFile.exists()) {
			targetFile.delete();
		}
		try {
			targetFile.createNewFile();

			try {
				Log.w(TAG, "starting download " + filename);
				URL downloadUrl = new URL(BASE_URL + filename);
				pictureIS = downloadUrl.openStream();
				pictureOS = new FileOutputStream(targetFile);
				byte[] buffer = new byte[BYTE_BUFFER_SIZE];
				int qBytes = 0;
				while ((qBytes = pictureIS.read(buffer)) != -1) {
					if (qBytes == BYTE_BUFFER_SIZE) {
						pictureOS.write(buffer);
					} else {
						pictureOS.write(buffer, 0, qBytes);
					}
				}
				pictureIS.close();
				pictureOS.close();
//				if(targetFile.length()!=0){
//					Log.w(TAG, "Download " + filename + " completed!");
//					Message message = handler.obtainMessage(1, filename);
//					handler.sendMessage(message);
//				}else{
//					Message message = handler.obtainMessage(0, filename);
//					handler.sendMessage(message);
//				}
				MD5Generator mdChecker=new MD5Generator();
				String md5Sum=mdChecker.getFileMD5(targetFile);
				if(md5Sum.equals(this.md5)){
					Log.w(TAG, "Download " + filename + " completed!");
					Message message = handler.obtainMessage(1, filename);
					handler.sendMessage(message);
				}else{
					Log.w(TAG, "MD5 not equals " + filename + "!");
					Message message = handler.obtainMessage(0, filename);
					handler.sendMessage(message);
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.w(TAG, "URL incorrect!");
				Message message = handler.obtainMessage(0, filename);
				handler.sendMessage(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.w(TAG, "IO Exception while download!");
				Message message = handler.obtainMessage(0, filename);
				handler.sendMessage(message);
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.w(TAG, "IO Exception while creating file!");
			Message message = handler.obtainMessage(0, filename);
			handler.sendMessage(message);
		} catch (Exception ee){
			ee.printStackTrace();
			Message message = handler.obtainMessage(0, filename);
			handler.sendMessage(message);
		}
	}
}
