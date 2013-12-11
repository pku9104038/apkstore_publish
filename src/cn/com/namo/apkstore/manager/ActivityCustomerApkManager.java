/**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author wangpeifeng
 *
 */
public class ActivityCustomerApkManager extends Activity{
    /////////////////////////////////////////////////
    // PROPERTIES, PRIVATE
    /////////////////////////////////////////////////
	private Context context;
	private String strApkDir;
	
	private ListView listView;
	private ArrayList<ApkInfo> listApkInfo;
	private ListAdaptorApkManager adaptor;
	private ProgressBar progressBar;
	private WakeLock wakeLock;

    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////
	
	public static final String DIR_REPO					= "/repo";
	
	private static final int HANDLER_MSG_APKINFO_UPDATED	= 1;
	private static final int HANDLER_MSG_TOAST				= 1 + HANDLER_MSG_APKINFO_UPDATED;
	private static final int HANDLER_MSG_APKINFO_SYNC		= 1 + HANDLER_MSG_TOAST;
	private static final int HANDLER_MSG_APP_ADD			= 1 + HANDLER_MSG_APKINFO_SYNC;
	private static final int HANDLER_MSG_ICON_UPLOAD		= 1 + HANDLER_MSG_APP_ADD;
	private static final int HANDLER_MSG_APK_ADD			= 1 + HANDLER_MSG_ICON_UPLOAD;
	private static final int HANDLER_MSG_APK_UPLOAD			= 1 + HANDLER_MSG_APK_ADD;
	private static final int HANDLER_MSG_ICON_UPDATE		= 1 + HANDLER_MSG_APK_UPLOAD;
	private static final int HANDLER_MSG_APK_UPLOADED		= 1 + HANDLER_MSG_ICON_UPDATE;
	private static final int HANDLER_MSG_NO_APK				= 1 + HANDLER_MSG_APK_UPLOADED;
	
	private static final String HANDLER_DATA_TOAST_TEXT		= "toast_text";
	private static final String HANDLER_DATA_APK_INDEX		= "apk_index";
	private static final String HANDLER_DATA_CATEGORY_SERIAL= "category_serial";
	private static final String HANDLER_DATA_SUPPLIER_SERIAL= "supplier_serial";
	
	public static final String INTENT_EXTRA_INDEX			= "index";
	public static final String INTENT_EXTRA_VALUE_SELECTED	= "value_selected";
	private static final String INTENT_EXTRA_OPTIONS		= "options";
	private static final String INTENT_EXTRA_VALUE_NAME		= "value_name";
	private static final String INTENT_EXTRA_TEXT_NAME		= "text_name";
	private static final String INTENT_EXTRA_TITLE			= "title";
	
	private static final int ACTIVITY_UPLOAD_APK_FILE		= 11;
	
    /////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
	
    /////////////////////////////////////////////////
    // METHODS, CREATE
    /////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(this.getClass().getName(), "customer_serial:"+StaticData.customer_serial+
				", brand_serial:"+StaticData.brand_serial+",model_serial:"+StaticData.model_serial);
		context = this;
		
		setContentView(R.layout.apklist);

		String verCode = "";
		try {
			PackageManager packageManager =context.getPackageManager();  
			PackageInfo appInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			verCode = "_"+appInfo.versionCode;
			verCode = "_"+appInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TextView tvTitle = (TextView)findViewById(R.id.textViewTitle);
		//tvTitle.setText(R.string.app_name);
		//tvTitle.append(verCode);
		tvTitle.setText(StaticData.brand);
		tvTitle.append(":");
		tvTitle.append(StaticData.model);
				
		ImageButton ibtnRefresh = (ImageButton)findViewById(R.id.imageButtonRefresh);
		ibtnRefresh.setOnClickListener(lsrButtonRefresh);

		initDir();
		int newapks = fetchApkInfos();
		
			listView = (ListView)findViewById(R.id.listView);
			adaptor =  new ListAdaptorApkManager(context,this.listApkInfo,onApkActionSelected);
			listView.setAdapter(adaptor);
		
			progressBar = (ProgressBar)findViewById(R.id.progressBar);
			progressBar.setVisibility(View.INVISIBLE);
		
			if(newapks>0){
				handler.sendEmptyMessage(HANDLER_MSG_APKINFO_SYNC);
			}
			else{
				handler.sendEmptyMessage(HANDLER_MSG_NO_APK);
			}
			PowerManager powerManager = (PowerManager) this
                .getSystemService(Service.POWER_SERVICE);
        
			wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "ApkStoreManager");
       	
			wakeLock.setReferenceCounted(false);
		
		
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		wakeLock.release();
		super.onPause();
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
    	wakeLock.acquire();
		super.onResume();
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



	private void initDir(){
		File download = 
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		if(!download.exists()){
			download.mkdirs();
		}
		this.strApkDir = download.getAbsoluteFile() + DIR_REPO;
		File repo = new File(this.strApkDir);
		if(!repo.exists()){
			repo.mkdir();
		}
		
	}
	
	
	private int fetchApkInfos(){
		ArrayList<String> listApkFiles = new ArrayList<String>();
		listApkFiles = ApkInfo.fetchApkFiles(listApkFiles
				, this.strApkDir
				, false);
		
		this.listApkInfo = new ArrayList<ApkInfo>();
		Iterator<String> iterator = listApkFiles.iterator();
		while(iterator.hasNext()){
			String strApkFile = iterator.next();
			Log.i("fetchApkInfos", strApkFile);
			ApkInfo apkinfo = new ApkInfo(this.context, strApkFile);
			if(apkinfo.getPackage() != null){
				this.listApkInfo.add(apkinfo);
			}
		}
		return this.listApkInfo.size();
	}
	
    /////////////////////////////////////////////////
    // METHODS, ACTION
    /////////////////////////////////////////////////
	
	private OnApkActionSelected onApkActionSelected = new OnApkActionSelected(){

		@Override
		public void onApkAction(int index) {
			// TODO Auto-generated method stub
//			WebApi webApi;
//			TaskFileUpload taskUpload;
			ApkInfo apkinfo = listApkInfo.get(index);
			Bundle bundle = new Bundle();
			bundle.putInt(HANDLER_DATA_APK_INDEX, index);
			Message msg = new Message();
			msg.setData(bundle);
//			Intent intent;
			switch(apkinfo.getOnlineState()){
			case ApkInfo.ONLINE_STATE_UNKNOWN:
				// add application info and upload icon file
				msg.what = HANDLER_MSG_APP_ADD;
				handler.sendMessage(msg);
				break;
				
			case ApkInfo.ONLINE_STATE_APP_ONLINE:
				// add apkfile info 
				if(StaticData.role_id==7){
					WebApi webApi = new WebApi(context);
					onHttpRespApkAdd.setApkIndex(index);
					webApi.setOnHttpResponse(onHttpRespApkAdd);
					webApi.addApkinfo(
							apkinfo.getFileName(), 
							apkinfo.getPackage(),
							apkinfo.getVerCode(),
							apkinfo.getVerName(),
							apkinfo.getMiniSDK(),
							StaticData.supplier_serial,
							StaticData.customer_serial,
							StaticData.brand_serial,
							StaticData.model_serial
							);
				}
				else{
					msg.what = HANDLER_MSG_APK_ADD;
					handler.sendMessage(msg);
				}
				break;
				
			case ApkInfo.ONLINE_STATE_APK_ONLINE:
				// upload apkfile
				msg.what = HANDLER_MSG_APK_UPLOAD;
/*				
				taskUpload = new TaskFileUpload(context, 
						apkinfo.getFilePath(),
						WebApi.API_ROOT + WebApi.API_MANAGER_APK_UPLOAD
						);
				taskUpload.setOnHttpResponse(onHttpRespFileUpload);
				taskUpload.execute();
				*/
				handler.sendMessage(msg);
				break;
				
			case ApkInfo.ONLINE_STATE_APK_UPLOADED:
				// update icon file 
				msg.what = HANDLER_MSG_APK_UPLOADED;
				
				TaskFileUpload taskUpload = new TaskFileUpload(context, 
						apkinfo.getIconFilePath(),
						WebApi.API_ROOT + WebApi.API_MANAGER_ICON_UPLOAD
						,0
						);
				taskUpload.setOnHttpResponse(onHttpRespIconUpload);
				taskUpload.execute();
				handler.sendMessage(msg);
				
				break;
			case ApkInfo.ONLINE_STATE_ICON_UPLOAD:
				// update icon file 
				msg.what = HANDLER_MSG_ICON_UPDATE;
/*				
				taskUpload = new TaskFileUpload(context, 
						apkinfo.getIconFilePath(),
						WebApi.API_ROOT + WebApi.API_MANAGER_ICON_UPLOAD
						);
				taskUpload.setOnHttpResponse(onHttpRespFileUpload);
				taskUpload.execute();
*/				
				handler.sendMessage(msg);
				break;
			}
			

		}
		
	};

	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
		case ApkInfo.ONLINE_STATE_UNKNOWN:
			if(resultCode == Activity.RESULT_OK && data != null){
				int index = data.getIntExtra(ActivityRadioSelect.INTENT_EXTRA_INDEX, -1);
				int category_serial = data.getIntExtra(ActivityRadioSelect.INTENT_EXTRA_VALUE_SELECTED, -1);
				if (index > -1 && category_serial > 0 ){
					ApkInfo apkinfo = listApkInfo.get(index);
					WebApi webApi = new WebApi(context);
					onHttpRespAppAdd.setApkIndex(index);
					webApi.setOnHttpResponse(onHttpRespAppAdd);
					webApi.addApplication(
							apkinfo.getLabel(), 
							apkinfo.getPackage(), 
							apkinfo.getIconFileName(),
							category_serial,
							StaticData.customer_serial,
							StaticData.brand_serial,
							StaticData.model_serial
							);
					
					TaskFileUpload taskUpload = new TaskFileUpload(context, 
							apkinfo.getIconFilePath(),
							WebApi.API_ROOT + WebApi.API_MANAGER_ICON_UPLOAD,
							index);
					taskUpload.setOnHttpResponse(onHttpRespIconUpload);
					taskUpload.execute();
					
				}
			}
			break;
			
		case ApkInfo.ONLINE_STATE_APP_ONLINE:
			if(resultCode == Activity.RESULT_OK && data != null){
				int index = data.getIntExtra(ActivityRadioSelect.INTENT_EXTRA_INDEX, -1);
				int supplier_serial = data.getIntExtra(ActivityRadioSelect.INTENT_EXTRA_VALUE_SELECTED, -1);
				if (index > -1 && supplier_serial > 0 ){
					ApkInfo apkinfo = listApkInfo.get(index);
					WebApi webApi = new WebApi(context);
					onHttpRespApkAdd.setApkIndex(index);
					webApi.setOnHttpResponse(onHttpRespApkAdd);
					webApi.addApkinfo(
							apkinfo.getFileName(), 
							apkinfo.getPackage(),
							apkinfo.getVerCode(),
							apkinfo.getVerName(),
							apkinfo.getMiniSDK(),
							supplier_serial,
							StaticData.customer_serial,
							StaticData.brand_serial,
							StaticData.model_serial
							);
					Log.i("addApkinfo",apkinfo.getFileName());
/*					
					TaskFileUpload taskUpload = new TaskFileUpload(context, 
							apkinfo.getFilePath(),
							WebApi.API_ROOT + WebApi.API_MANAGER_APK_UPLOAD
							);
					taskUpload.setOnHttpResponse(onHttpRespFileUpload);
					taskUpload.execute();
					*/
				}
			}
			
			break;
		case ApkInfo.ONLINE_STATE_APK_ONLINE:
			if(resultCode == Activity.RESULT_OK && data != null){
				int index = data.getIntExtra(SocketUploadActivity.INTENT_EXTRA_APK_INDEX, -1);
				Log.i("uploaded index", ""+index);
				if (index > -1 ){
					ApkInfo apkinfo = listApkInfo.get(index);
					WebApi webApi = new WebApi(context);
					onHttpRespApkFileCopy.setApkIndex(index);
					webApi.setOnHttpResponse(onHttpRespApkFileCopy);
					webApi.copyApkFile(
							apkinfo.getFileName(), 
							apkinfo.getPackage(),
							apkinfo.getVerCode()
							);
					Log.i("addApkinfo",apkinfo.getFileName());
					/*					
/*					
					TaskFileUpload taskUpload = new TaskFileUpload(context, 
							apkinfo.getFilePath(),
							WebApi.API_ROOT + WebApi.API_MANAGER_APK_UPLOAD
							);
					taskUpload.setOnHttpResponse(onHttpRespFileUpload);
					taskUpload.execute();
					*/
				}
			}
			
			break;
		}
	}

	private OnClickListener lsrButtonRefresh = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			syncApkInfos();
			
		}
		
	};
	
	private void syncApkInfos(){
		progressBar.setVisibility(View.VISIBLE);
		WebApi webapi = new WebApi(context);
		webapi.setOnHttpResponse(onHttpRespSyncInfo);
		webapi.syncCustomerApkInfos(listApkInfo,StaticData.customer_serial, StaticData.brand_serial,StaticData.model_serial);
	}
	
	private void queryCategory(){
		WebApi webapi = new WebApi(context);
		webapi.setOnHttpResponse(this.onHttpRespCategoryQuery);
		webapi.queryCategory();
	}
	
	private void querySupplier(){
		WebApi webapi = new WebApi(context);
		webapi.setOnHttpResponse(this.onHttpRespSupplierQuery);
		webapi.querySupplier();
	}
	
	private void updateApkState(String response){
		try {
			//JSONObject obj = new JSONObject(response);
			JSONArray array;// = obj.getJSONArray(WebApi.API_PARAM_APKINFO_LIST);
			array = new JSONArray(response);
			if(array != null){
				for(int i = 0; i<array.length(); i++){
					JSONObject jsonapk = array.getJSONObject(i);
					int index = jsonapk.getInt(WebApi.API_PARAM_APKINFO_INDEX);
					int state = jsonapk.getInt(WebApi.API_PARAM_APKINFO_STATE);
					this.listApkInfo.get(index).setOnlineState(state);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private Intent makeOptionSelectIntent(int index, String title, String options, String value_name, String text_name){
		Intent intent = new Intent();
		intent.setClass(context, ActivityRadioSelect.class);
		intent.putExtra(ActivityRadioSelect.INTENT_EXTRA_INDEX, index);
		intent.putExtra(ActivityRadioSelect.INTENT_EXTRA_OPTIONS, options);
		intent.putExtra(ActivityRadioSelect.INTENT_EXTRA_VALUE_NAME, value_name);
		intent.putExtra(ActivityRadioSelect.INTENT_EXTRA_TEXT_NAME, text_name);
		intent.putExtra(ActivityRadioSelect.INTENT_EXTRA_TITLE, title);
	
		return intent;
	}

	private Intent makeUploatFiletIntent(int index, String path){
		Intent intent = new Intent();
		intent.setClass(context, SocketUploadActivity.class);
		intent.putExtra(SocketUploadActivity.INTENT_EXTRA_APK_INDEX, index);
		intent.putExtra(SocketUploadActivity.INTENT_EXTRA_FILE_PATH, path);
		intent.putExtra(SocketUploadActivity.INTENT_EXTRA_HOST, PrefProxy.getHost(context));
		intent.putExtra(SocketUploadActivity.INTENT_EXTRA_PORT, 
				PrefProxy.getSocketPort(context));
		
	
		return intent;
	}
	
	private OnHttpResponse onHttpRespCategoryQuery = new OnHttpResponse(){

		@Override
		public void onHttpResponse(String response) {
			// TODO Auto-generated method stub
			if (WebApi.isRespSuccess(response)){
				JSONArray array = WebApi.getRespArray(response);
				if (array != null){
					PrefProxy.setCategoryList(context, array.toString());
				}
			}
		}
		
	};
	
	private OnHttpResponse onHttpRespSupplierQuery = new OnHttpResponse(){

		@Override
		public void onHttpResponse(String response) {
			// TODO Auto-generated method stub
			if (WebApi.isRespSuccess(response)){
				JSONArray array = WebApi.getRespArray(response);
				if (array != null){
					PrefProxy.setSupplierList(context, array.toString());
				}
			}
		}
		
	};
	
	private OnHttpResponse onHttpRespSyncInfo = new OnHttpResponse(){

		@Override
		public void onHttpResponse(String response) {
			// TODO Auto-generated method stub
			if (WebApi.isRespSuccess(response)){
				String str = WebApi.getRespMsg(response);
				if (str != WebApi.API_RESP_MSG_NULL){
					updateApkState(str);
					handler.sendEmptyMessage(HANDLER_MSG_APKINFO_UPDATED);
				}
			}
		}
		
	};
	
	private OnHttpResponse onHttpRespAppAdd = new OnHttpResponse(){

		@Override
		public void onHttpResponse(String response) {
			// TODO Auto-generated method stub
			if(WebApi.isRespSuccess(response)){
				handler.sendEmptyMessage(HANDLER_MSG_APKINFO_SYNC);
				Bundle bundle = new Bundle();
				bundle.putInt(HANDLER_DATA_APK_INDEX, this.getApkIndex());
				Message msg =new Message();
				msg.setData(bundle);
				msg.what = HANDLER_MSG_APK_ADD;
				handler.sendMessage(msg);
				
			}
			String resp_msg = WebApi.getRespMsg(response);
			Message msg = new Message();
			msg.what = HANDLER_MSG_TOAST;
			Bundle bundle = new Bundle();
			bundle.putString(HANDLER_DATA_TOAST_TEXT, resp_msg);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
		
	};
	
	private OnHttpResponse onHttpRespApkAdd = new OnHttpResponse(){

		@Override
		public void onHttpResponse(String response) {
			// TODO Auto-generated method stub
			if(WebApi.isRespSuccess(response)){
				handler.sendEmptyMessage(HANDLER_MSG_APKINFO_SYNC);
				Bundle bundle = new Bundle();
				bundle.putInt(HANDLER_DATA_APK_INDEX, this.getApkIndex());
				Message msg =new Message();
				msg.setData(bundle);
				msg.what = HANDLER_MSG_APK_UPLOAD;
				handler.sendMessage(msg);
				
			}
			String resp_msg = WebApi.getRespMsg(response);
			Message msg = new Message();
			msg.what = HANDLER_MSG_TOAST;
			Bundle bundle = new Bundle();
			bundle.putString(HANDLER_DATA_TOAST_TEXT, resp_msg);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
		
	};

	private OnHttpResponse onHttpRespApkFileCopy = new OnHttpResponse(){

		@Override
		public void onHttpResponse(String response) {
			// TODO Auto-generated method stub
			if(WebApi.isRespSuccess(response)){
				handler.sendEmptyMessage(HANDLER_MSG_APKINFO_SYNC);
			}
			String resp_msg = WebApi.getRespMsg(response);
			Message msg = new Message();
			msg.what = HANDLER_MSG_TOAST;
			Bundle bundle = new Bundle();
			bundle.putString(HANDLER_DATA_TOAST_TEXT, resp_msg);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
		
	};

	private OnHttpResponse onHttpRespIconUpload = new OnHttpResponse(){

		@Override
		public void onHttpResponse(String response) {
			// TODO Auto-generated method stub
			if(WebApi.isRespSuccess(response)){
//				handler.sendEmptyMessage(HANDLER_MSG_APKINFO_SYNC);
			}
			String resp_msg = WebApi.getRespMsg(response);
			Message msg = new Message();
			msg.what = HANDLER_MSG_TOAST;
			Bundle bundle = new Bundle();
			bundle.putString(HANDLER_DATA_TOAST_TEXT, resp_msg);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
		
	};

	private OnHttpResponse onHttpRespApkUpload = new OnHttpResponse(){

		@Override
		public void onHttpResponse(String response) {
			// TODO Auto-generated method stub
			/* upload file and copy update info in one api now
			if(WebApi.isRespSuccess(response)){
				//handler.sendEmptyMessage(HANDLER_MSG_APKINFO_SYNC);
				int index = this.getApkIndex();
				ApkInfo apkinfo = listApkInfo.get(index);
				WebApi webApi = new WebApi(context);
				onHttpRespApkFileCopy.setApkIndex(index);
				webApi.setOnHttpResponse(onHttpRespApkFileCopy);
				webApi.copyApkFile(
						apkinfo.getFileName(), 
						apkinfo.getPackage(),
						apkinfo.getVerCode()
						);
				
			}
			*/
			
			String resp_msg = WebApi.getRespMsg(response);
			Message msg = new Message();
			msg.what = HANDLER_MSG_TOAST;
			Bundle bundle = new Bundle();
			bundle.putString(HANDLER_DATA_TOAST_TEXT, resp_msg);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
		
	};
	
	
	private Handler handler = new Handler(){

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			TaskFileUpload taskUpload;
			int index = -1;
			Intent intent;
			super.handleMessage(msg);
			switch(msg.what){
			case HANDLER_MSG_APKINFO_SYNC:
				queryCategory();
				querySupplier();
				syncApkInfos();
				
				break;
			case HANDLER_MSG_APKINFO_UPDATED:
				adaptor.notifyDataSetChanged();
				listView.invalidate();
				progressBar.setVisibility(View.INVISIBLE);
				
				break;
			case HANDLER_MSG_TOAST:
				Toast.makeText(context, msg.getData().getString(HANDLER_DATA_TOAST_TEXT), Toast.LENGTH_LONG).show();
				syncApkInfos();
				break;
				
			case HANDLER_MSG_APP_ADD:
				if(StaticData.role_id == 7){
					Toast.makeText(context, "这是新增应用，必须先提交系统审核！", Toast.LENGTH_LONG).show();
				}
				else{
					index = msg.getData().getInt(HANDLER_DATA_APK_INDEX);
					intent = new Intent(context, ActivityCategorySelect.class);
					intent.putExtra(INTENT_EXTRA_INDEX, index);
					
					 ((Activity)context).startActivityForResult(
							intent,
							ApkInfo.ONLINE_STATE_UNKNOWN);
					
				}
				break;
				
			case HANDLER_MSG_ICON_UPLOAD:
				
				break;
			case HANDLER_MSG_APK_ADD:
				index = msg.getData().getInt(HANDLER_DATA_APK_INDEX);
				/*
				((ApkManagerActivity) context).startActivityForResult(
						makeOptionSelectIntent(index,
								//listApkInfo.get(index).getLabel(),
								"选择版本供应商:\n"+listApkInfo.get(index).getLabel(),
								PrefProxy.getSupplierList(context),
								ActivityRadioSelect.VALUE_NAME_SUPPLIER_SERIAL,
								ActivityRadioSelect.TEXT_NAME_SUPPLIER
								), 
						ApkInfo.ONLINE_STATE_APP_ONLINE);				
				*/
				intent = new Intent(context, ActivitySupplierSelect.class);
				intent.putExtra(INTENT_EXTRA_INDEX, index);
				
				((Activity)context).startActivityForResult(
						intent,
						ApkInfo.ONLINE_STATE_APP_ONLINE);
				
				break;
			case HANDLER_MSG_APK_UPLOAD:
				
				index = msg.getData().getInt(HANDLER_DATA_APK_INDEX);
/*
				taskUpload = new TaskFileUpload(context, 
						listApkInfo.get(index).getFilePath(),
						WebApi.API_ROOT + WebApi.API_MANAGER_APK_UPLOAD,
						index);
				onHttpRespApkUpload.setApkIndex(index);
				taskUpload.setOnHttpResponse(onHttpRespApkUpload);
				taskUpload.setPackageInfo(listApkInfo.get(index).getPackage(), 
						listApkInfo.get(index).getFileName(), 
						listApkInfo.get(index).getVerCode());
				
				taskUpload.execute();
*/				
				intent = new Intent(context, ActivityFileUpload.class);
				intent.putExtra(ActivityFileUpload.EXTRA_FILE_PATH, listApkInfo.get(index).getFilePath());
				intent.putExtra(ActivityFileUpload.EXTRA_UPLOAD_API, WebApi.API_ROOT + WebApi.API_MANAGER_APK_UPLOAD);
				intent.putExtra(ActivityFileUpload.EXTRA_PACKAGE, listApkInfo.get(index).getPackage());
				intent.putExtra(ActivityFileUpload.EXTRA_VERCODE, listApkInfo.get(index).getVerCode());
				intent.putExtra(ActivityFileUpload.EXTRA_FILE_NAME, listApkInfo.get(index).getFileName());
				
				onHttpRespApkUpload.setApkIndex(index);
				ActivityFileUpload.setOnHttpResponse(onHttpRespApkUpload);
				((Activity)context).startActivityForResult(
						intent,
						ACTIVITY_UPLOAD_APK_FILE);

				break;
			case HANDLER_MSG_ICON_UPDATE:
				index = msg.getData().getInt(HANDLER_DATA_APK_INDEX);
				taskUpload = new TaskFileUpload(context, 
						listApkInfo.get(index).getIconFilePath(),
						WebApi.API_ROOT + WebApi.API_MANAGER_ICON_UPLOAD,
						index
						);
				taskUpload.setOnHttpResponse(onHttpRespIconUpload);
				taskUpload.execute();
				
				break;
			case HANDLER_MSG_APK_UPLOADED:
				Toast.makeText(context, "Apk Online !", Toast.LENGTH_LONG).show();
				
				break;
			case HANDLER_MSG_NO_APK:
				Toast.makeText(context, "No Apk files in Download/repo Dir!", Toast.LENGTH_LONG).show();
				
				break;
			}
		}
		
	};
}
