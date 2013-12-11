/**
 *
 */
package cn.com.namo.apkstore.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * PrefProxy - SharedPreferences proxy class
 *
 * @author wangpeifeng
 */
public class PrefProxy {

    /////////////////////////////////////////////////
    // PROPERTIES, PUBLIC
    /////////////////////////////////////////////////

    /////////////////////////////////////////////////
    // PROPERTIES, PROTECTED
    /////////////////////////////////////////////////

    /////////////////////////////////////////////////
    // PROPERTIES, PRIVATE
    /////////////////////////////////////////////////
    
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////
	/**
	 * FILE_SHAREDPREFERENCES 
	 * 
	 */
	private static final String FILE_SHAREDPREFERENCES 				= "myPreferences";
	
	/**
	 * SUPPLIER LIST
	 */
	private static final String KEY_SUPPLIER_LIST					= "supplier_list";
	private static final String VAL_DEFAULT_SUPPLIER_LIST			= "";

	/**
	 * CATEGORY LIST
	 */
	private static final String KEY_CATEGORY_LIST					= "category_list";
	private static final String VAL_DEFAULT_CATEGORY_LIST			= "";
	
	
	/**
	 * SETTING_PARAM 
	 */
	private static final String KEY_SETTING_HOST					= "setting_host";
	private static final String KEY_SETTING_PORT					= "setting_port";
	private static final String KEY_SETTING_SOCKETPORT				= "setting_socketport";
	private static final int VAL_DEFAULT_SOCKETPORT					= 8787;
	private static final String VAL_DEFAULT_HOST					= "www.pu-up.com";
	private static final String VAL_DEFAULT_PORT					= "80";
	
	/**
	 * LOGIN_INFO 
	 */
	private static final String KEY_LOGIN_ACCOUNT					= "login_account";
	private static final String KEY_LOGIN_PWD						= "login_pwd";
	private static final String VAL_DEFAULT_ACCOUNT					= "test";
	private static final String VAL_DEFAULT_PWD						= "";
	
	private static final String VAL_DEFAULT_NULL					= "NULL";
	
    /////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
	/////////////////////////////////////////////////
    // METHODS, KEY/VALUE PAIR
    /////////////////////////////////////////////////
	class KeyTextPair{
		String key;
		String text;
		KeyTextPair(String key, String text){
			this.key = key;
			this.text = text;
		}
	}

	/**
	 * setKeyText		write key/text pair to preferences
	 * @param context
	 * @param pair
	 */
	public static void setKeyText(Context context,KeyTextPair pair)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(pair.key, pair.text);
		editor.commit();
	}
	
	/**
	 * getKeyText 		read key/text pair from preferences
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getKeyText(Context context, String key)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(key, VAL_DEFAULT_NULL);
	}


	/////////////////////////////////////////////////
    // METHODS, SUPPLIER LIST
    /////////////////////////////////////////////////

	/**
	 * setSupplierList		write supplier list to preferences
	 * @param context
	 * @param list
	 */
	public static void setSupplierList(Context context,String list)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(KEY_SUPPLIER_LIST, list);
		editor.commit();
	}
	
	/**
	 * getSupplierList 		read supplier list from preferences
	 * @param context
	 * @return
	 */
	public static String getSupplierList(Context context)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(KEY_SUPPLIER_LIST, VAL_DEFAULT_CATEGORY_LIST);
	}

	/////////////////////////////////////////////////
    // METHODS, CATEGORY LIST
    /////////////////////////////////////////////////

	/**
	 * setCategoryList		write category list to preferences
	 * @param context
	 * @param list
	 */
	public static void setCategoryList(Context context,String list)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(KEY_CATEGORY_LIST, list);
		editor.commit();
	}
	
	/**
	 * getCategoryList 		read category list from preferences
	 * @param context
	 * @return
	 */
	public static String getCategoryList(Context context)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(KEY_CATEGORY_LIST, VAL_DEFAULT_CATEGORY_LIST);
	}
	
    /////////////////////////////////////////////////
    // METHODS, SETTING PARAMETERS
    /////////////////////////////////////////////////

	/**
	 * setHost		write host to preferences
	 * @param context
	 * @param host
	 */
	public static void setHost(Context context,String host)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(KEY_SETTING_HOST, host);
		editor.commit();
	}
	
	/**
	 * getHost 		read host from preferences
	 * @param context
	 * @return
	 */
	public static String getHost(Context context)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(KEY_SETTING_HOST, VAL_DEFAULT_HOST);
	}
	
	/**
	 * setHost		write port to preferences
	 * @param context
	 * @param host
	 */
	public static void setPort(Context context,String port)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(KEY_SETTING_PORT, port);
		editor.commit();
	}
	
	/**
	 * getHost 		read port from preferences
	 * @param context
	 * @return
	 */
	public static String getPort(Context context)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(KEY_SETTING_PORT, VAL_DEFAULT_PORT);
	}
	
	/**
	 * setSocketPort	write socket port to preferences
	 * @param context
	 * @param host
	 */
	public static void setSocketPort(Context context,int port)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putInt(KEY_SETTING_SOCKETPORT, port);
		editor.commit();
	}
	
	/**
	 * getSocketHost 	read port from preferences
	 * @param context
	 * @return
	 */
	public static int getSocketPort(Context context)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		return pref.getInt(KEY_SETTING_SOCKETPORT, VAL_DEFAULT_SOCKETPORT);
	}

    /////////////////////////////////////////////////
    // METHODS, LOGIN_INFOS
    /////////////////////////////////////////////////
	
	/**
	 * setAccount		write account to preferences
	 * @param context
	 * @param account
	 */
	public static void setAccount(Context context,String account)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(KEY_LOGIN_ACCOUNT, account);
		editor.commit();
	}
	
	/**
	 * getAccount 		read account from preferences
	 * @param context
	 * @return
	 */
	public static String getAccount(Context context)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(KEY_LOGIN_ACCOUNT, VAL_DEFAULT_ACCOUNT);
	}
	
	/**
	 * setPwd		write pwd to preferences
	 * @param context
	 * @param pwd
	 */
	public static void setPwd(Context context,String pwd)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(KEY_LOGIN_PWD, pwd);
		editor.commit();
	}
	
	/**
	 * getPwd 		read pwd from preferences
	 * @param context
	 * @return
	 */
	public static String getPwd(Context context)
	{
		SharedPreferences pref = context.getSharedPreferences(FILE_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(KEY_LOGIN_PWD, VAL_DEFAULT_PWD);
	}
	
}
