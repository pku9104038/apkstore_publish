/**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * @author wangpeifeng
 *
 */
public class WebApi 
{
	
    /////////////////////////////////////////////////
    // PROPERTIES, PUBLIC
    /////////////////////////////////////////////////
	
	/////////////////////////////////////////////////
    // PROPERTIES, PROTECTED
    /////////////////////////////////////////////////

    /////////////////////////////////////////////////
    // PROPERTIES, PRIVATE
    /////////////////////////////////////////////////
    
	private OnHttpResponse onHttpResponse;
	
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////
	
	public static final String PROTOCOL_HTTP 						= "http://";
    
	/**
	 * API_ROOT
	 */
	public static final String API_ROOT 							= "/ApkStore/api/";
    
	
	//api script
	private static final String API_MANAGER_LOGIN 					= "manager_login.php";
	private static final String API_MANAGER_APP_ADD 				= "manager_app_add.php";
	public static final String API_MANAGER_ICON_UPLOAD 				= "manager_icon_upload.php";
	private static final String API_MANAGER_APK_ADD 				= "manager_apk_add.php";
	public static final String API_MANAGER_APK_UPLOAD 				= "manager_apk_upload.php";
	public static final String API_MANAGER_APK_FILE_COPY			= "manager_apk_file_copy.php";
	private static final String API_MANAGER_APKINFO_SYNC			= "manager_apkinfo_sync.php";
	private static final String API_MANAGER_CATEGORY_QUERY			= "manager_category_group_query.php";
	private static final String API_MANAGER_SUPPLIER_QUERY			= "manager_supplier_query.php";
	private static final String API_MANAGER_CUSTOMER_QUERY			= "manager_customer_query.php";
	private static final String API_MANAGER_BRAND_QUERY				= "manager_brand_query.php";
	private static final String API_MANAGER_MODEL_QUERY				= "manager_model_query.php";
	private static final String API_MANAGER_CUSTOMER_APKINFO_SYNC	= "manager_customer_apkinfo_sync.php";
	
	//api params
	private static final String API_PARAM_ACCOUNT 					= "account";
	private static final String API_PARAM_PASSWORD					= "password";
	
	private static final String API_PARAM_APPLICATION				= "application";
	private static final String API_PARAM_PACKAGE					= "package";
	private static final String API_PARAM_ICON						= "icon";
	public static final String API_PARAM_CATEGORY_SERIAL			= "category_serial";

	private static final String API_PARAM_FILE_ORIGINAL				= "file_original";
	private static final String API_PARAM_VERSION_CODE				= "version_code";
	private static final String API_PARAM_VERSION_NAME				= "version_name";
	private static final String API_PARAM_SDK_MIN					= "sdk_min";
	public static final String API_PARAM_SUPPLIER_SERIAL			= "supplier_serial";

	public static final String API_PARAM_APKINFO_LIST				= "apkinfo_list";
	public static final String API_PARAM_APKINFO_INDEX				= "index";
	private static final String API_PARAM_APKINFO					= "apkinfo";
	public static final String API_PARAM_APKINFO_STATE				= "online_state";
	
	public static final String API_PARAM_GROUP_SERIAL				= "group_serial";
	public static final String API_PARAM_GROUP_NAME					= "group_name";
	public static final String API_PARAM_CATEGORIES					= "categories";
	public static final String API_PARAM_CATEGORY					= "category";
//	public static final String API_PARAM_CATEGORY_SERIAL			= "category_serial";
	
	public static final String API_PARAM_SUPPLIER					= "supplier";
//	public static final String API_PARAM_SUPPLIER_SERIAL			= "supplier_serial";

	public static final String API_PARAM_CUSTOMER_SERIAL			= "customer_serial";
	public static final String API_PARAM_CUSTOMER					= "customer";
	public static final String API_PARAM_BRAND_SERIAL				= "brand_serial";
	public static final String API_PARAM_BRAND						= "brand";
	public static final String API_PARAM_MODEL_SERIAL				= "model_serial";
	public static final String API_PARAM_MODEL						= "model";
	public static final String API_PARAM_ROLE_ID					= "role_id";
	
	//api response 
	private static final String API_RESP 							= "api_resp";
	private static final String API_RESP_ERR 						= "api_resp_err";
	private static final String API_RESP_MSG						= "api_resp_msg";
	public static final String API_RESP_ARRAY						= "api_resp_array";
	
	public static final String API_RESP_MSG_NULL					= "MSG_NULL";
	
	private Context context;
	

    /////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
	
    /////////////////////////////////////////////////
    // METHODS, COMMON
    /////////////////////////////////////////////////
	
	
	/**Constructor 
	 * @param CONTEXT context
	 */
	public WebApi(Context context) {
		super();
		this.context = context;
	}

	/**
	 * setOnHttpResponce
	 * @param onHttpResponce
	 * @return void
	 */
	public void setOnHttpResponse(OnHttpResponse onHttpResponse)
	{
		this.onHttpResponse = onHttpResponse;
	}
	
	/**
	 * isRespSuccess
	 * 
	 * @param response
	 * @return
	 */
	public static boolean isRespSuccess(String response)
	{
		try {
			return new JSONObject(response).getBoolean(API_RESP);
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static String getRespMsg(String response)
	{
		try {
			return new JSONObject(response).getString(API_RESP_MSG);
		}
		catch (Exception e){
			e.printStackTrace();
			return API_RESP_MSG_NULL;
		}
	}
	
	public static int getRespErr(String response)
	{
		try {
			return new JSONObject(response).getInt(API_RESP_ERR);
		}
		catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	
	public static JSONArray getRespArray(String response)
	{
		try {
			return new JSONObject(response).getJSONArray(API_RESP_ARRAY);
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static int getRespInt(String response, String key, int defaultvalue)
	{
		try {
			return Integer.parseInt(new JSONObject(response).getString(key));
		}
		catch (Exception e){
			e.printStackTrace();
			return defaultvalue;
		}
	}

	/*
	 * postApi  create a new thread to post the params to script located a strUrl
	 * 			then callback the onHttpResponse while get the response from server
	 * @param strUrl
	 * @param params
	 */
	private void postApi(String strUrl, ArrayList<NameValuePair> params)
	{
		ThreadHttpPost thread = new ThreadHttpPost(strUrl, params, onHttpResponse);
		thread.start();
	}
	


    /////////////////////////////////////////////////
    // METHODS, APIs
    /////////////////////////////////////////////////

	
	/**
	 * loginDevice
	 * 
	 * @param account
	 * @param pwd
	 */
	public void loginDevice(String account, String pwd)
	{
		String strUrl = PROTOCOL_HTTP
				+ PrefProxy.getHost(context) 
				+ ":" 
				+ PrefProxy.getPort(context)
				+ API_ROOT 
				+ API_MANAGER_LOGIN;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(WebApi.API_PARAM_ACCOUNT, account));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_PASSWORD, pwd));
		
		postApi(strUrl, params);
	}
	
	public void addApplication(String application, String package_name, String icon, int category_serial,
			int customer_serial, int brand_serial, int model_serial)
	{
		String strUrl = PROTOCOL_HTTP
				+ PrefProxy.getHost(context) 
				+ ":" 
				+ PrefProxy.getPort(context)
				+ API_ROOT 
				+ API_MANAGER_APP_ADD;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(WebApi.API_PARAM_APPLICATION, application));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_PACKAGE, package_name));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_ICON, icon));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_CATEGORY_SERIAL, ""+category_serial));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_CUSTOMER_SERIAL, ""+customer_serial));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_BRAND_SERIAL, ""+brand_serial));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_MODEL_SERIAL, ""+model_serial));
		
		postApi(strUrl, params);
		
	}

	public void addApkinfo(String apkfile, String package_name, int ver_code, String ver_name, int sdk_min, int supplier_serial,
			int customer_serial, int brand_serial, int model_serial)
	{
		String strUrl = PROTOCOL_HTTP
				+ PrefProxy.getHost(context) 
				+ ":" 
				+ PrefProxy.getPort(context)
				+ API_ROOT 
				+ API_MANAGER_APK_ADD;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(WebApi.API_PARAM_FILE_ORIGINAL, apkfile));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_PACKAGE, package_name));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_VERSION_CODE, ""+ver_code));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_VERSION_NAME, ver_name));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_SDK_MIN, ""+sdk_min));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_SUPPLIER_SERIAL, ""+supplier_serial));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_CUSTOMER_SERIAL, ""+customer_serial));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_BRAND_SERIAL, ""+brand_serial));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_MODEL_SERIAL, ""+model_serial));
		
		postApi(strUrl, params);
		
	}

	public void copyApkFile(String apkfile, String package_name, int ver_code)
	{
		String strUrl = PROTOCOL_HTTP
				+ PrefProxy.getHost(context) 
				+ ":" 
				+ PrefProxy.getPort(context)
				+ API_ROOT 
				+ API_MANAGER_APK_FILE_COPY;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(WebApi.API_PARAM_FILE_ORIGINAL, apkfile));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_PACKAGE, package_name));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_VERSION_CODE, ""+ver_code));
		
		postApi(strUrl, params);
		
	}

	public void syncApkInfos(ArrayList<ApkInfo> listApkInfo){

		JSONObject obj = new JSONObject();
		try{
			JSONArray array = new JSONArray();
			for (int i = 0; i<listApkInfo.size();i++){
				ApkInfo apkinfo = listApkInfo.get(i);
				JSONObject jsonapk = apkinfo.toJSONObject();
				jsonapk.put("index", i);
				array.put(jsonapk);
			}
			obj.put(API_PARAM_APKINFO_LIST, array);
			Log.i("syncApkInfos", obj.toString());
			
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		
		String strUrl = PROTOCOL_HTTP
				+ PrefProxy.getHost(context) 
				+ ":" 
				+ PrefProxy.getPort(context)
				+ API_ROOT 
				+ API_MANAGER_APKINFO_SYNC;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(WebApi.API_PARAM_APKINFO_LIST, obj.toString()));
		
		postApi(strUrl, params);
		
	}

	public void syncCustomerApkInfos(ArrayList<ApkInfo> listApkInfo,int customer_serial, int brand_serial, int model_serial){

		JSONObject obj = new JSONObject();
		try{
			JSONArray array = new JSONArray();
			for (int i = 0; i<listApkInfo.size();i++){
				ApkInfo apkinfo = listApkInfo.get(i);
				JSONObject jsonapk = apkinfo.toJSONObject();
				jsonapk.put("index", i);
				array.put(jsonapk);
			}
			obj.put(API_PARAM_APKINFO_LIST, array);
			Log.i("syncApkInfos", obj.toString());
			
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		
		String strUrl = PROTOCOL_HTTP
				+ PrefProxy.getHost(context) 
				+ ":" 
				+ PrefProxy.getPort(context)
				+ API_ROOT 
				+ API_MANAGER_CUSTOMER_APKINFO_SYNC;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(WebApi.API_PARAM_APKINFO_LIST, obj.toString()));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_CUSTOMER_SERIAL, customer_serial+""));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_BRAND_SERIAL, brand_serial+""));
		params.add(new BasicNameValuePair(WebApi.API_PARAM_MODEL_SERIAL, model_serial+""));
		
		postApi(strUrl, params);
		
	}
	
	public void  queryCategory(){
		String strUrl = PROTOCOL_HTTP
				+ PrefProxy.getHost(context) 
				+ ":" 
				+ PrefProxy.getPort(context)
				+ API_ROOT 
				+ API_MANAGER_CATEGORY_QUERY;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		postApi(strUrl, params);
		
	}
	
	public void  querySupplier(){
		String strUrl = PROTOCOL_HTTP
				+ PrefProxy.getHost(context) 
				+ ":" 
				+ PrefProxy.getPort(context)
				+ API_ROOT 
				+ API_MANAGER_SUPPLIER_QUERY;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		postApi(strUrl, params);
		
	}
	
	public void  queryBrand(int customer_serial){
		String strUrl = PROTOCOL_HTTP
				+ PrefProxy.getHost(context) 
				+ ":" 
				+ PrefProxy.getPort(context)
				+ API_ROOT 
				+ API_MANAGER_BRAND_QUERY;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(WebApi.API_PARAM_CUSTOMER_SERIAL, customer_serial+""));
		
		postApi(strUrl, params);
		
	}	

	public void  queryModel(int brand_serial){
		String strUrl = PROTOCOL_HTTP
				+ PrefProxy.getHost(context) 
				+ ":" 
				+ PrefProxy.getPort(context)
				+ API_ROOT 
				+ API_MANAGER_MODEL_QUERY;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(WebApi.API_PARAM_BRAND_SERIAL, brand_serial+""));
		
		postApi(strUrl, params);
		
	}	

	public void  queryCustomer(){
		String strUrl = PROTOCOL_HTTP
				+ PrefProxy.getHost(context) 
				+ ":" 
				+ PrefProxy.getPort(context)
				+ API_ROOT 
				+ API_MANAGER_CUSTOMER_QUERY;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		postApi(strUrl, params);
		
	}	

}
