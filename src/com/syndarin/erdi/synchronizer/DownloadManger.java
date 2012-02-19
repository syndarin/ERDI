package com.syndarin.erdi.synchronizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.syndarin.erdi.db.DatabaseManager;

public class DownloadManger implements Runnable {

	private DatabaseManager dbManager;
	private Handler handler;
	private int threadsOnRun;

	public DownloadManger(Context context, Handler handler) {
		this.dbManager = new DatabaseManager(context);
		this.handler=handler;
		this.threadsOnRun=0;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ExecutorService executor = Executors.newFixedThreadPool(1);
		ArrayList<MultimediaFile> filesToDownload = this.dbManager.getAllIncompleteFilesMD5();
		for (MultimediaFile file:filesToDownload) {
			switch (file.getType()) {
			case FileType.FILETYPE_PICTURE:
				PictureDownloader pd=new PictureDownloader(file.getName(), this.downloadHandler, file.getMd5());
				executor.execute(pd);
				break;
			case FileType.FILETYPE_ICON:
				PreviewDownloader id=new PreviewDownloader(file.getName(), this.downloadHandler, file.getMd5());
				executor.execute(id);
				break;
			case FileType.FILETYPE_VIDEO:
				VideoDownloader vd=new VideoDownloader(file.getName(), this.downloadHandler, file.getMd5());
				executor.execute(vd);
				break;
			default:
				break;
			}
			this.threadsOnRun++;
		}
		executor.shutdown();
	}
	
	// INNER CLASSES
	private Handler downloadHandler=new Handler(){
		// 1 - success, 0 - fail
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Message message;
			String filename=(String)msg.obj;
			if(msg.what==1){
				dbManager.markFileAsDownloaded(filename);
				message=handler.obtainMessage(0, "Файл "+filename+" успешно загружен! Осталось "+threadsOnRun+" файлов!");
			}else{
				message=handler.obtainMessage(0, "Файл "+filename+" не был загружен корректно! Осталось "+threadsOnRun+" файлов!");
			}	
			handler.sendMessage(message);
			threadsOnRun--;
			if(threadsOnRun==0){
				message=handler.obtainMessage(1, "Загрузка файлов завершена!");
				handler.sendMessage(message);
			}
		}
		
	};

}
