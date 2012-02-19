package com.syndarin.erdi.synchronizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.syndarin.erdi.ERDIActivity;

public class VideoDownloader implements Runnable {
	public final static String TAG="VIDEO DOWNLOADER";
	public final static String LOCAL_PATH = "/video/";
	private String filename;
	private Handler handler;
	private FileOutputStream videoOut;
	private String md5;

	public VideoDownloader(String filename, Handler handler, String md5) {
		this.filename = filename;
		this.handler = handler;
		this.md5=md5;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		File targetFile = new File(ERDIActivity.STORAGE_ROOT+LOCAL_PATH+ filename);

		if(targetFile.exists()){
			targetFile.delete();
		}
		
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect("ftp.skyfire.dn.ua", 21);
			ftpClient.enterLocalPassiveMode();
			ftpClient.login("word", "word123");

			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			targetFile.createNewFile();
			videoOut = new FileOutputStream(targetFile);
			boolean result=ftpClient.retrieveFile("/" + this.filename, videoOut);
			
			ftpClient.disconnect();
			videoOut.close();

			if(result){
				Message message = handler.obtainMessage(1, filename);
				handler.sendMessage(message);
			}else{
				Message message = handler.obtainMessage(0, filename);
				handler.sendMessage(message);
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message message = handler.obtainMessage(0, filename);
			handler.sendMessage(message);
		} catch (Exception e){
			e.printStackTrace();
			Message message = handler.obtainMessage(0, filename);
			handler.sendMessage(message);
		}
	}
}
