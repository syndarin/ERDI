package com.syndarin.erdi.synchronizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.syndarin.erdi.db.DatabaseManager;
import com.syndarin.erdi.entities.SaluteCategory;
import com.syndarin.erdi.entities.SaluteModel;
import com.syndarin.erdi.events.OnParserProgressEventsListener;

public class SynchronizeManager implements Runnable, OnParserProgressEventsListener {

	private final static String TAG = "SYNCHRONIZE MANAGER";
	private final static String SYNCHRONIZE_URL = "http://www.skyfire.dn.ua/xmlcatalog/";

	private Context context;
	private Handler handler;
	private int storeNumber;

	private DatabaseManager dbManager;
	private ArrayList<SaluteCategory> categoryToAdd;
	private ArrayList<SaluteModel> modelsToAdd;

	// =================================================
	// CONSTRUCTOR
	// =================================================

	public SynchronizeManager(Context context, Handler handler, int storeNumber) {
		this.context = context;
		this.handler = handler;
		this.storeNumber = storeNumber;
	}

	// =================================================
	// OVERRIDEN METHODS
	// =================================================
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Message message = handler.obtainMessage(0, "Запуск синхронизации...");
		handler.sendMessage(message);

		HttpClient httpClient = new DefaultHttpClient();

		HttpGet getRequest = new HttpGet(SYNCHRONIZE_URL + storeNumber);

		try {

			HttpResponse httpResponse = httpClient.execute(getRequest);
			HttpEntity responseEntity = httpResponse.getEntity();
			InputStream responseXMLInputStream = responseEntity.getContent();

			UpdateResponceParser responceParser = new UpdateResponceParser(responseXMLInputStream, this);
			Thread parsingThread = new Thread(responceParser);
			parsingThread.start();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "Client Protocol Exception occurs!");
			Message exceptionMessage = this.handler.obtainMessage(1, "Protocol Error!");
			handler.dispatchMessage(exceptionMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "IO Exception occurs!");
			Message exceptionMessage = this.handler.obtainMessage(1, "IO Exception!");
			handler.dispatchMessage(exceptionMessage);
		}

	}

	@Override
	public void onParsingSuccess(ArrayList<SaluteCategory> categories, ArrayList<SaluteModel> models) {
		// TODO Auto-generated method stub
		Message message = handler.obtainMessage(0, "Ответ сервера успешно обработан!");
		handler.sendMessage(message);

		dbManager = new DatabaseManager(this.context);

		ArrayList<SaluteCategory> categoriesList = dbManager.getAllCategoriesFullInfo();
		this.categoryToAdd = new ArrayList<SaluteCategory>();

		if (categoriesList.size() > 0) {
			Log.i(TAG, "Has categories in database!");
			// iterating server categories
			for (int i = 0; i < categories.size(); i++) {
				SaluteCategory serverCategory = categories.get(i);
				// if cat contains in base - next
				if (categoriesList.contains(serverCategory)) {
					continue;
				} else {
					categoryToAdd.add(serverCategory);
				}
			}
		} else {
			categoryToAdd.addAll(categories);
		}
		// if need to add categories
		if (categoryToAdd.size() > 0) {
			dbManager.insertCategories(categoryToAdd);
			message = handler.obtainMessage(0, "Категории обновлены!");
			handler.sendMessage(message);
		} else {
			message = handler.obtainMessage(0, "Категории не требуют обновления!");
			handler.sendMessage(message);
		}

		ArrayList<SaluteModel> modelsList = dbManager.getAllModelsBaseInfo();
		this.modelsToAdd = new ArrayList<SaluteModel>();

		// checking models
		if (modelsList.size() > 0) {
			for (int i = 0; i < models.size(); i++) {
				SaluteModel serverModel = models.get(i);
				if (modelsList.contains(serverModel)) {
					continue;
				} else {
					modelsToAdd.add(serverModel);
				}
			}
		} else {
			modelsToAdd.addAll(models);
		}

		// if need to add some models
		if (modelsToAdd.size() > 0) {
			dbManager.insertModels(modelsToAdd);
			message = handler.obtainMessage(0, "Новые модели были добавлены!");
			handler.sendMessage(message);

			for (SaluteModel model : modelsToAdd) {

				String pictureFile = model.getPictureFile();
				if (pictureFile != null && !pictureFile.equals("")) {
					dbManager.storeFileForDownload(pictureFile, FileType.FILETYPE_PICTURE, model.getPictureMD5());
				}
				String iconFile = model.getPreviewFile();
				if (iconFile != null && !iconFile.equals("")) {
					dbManager.storeFileForDownload(iconFile, FileType.FILETYPE_ICON, model.getPreviewMD5());
				}
				String videoFile = model.getVideoFile();
				if (videoFile != null && !videoFile.equals("")) {
					dbManager.storeFileForDownload(videoFile, FileType.FILETYPE_VIDEO, model.getVideoMD5());
				}
			}

		} else {
			message = handler.obtainMessage(0, "Модели не требуют обновления!");
			handler.sendMessage(message);
		}

		message = handler.obtainMessage(1, "Синхронизация завершена!");
		handler.sendMessage(message);

		Log.i(TAG, "Parsing success!");

	}

	@Override
	public void onParsingError(String message) {
		// TODO Auto-generated method stub
		Message errorMessage = handler.obtainMessage(1, message);
		handler.sendMessage(errorMessage);
	}

}
