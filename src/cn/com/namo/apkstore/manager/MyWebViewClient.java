/**
 * 
 */
package cn.com.namo.apkstore.manager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author wangpeifeng
 *
 */
public class MyWebViewClient extends WebViewClient {
	
	private static final String TAG = "WebViewClient";

	
	Handler handler;
	
	
	
	public MyWebViewClient() {
		super();
	}



	/* (non-Javadoc)
	 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
	 */
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// TODO Auto-generated method stub
//		return super.shouldOverrideUrlLoading(view, url);
		view.loadUrl(url);
		return true;
	}



	/* (non-Javadoc)
	 * @see android.webkit.WebViewClient#onTooManyRedirects(android.webkit.WebView, android.os.Message, android.os.Message)
	 */
	@Override
	public void onTooManyRedirects(WebView view, Message cancelMsg,
			Message continueMsg) {
		// TODO Auto-generated method stub
		super.onTooManyRedirects(view, cancelMsg, continueMsg);
	}
	
	
	

}
