/**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * @author wangpeifeng
 *
 */
public class ApkInfo {
	/////////////////////////////////////////////////
    // PROPERTIES, PUBLIC
    /////////////////////////////////////////////////

    /////////////////////////////////////////////////
    // PROPERTIES, PROTECTED
    /////////////////////////////////////////////////

    /////////////////////////////////////////////////
    // PROPERTIES, PRIVATE
    /////////////////////////////////////////////////
	private Context context;
	
	private String strAppLabel;
	private String strPackage;
	private String strVerName;
	private int intVerCode;
	private int intMiniSDK;
	private String strFileName;
	private String strFilePath;
	private BitmapDrawable bitmapIcon;
	private String strIconFileName;
	private String strIconFilePath;
	private String sha1;
	
	private int online_state;
	
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////
	
	public static final int ONLINE_STATE_UNKNOWN 			= 0;
	public static final int ONLINE_STATE_APP_ONLINE			= 1;
	public static final int ONLINE_STATE_APK_ONLINE			= 2;
	public static final int ONLINE_STATE_APK_UPLOADED		= 3;
	public static final int ONLINE_STATE_ICON_UPLOAD		= 4;
	
	
	public static final String KEY_APK_PACKAGE				= "package";
	public static final String KEY_APK_VERCODE				= "vercode";
	public static final String KEY_APK_ONLINE_STATE			= "online_state";

	public static final String DIR_ICON						= "/icon";

	
    /////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
	
	/**
	 * @param context
	 */
	public ApkInfo(Context context, String filepath) {
		super();
		this.context = context;
		String strIconPath = filepath.substring(0, filepath.lastIndexOf("/"))+DIR_ICON;
		Log.i("strIconPath", strIconPath);
		File fileIconDir = new File(strIconPath);
		if(!fileIconDir.exists()){
			fileIconDir.mkdir();
		}
		if(!getApkFileInfo(filepath, strIconPath)){
			this.strPackage = null; // get apk info failed, set package name null;
		}
		try{
			this.sha1 = TaskFileUpload.getFileSha1(filepath);
		}
		catch(Exception e){
			e.printStackTrace();
			this.sha1 = null;
		}
		this.online_state = ONLINE_STATE_UNKNOWN;
	}
	
	public String getLabel(){
		return this.strAppLabel;
	}
	
	public String getPackage(){
		return this.strPackage;
	}
	
	public String getFileName(){
		return this.strFileName;
	}
	
	public String getFilePath(){
		return this.strFilePath;
	}
	
	public String getIconFileName(){
		return this.strIconFileName;
	}
	
	public String getIconFilePath(){
		return this.strIconFilePath;
	}
	
	public BitmapDrawable getIcon(){
		return this.bitmapIcon;
	}
	
	public String getVerName(){
		return this.strVerName;
	}
	
	public int getVerCode(){
		return this.intVerCode;
	}
	
	public String getSHA1(){
		return this.sha1;
	}
	
	public int getMiniSDK(){
		return this.intMiniSDK;
	}
	
	public int getOnlineState(){
		return this.online_state;
	}
	
	public void setOnlineState(int state){
		this.online_state = state;
	}
	
	public JSONObject toJSONObject(){
		
		JSONObject obj = null;
		try {
			obj = new JSONObject();
			obj.put(KEY_APK_PACKAGE, this.strPackage);
			obj.put(KEY_APK_VERCODE, this.intVerCode);
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		return obj;
		
	}
	
	/**
	 * fetchApkFiles
	 * 
	 * @param ArrayList<String> listFile
	 * @param String path
	 * @param boolean iterative
	 * @return ArrayList<String >
	 */
	public static ArrayList<String > fetchApkFiles(ArrayList<String> listFile, String path, boolean iterative)  //搜索目录，扩展名，是否进入子文件夹
	{
		Log.i("fetchApkFiles", path);
		String extension = ".APK";
		File[] files = new File(path).listFiles();
		Log.i("fetchApkFiles", ""+files.length);
		
	    for (int i = 0; i < files.length; i++)
	    {
	        File file = files[i];
	        Log.i("fetchApkFiles", ""+file.getAbsolutePath());
			
	        if (file.isFile())
	        {
	        	String ext = file.getPath().substring(file.getPath().length() - extension.length()).toUpperCase();
	        	Log.i("fetchApkFiles", ext);
	        	Log.i("fetchApkFiles", extension.toUpperCase());
	        	
	        	if (ext.equals(extension.toUpperCase())){  //判断扩展名
	                listFile.add(file.getPath());
	                Log.i("fetchApkFiles", ""+listFile.size());
		        }
	  
//	            if (!iterative)
//	                break;
	        }
	        else if (file.isDirectory() && file.getPath().indexOf("/.") == -1 && iterative)  //忽略点文件（隐藏文件/文件夹）
	        	fetchApkFiles(listFile, file.getPath(), iterative);
	    }
	    
	    return listFile;
	}
	
    /** 
     * 获取未安装的apk信息 
     *  
     * @param context 
     * @param strApkFile 
     * @param strIconPath
     * @return boolean
     */  
    private boolean getApkFileInfo(String strApkFile, String strIconPath) {  
        File apkFile = new File(strApkFile);  
        if (apkFile.exists() && strApkFile.toLowerCase().endsWith(".apk")) {  
        
	        String PATH_PackageParser = "android.content.pm.PackageParser";  
	        String PATH_AssetManager = "android.content.res.AssetManager";  
	        try {  
	            //反射得到pkgParserCls对象并实例化,有参数  
	            Class<?> pkgParserCls = Class.forName(PATH_PackageParser);  
	            Class<?>[] typeArgs = {String.class};  
	            Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);  
	            Object[] valueArgs = {strApkFile};  
	            Object pkgParser = pkgParserCt.newInstance(valueArgs);  
	              
	            //从pkgParserCls类得到parsePackage方法  
	            DisplayMetrics metrics = new DisplayMetrics();  
	            metrics.setToDefaults();//这个是与显示有关的, 这边使用默认  
	            typeArgs = new Class<?>[]{File.class,String.class,  
	                                    DisplayMetrics.class,int.class};  
	            Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(  
	                    "parsePackage", typeArgs);  
	              
	            valueArgs=new Object[]{new File(strApkFile),strApkFile,metrics,0};  
	              
	            //执行pkgParser_parsePackageMtd方法并返回  
	            Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,  
	                    valueArgs);  
	              
	            //从返回的对象得到名为"applicationInfo"的字段对象    
	            if (pkgParserPkg==null) {  
	                return false;  
	            }  
	            Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(  
	                    "applicationInfo");  
	              
	            //从对象"pkgParserPkg"得到字段"appInfoFld"的值  
	            if (appInfoFld.get(pkgParserPkg)==null) {  
	                return false;  
	            }  
	            ApplicationInfo info = (ApplicationInfo) appInfoFld.get(pkgParserPkg);   
	            
	              
	            //反射得到assetMagCls对象并实例化,无参  
	            Class<?> assetMagCls = Class.forName(PATH_AssetManager);            
	            Object assetMag = assetMagCls.newInstance();  
	            //从assetMagCls类得到addAssetPath方法  
	            typeArgs = new Class[1];  
	            typeArgs[0] = String.class;  
	            Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(  
	                    "addAssetPath", typeArgs);  
	            valueArgs = new Object[1];  
	            valueArgs[0] = strApkFile;  
	            //执行assetMag_addAssetPathMtd方法  
	            assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);  
	              
	              
	            //得到Resources对象并实例化,有参数  
	            Resources res = context.getResources();  
	            typeArgs = new Class[3];  
	            typeArgs[0] = assetMag.getClass();  
	            typeArgs[1] = res.getDisplayMetrics().getClass();  
	            typeArgs[2] = res.getConfiguration().getClass();  
	            Constructor<Resources> resCt = Resources.class  
	                    .getConstructor(typeArgs);  
	            valueArgs = new Object[3];  
	            valueArgs[0] = assetMag;  
	            valueArgs[1] = res.getDisplayMetrics();  
	            valueArgs[2] = res.getConfiguration();  
	            res = (Resources) resCt.newInstance(valueArgs);  
	              
	              
	            // 读取apk文件的信息   
	            if (info!=null) { 
	            	BitmapDrawable icon = null;
	                if (info.icon != 0) {// 图片存在，则读取相关信息  
	                    icon = (BitmapDrawable) res.getDrawable(info.icon);// 图标  
	                    this.bitmapIcon = icon;  
                    }  
	                if (info.labelRes != 0) {  
	                    String name = (String) res.getText(info.labelRes);// 名字  
	                    this.strAppLabel = name;  
	                }else {  
	                    String apkName=apkFile.getName();  
	                    this.strAppLabel = apkName.substring(0,apkName.lastIndexOf("."));  
	                }  
	                this.strPackage = info.packageName;  //包名
	                this.strIconFileName = this.strPackage + ".png";
	                
	                this.intMiniSDK =  info.targetSdkVersion; //目标SDK 
	                String[] path = strApkFile.split("/");
	                this.strFileName = path[path.length-1]; // 文件名
	                this.strFilePath = strApkFile;
	                Log.i(this.getClass().getName(), this.strFilePath+",sdk:"+this.intMiniSDK);
	                String bitmapFile = strIconPath
	                		+ "/"
	                		+ this.strIconFileName;
	                		
	                FileOutputStream bitmapWtriter = null;
	                try {
	                	bitmapWtriter = new FileOutputStream(new File(bitmapFile),false);
	                } catch (FileNotFoundException e) {
	                 // TODO Auto-generated catch block
	                	e.printStackTrace();
	                }
	                Bitmap bitmap = null;
	                Bitmap bmp = icon.getBitmap();  
	                int size_max = 72;
	                if(bmp.getWidth() > size_max 
	    					|| bmp.getHeight() > size_max){
	    				bitmap = Bitmap.createScaledBitmap(bmp, size_max, size_max, true);
	                }
	                else{
	                	bitmap = bmp;
	                }
	                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bitmapWtriter); 
	                bitmapWtriter.flush();
	                bitmapWtriter.close();
	                this.strIconFilePath = bitmapFile;//图标文件名
	                
	                PackageManager pm = context.getPackageManager();  
	                PackageInfo packageInfo = pm.getPackageArchiveInfo(strApkFile, PackageManager.GET_ACTIVITIES);  
	                if (packageInfo != null) {  
	                    this.strVerName = packageInfo.versionName;//版本号  
	                    this.intVerCode = packageInfo.versionCode;//版本码  
	                }  
	                return true;  
	            }
	        } catch (Exception e) {   
	            e.printStackTrace();  
	        }  
        }
        return false;  
    } 
    
    
}
