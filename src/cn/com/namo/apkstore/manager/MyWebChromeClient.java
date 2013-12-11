/**
 * 
 */
package cn.com.namo.apkstore.manager;

import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage.QuotaUpdater;

/**
 * @author wangpeifeng
 *
 */
public class MyWebChromeClient extends WebChromeClient {

	
	/* (non-Javadoc)
	 * @see android.webkit.WebChromeClient#onExceededDatabaseQuota(java.lang.String, java.lang.String, long, long, long, android.webkit.WebStorage.QuotaUpdater)
	 */
	@Override
	public void onExceededDatabaseQuota(String url, String databaseIdentifier,
			long currentQuota, long estimatedSize, long totalUsedQuota,
			QuotaUpdater quotaUpdater) {
		// TODO Auto-generated method stub
//		super.onExceededDatabaseQuota(url, databaseIdentifier, currentQuota,
//				estimatedSize, totalUsedQuota, quotaUpdater);
		
		quotaUpdater.updateQuota(1024*1024);
	}
	
    // The undocumented magic method override  
    // Eclipse will swear at you if you try to put @Override here  
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {  

        MainActivity.activity.setUploadMessage(uploadMsg);
        
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
        i.addCategory(Intent.CATEGORY_OPENABLE);  
        i.setType("*/*");  
        MainActivity.activity.startActivityForResult(  
                Intent.createChooser(i, "File Chooser"),  
                MainActivity.FILECHOOSER_RESULTCODE);  
    }  
	
    // The undocumented magic method override  
    // Eclipse will swear at you if you try to put @Override here  
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {  

        MainActivity.activity.setUploadMessage(uploadMsg);
        
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
        i.addCategory(Intent.CATEGORY_OPENABLE);  
        i.setType(acceptType);  
        MainActivity.activity.startActivityForResult(  
                Intent.createChooser(i, "File Chooser"),  
                MainActivity.FILECHOOSER_RESULTCODE);  
    }  
	

}
