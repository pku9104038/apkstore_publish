/**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * @author wangpeifeng
 *
 */
public class ThreadHttpPost extends Thread 
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
	private boolean running;
	private String url;
	private ArrayList<NameValuePair> params;
	private String response;
    
	private OnHttpResponse onHttpResponse;
	
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////
	private static final String TAG = "ThreadHttpPost";

    /////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
	
	public ThreadHttpPost(String url, ArrayList<NameValuePair> params, OnHttpResponse onHttpResponse) 
	{
		super();
		this.running = false;
		this.url = url;
		this.params = params;
		response = null;
		this.onHttpResponse = onHttpResponse;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		super.run();
		running = true;
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
	    HttpPost httpPost = new HttpPost(this.url);
	    try {
	    	httpPost.setEntity(new UrlEncodedFormEntity(this.params,HTTP.UTF_8));
	    	Log.i(TAG, "post = "+this.toString());
	    	HttpResponse httpResp = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResp.getEntity();

		    if(httpEntity != null){
				BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
				String line = null;
				response = new String();
				while((line = br.readLine()) != null){
					response += line;
				}
		    	Log.i(TAG, "resp = "+response);
			}
		    
	    }catch (Exception e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    	Log.i(TAG, "cause: "+ e.getCause()+",msg:"+e.getMessage()+",locMsg:"+e.getLocalizedMessage());
			
		}
	
	    try{
			this.onHttpResponse.onHttpResponse(response);
		}
		catch(NullPointerException e1){
			e1.printStackTrace();
		}
	    running = false;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String string = "HttpPost: ";
		string += this.url + "/ ";
		
		Iterator<NameValuePair> i = this.params.iterator();
		while(i.hasNext()){
			string += " & ";
			BasicNameValuePair param = (BasicNameValuePair)i.next();
			//Log.i(this.getClass().getName(), param.toString());
			string += param.getName() + "=" + param.getValue();
		}
		
		return string;
		//return super.toString();
	}
		

}
