package com.syndarin.erdi;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.syndarin.erdi.adapters.CategoriesListAdapter;
import com.syndarin.erdi.db.DatabaseManager;
import com.syndarin.erdi.dialogs.DownloadDialog;
import com.syndarin.erdi.dialogs.SettingsDialog;
import com.syndarin.erdi.dialogs.StoreNumberDialog;
import com.syndarin.erdi.entities.SaluteModel;
import com.syndarin.erdi.events.OnDetailedInfoRequestListener;
import com.syndarin.erdi.events.OnDetailedViewTransition;
import com.syndarin.erdi.events.OnDialogCustomEventListener;
import com.syndarin.erdi.fragments.DetailFragment;
import com.syndarin.erdi.fragments.GoodsFragment;
import com.syndarin.erdi.synchronizer.DownloadManger;
import com.syndarin.erdi.synchronizer.PictureDownloader;
import com.syndarin.erdi.synchronizer.PreviewDownloader;
import com.syndarin.erdi.synchronizer.SynchronizeManager;
import com.syndarin.erdi.synchronizer.VideoDownloader;
import com.syndarin.erdi.utilities.InternetChecker;

public class ERDIActivity extends FragmentActivity implements
		OnDialogCustomEventListener, android.view.View.OnClickListener,
		OnItemClickListener, OnDetailedInfoRequestListener,
		OnDetailedViewTransition, OnLongClickListener {
	/** Called when the activity is first created. */

	// ===========================================================
	// CONSTANTS
	// ===========================================================
	private final static int MODE = Activity.MODE_PRIVATE;
	private final static String PREFERENCES = "ERDIPreferences";
	

	public static String STORAGE_ROOT;
	private final static int VIEW_GRID=1;
	private final static int VIEW_FAVOURITE=3;

	// ===========================================================
	// ATTRIBUTES
	// ===========================================================

	// ===== RESOURCES & PREFEREBCES =====
	private SharedPreferences preferences;
	private Resources resources;
	private DatabaseManager dbManager;

	// ===== OTHER VARS =====
	private ImageButton buttonSortPrice;
	private ImageButton buttonSortTime;
	private ImageButton buttonSortShots;
	private Integer storeNumber;
	private TextView statusText;
	private TextView functionOnRun;
	private ListView categoriesList;
	private long currentCategorySelected;
	private ArrayList<SaluteModel> currentModelsSet;
	private int currentViewFragment;
	private int savedPage;
	private PowerManager.WakeLock wakeLock;
	private String sortType;

	// ===========================================================
	// METHODS
	// ===========================================================
	public boolean initializeExternalStorage() {
		boolean result = true;
		File storageDir = new File("/mnt/flash/erdi");
		if (!storageDir.exists())
			result = storageDir.mkdir();
		File videoDir = new File("/mnt/flash/erdi"+VideoDownloader.LOCAL_PATH);
		if (!videoDir.exists())
			result = videoDir.mkdir();
		File pictureDir = new File("/mnt/flash/erdi"+PictureDownloader.LOCAL_PATH);
		if (!pictureDir.exists())
			result = pictureDir.mkdir();
		File previewDir = new File("/mnt/flash/erdi"+PreviewDownloader.LOCAL_PATH);
		if (!previewDir.exists())
			result = previewDir.mkdir();
		
		
		if(result){
			/* save base directory to shPr */
			SharedPreferences.Editor editor=this.preferences.edit();
			editor.putString("BASEDIR", "/mnt/flash/erdi");
			editor.commit();
			ERDIActivity.STORAGE_ROOT="/mnt/flash/erdi";
		}else{
			result=true;
			/* try to make storage on SD */
			String storeDir=Environment.getExternalStorageDirectory()+"/erdi";
			storageDir = new File(storeDir);
			if (!storageDir.exists())
				result = storageDir.mkdir();
			videoDir = new File(storeDir+VideoDownloader.LOCAL_PATH);
			if (!videoDir.exists())
				result = videoDir.mkdir();
			pictureDir = new File(storeDir+PictureDownloader.LOCAL_PATH);
			if (!pictureDir.exists())
				result = pictureDir.mkdir();
			previewDir = new File(storeDir+PreviewDownloader.LOCAL_PATH);
			if (!previewDir.exists())
				result = previewDir.mkdir();
			
			if(result){
				SharedPreferences.Editor editor=this.preferences.edit();
				editor.putString("BASEDIR", storeDir);
				editor.commit();
				ERDIActivity.STORAGE_ROOT=storeDir;
			}
		}
		
		return result;
	}

	private void runSynchronization() {
		this.functionOnRun.setText("Синхронизация");
		SynchronizeManager manager = new SynchronizeManager(this, this.handler,	storeNumber);
		Thread t = new Thread(manager);
		t.start();
	}

	private void loadCategories() {
		this.categoriesList.setAdapter(new CategoriesListAdapter(this.dbManager.getAllCategoriesFullInfo()));
	}

	private void loadModelsGridFragment(long categoryId, String sortType, int pageNo) {
		if (categoryId != 0) {
			if (sortType != null) {
				currentModelsSet = dbManager.getModelsByCategorySorted(categoryId, sortType);
			} else {
				currentModelsSet = dbManager.getModelsByCategory(categoryId);
			}
			String categoryDescription = this.dbManager.getCategoryDescription(categoryId);
			GoodsFragment goodsFragment = new GoodsFragment();
			goodsFragment.setModels(currentModelsSet);
			goodsFragment.setCurrentPage(pageNo);
			goodsFragment.setCategoryDescription(categoryDescription);
			goodsFragment.setOnDetailedInfoRequestListener(this);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fragment_container, goodsFragment);
			ft.commit();
			this.currentViewFragment=VIEW_GRID;
			this.currentCategorySelected=categoryId;
		} else {
			String message = this.resources.getString(R.string.messageNoCategorySelected);
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}

	}

	private void showModelDetailed(long modelId) {
		SaluteModel model = this.dbManager.getModelById(modelId);
		DetailFragment detailFragment = new DetailFragment();
		detailFragment.setOnDetailedViewTransition(this);
		detailFragment.setModel(model);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		//ft.addToBackStack(null);
		ft.replace(R.id.fragment_container, detailFragment);
		ft.commit();
		//this.currentViewFragment=VIEW_DETAIL;
	}

	// ===========================================================
	// OVERRIDEN METHODS
	// ===========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.initializeWidgets();
		
//		PowerManager pm=(PowerManager)this.getSystemService(Context.POWER_SERVICE);
//		this.wakeLock=pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "ERDI_LOCK");
//		this.wakeLock.acquire();

		this.dbManager = new DatabaseManager(this);

		this.resources = getResources();

		this.currentCategorySelected = 0;

		this.preferences = getSharedPreferences(PREFERENCES, MODE);
		if (preferences != null) {
			this.storeNumber = preferences.getInt("STORE_NUMBER", 0);
			if (this.storeNumber == 0) {
				StoreNumberDialog dialog = new StoreNumberDialog(this, this);
				dialog.show();
			} else {
				ERDIActivity.STORAGE_ROOT=preferences.getString("BASEDIR", null);
				if(ERDIActivity.STORAGE_ROOT==null){
					if(this.initializeExternalStorage()){
						loadCategories();
					}else{
						String message = getResources().getString(R.string.createExternalStorageError);
						Toast.makeText(this, message, Toast.LENGTH_LONG).show();
						finish();
					}
				}else{
					this.loadCategories();
					this.checkForDownloads();
				}
			}
		} else {
			String message = resources.getString(R.string.messagePreferencesNotFound);
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int sender = v.getId();
		switch (sender) {
		
		case R.id.buttonSortPrice:
			if(this.currentViewFragment==VIEW_GRID){
				if(this.sortType==null){
					this.buttonSortPrice.setImageResource(R.drawable.sort_price_up);
					this.sortType=DatabaseManager.ORDER_BY_PRICE_ASC;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}else if(this.sortType.equals(DatabaseManager.ORDER_BY_PRICE_ASC)){
					this.buttonSortPrice.setImageResource(R.drawable.sort_price_down);
					this.sortType=DatabaseManager.ORDER_BY_PRICE_DESC;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}else if(this.sortType.equals(DatabaseManager.ORDER_BY_PRICE_DESC)){
					this.buttonSortPrice.setImageResource(R.drawable.price_circle);
					this.sortType=null;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}else{
					this.skipSorting();
					this.buttonSortPrice.setImageResource(R.drawable.sort_price_up);
					this.sortType=DatabaseManager.ORDER_BY_PRICE_ASC;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}
			}
			
			break;
		case R.id.buttonSortTime:
			if(this.currentViewFragment==VIEW_GRID){
				if(this.sortType==null){
					this.buttonSortTime.setImageResource(R.drawable.sort_time_up);
					this.sortType=DatabaseManager.ORDER_BY_TIME_ASC;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}else if(this.sortType.equals(DatabaseManager.ORDER_BY_TIME_ASC)){
					this.buttonSortTime.setImageResource(R.drawable.sort_time_down);
					this.sortType=DatabaseManager.ORDER_BY_TIME_DESC;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}else if(this.sortType.equals(DatabaseManager.ORDER_BY_TIME_DESC)){
					this.buttonSortTime.setImageResource(R.drawable.time_circle);
					this.sortType=null;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}else{
					this.skipSorting();
					this.buttonSortTime.setImageResource(R.drawable.sort_time_up);
					this.sortType=DatabaseManager.ORDER_BY_TIME_ASC;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}
			}
			break;
		case R.id.buttonSortShots:
			
			if(this.currentViewFragment==VIEW_GRID){
				if(this.sortType==null){
					this.buttonSortShots.setImageResource(R.drawable.sort_shots_up);
					this.sortType=DatabaseManager.ORDER_BY_SHOTS_ASC;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}else if(this.sortType.equals(DatabaseManager.ORDER_BY_SHOTS_ASC)){
					this.buttonSortShots.setImageResource(R.drawable.sort_shots_down);
					this.sortType=DatabaseManager.ORDER_BY_SHOTS_DESC;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}else if(this.sortType.equals(DatabaseManager.ORDER_BY_SHOTS_DESC)){
					this.buttonSortShots.setImageResource(R.drawable.shots_circle);
					this.sortType=null;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}else{
					this.skipSorting();
					this.buttonSortShots.setImageResource(R.drawable.sort_shots_up);
					this.sortType=DatabaseManager.ORDER_BY_SHOTS_ASC;
					this.loadModelsGridFragment(currentCategorySelected, this.sortType, 0);
				}
			}
			
			break;

		case R.id.buttonFavourites:
			skipSorting();
			ArrayList<SaluteModel> favouriteModels = this.dbManager.getAllFavouriteModels();
			if (favouriteModels.size() > 0) {
				GoodsFragment goodsFragment = new GoodsFragment();
				goodsFragment.setModels(favouriteModels);
				goodsFragment.setCategoryDescription("Избранное");
				goodsFragment.setOnDetailedInfoRequestListener(this);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, goodsFragment);
				ft.commit();
				this.currentViewFragment=VIEW_FAVOURITE;
				this.currentModelsSet=favouriteModels;
			} else {
				String message=this.resources.getString(R.string.messageNoModelsFavourite);
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}

			break;

		case R.id.buttonSettings:
			SettingsDialog settingsDialog=new SettingsDialog(this, this);
			settingsDialog.show();
			break;
		default:
			break;
		}

	}
	
	@Override
	public void onRunSynchronization() {
		// TODO Auto-generated method stub
		if(InternetChecker.isOnline(this)){
			this.runSynchronization();
		}else{
			String message=this.getResources().getString(R.string.messageNoInternetConnection);
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}		
	}

	@Override
	public void onRunDownload() {
		// TODO Auto-generated method stub
		this.runDownloadManager();
	}
	
	@Override
	public void startDownload() {
		// TODO Auto-generated method stub
		this.runDownloadManager();
	}
	
	private void runDownloadManager(){
		if(this.dbManager.isFilesToDownload()){
			if(InternetChecker.isOnline(this)){
				this.functionOnRun.setText("Менеджер загрузки");
				DownloadManger dm=new DownloadManger(this, handler);
				Thread t=new Thread(dm);
				t.start();
			}else{
				String message=this.getResources().getString(R.string.messageNoInternetConnection);
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}	
		}else{
			Toast.makeText(this, "Нет файлов, ожидающих загрузки!", Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public void onDetailedInfoRequest(long modelId, int pageNo) {
		// TODO Auto-generated method stub
		this.showModelDetailed(modelId);
		this.savedPage=pageNo;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		this.loadModelsGridFragment(arg3, null, 0);
		this.currentCategorySelected = arg3;
		skipSorting();
	}
	
	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		this.dbManager.skipFavourites();
		String message=this.resources.getString(R.string.messageFavouritesDeleted);
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		if(currentViewFragment==VIEW_FAVOURITE){
			this.loadModelsGridFragment(1, null, 0);
		}
		return true;
	}

	@Override
	public void onEnterStoreNumber(int storeNumber) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor preferencesEditor = this.preferences.edit();
		preferencesEditor.putInt("STORE_NUMBER", storeNumber);
		preferencesEditor.commit();

		if (initializeExternalStorage()) {
			if (InternetChecker.isOnline(this)) {
				runSynchronization();
			} else {
				String message=this.resources.getString(R.string.messageNoInternetConnection);
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}

		} else {
			String message = getResources().getString(R.string.createExternalStorageError);
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			finish();
		}

	}
	
	@Override
	public void detailedBack() {
		// TODO Auto-generated method stub
		if(this.currentViewFragment==VIEW_GRID){
			this.loadModelsGridFragment(this.currentCategorySelected, this.sortType, this.savedPage);
		}else if(this.currentViewFragment==VIEW_FAVOURITE){
			skipSorting();
			ArrayList<SaluteModel> favouriteModels = this.dbManager.getAllFavouriteModels();
			if (favouriteModels.size() > 0) {
				GoodsFragment goodsFragment = new GoodsFragment();
				goodsFragment.setModels(favouriteModels);
				goodsFragment.setCurrentPage(savedPage);
				goodsFragment.setCategoryDescription("Избранное");
				goodsFragment.setOnDetailedInfoRequestListener(this);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, goodsFragment);
				ft.commit();
				this.currentViewFragment=VIEW_FAVOURITE;
				this.currentModelsSet=favouriteModels;
			} else {
				String message=this.resources.getString(R.string.messageNoModelsFavourite);
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	private void checkForDownloads(){
		if(this.dbManager.isFilesToDownload()){
			DownloadDialog dDialog=new DownloadDialog(this, this);
			dDialog.show();
		}
	}

	@Override
	public void detailedMoveToNext(SaluteModel model) {
		// TODO Auto-generated method stub
		if (currentModelsSet.contains(model)) {
			int index = currentModelsSet.indexOf(model);
			if (++index != currentModelsSet.size()) {
				this.showModelDetailed(currentModelsSet.get(index).getId());
			} else {
				String message = this.resources.getString(R.string.detailedNavigationErrorLast);
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}
		} else {
			String message = this.resources.getString(R.string.detailedNavigationError);
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void detailedMoveToPrevious(SaluteModel model) {
		// TODO Auto-generated method stub
		if (currentModelsSet.contains(model)) {
			int index = currentModelsSet.indexOf(model);
			if (--index != -1) {
				this.showModelDetailed(currentModelsSet.get(index).getId());
			} else {
				String message = this.resources.getString(R.string.detailedNavigationErrorFirst);
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}
		} else {
			String message = this.resources.getString(R.string.detailedNavigationError);
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
	}
	
	private void skipSorting(){
		this.sortType=null;
		this.buttonSortPrice.setImageResource(R.drawable.price_circle);
		this.buttonSortTime.setImageResource(R.drawable.time_circle);
		this.buttonSortShots.setImageResource(R.drawable.shots_circle);
	}

	private void initializeWidgets() {
		ImageButton buttonFavourites = (ImageButton) findViewById(R.id.buttonFavourites);
		buttonFavourites.setOnClickListener(this);
		buttonFavourites.setOnLongClickListener(this);

		ImageButton buttonSettings = (ImageButton) findViewById(R.id.buttonSettings);
		buttonSettings.setOnClickListener(this);

		buttonSortPrice = (ImageButton) findViewById(R.id.buttonSortPrice);
		buttonSortPrice.setOnClickListener(this);

		buttonSortTime = (ImageButton) findViewById(R.id.buttonSortTime);
		buttonSortTime.setOnClickListener(this);

		buttonSortShots = (ImageButton) findViewById(R.id.buttonSortShots);
		buttonSortShots.setOnClickListener(this);

		this.statusText = (TextView) findViewById(R.id.statusText);
		this.functionOnRun=(TextView)findViewById(R.id.functionOnRun);

		this.categoriesList = (ListView) findViewById(R.id.catalogue_list);
		this.categoriesList.setOnItemClickListener(this);
		this.categoriesList.setSelector(new ColorDrawable(Color.TRANSPARENT));
	}

	// ===============================================
	// INNER CLASSES
	// ===============================================
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 0) {
				statusText.setText((String) msg.obj);
			} else {
				statusText.setText((String) msg.obj);
				loadCategories();
				checkForDownloads();
			}

		}

	};
}