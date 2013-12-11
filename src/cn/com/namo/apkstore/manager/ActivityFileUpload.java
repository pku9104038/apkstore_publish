/**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author wangpeifeng
 *
 */
public class ActivityFileUpload extends Activity{
	
	
	private Context context;
	 
	//the file path to upload  
    private String strFilePath;  
    private String strScriptUrl;
    
    private ProgressDialog dialog = null;  
    private HttpURLConnection connection = null;  
    private DataOutputStream outputStream = null;  
    private DataInputStream inputStream = null;  
   
    private File uploadFile;
    private long totalSize; 
    
    private static OnHttpResponse onHttpResponse;
    private String response;
    private int index;
    
    private String package_name;
    private String file_name;
    private int version_code;
    
    private String sha1;
    
    private ProgressBar pBar;
    private TextView tvPercent;
   
    private boolean running;
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////
    
    public static final String EXTRA_FILE_PATH			= "file_path";
    public static final String EXTRA_UPLOAD_API			= "upload_api";
    public static final String EXTRA_PACKAGE			= "package";
    public static final String EXTRA_VERCODE			= "vercode";
    public static final String EXTRA_FILE_NAME			= "file_name";
    
    
    private static final int HANDLER_MSG_PROGRESS		= 1;
    private static final int HANDLER_MSG_FINISH			= 1 + HANDLER_MSG_PROGRESS;
    
    private static final String lineEnd 				= "\r\n";  
    private static final String twoHyphens 				= "--";  
    private static final String boundary 				= "*****";  

    private static final String PROTOCOL_HTTP		 	= "http://";  

   
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.fileupload);
		Intent intent = getIntent();
		context = this;
		
		this.strFilePath = intent.getStringExtra(EXTRA_FILE_PATH);
		this.strScriptUrl = PROTOCOL_HTTP 
							+ PrefProxy.getHost(context)
							+ ":"
							+ PrefProxy.getPort(context)
							+ intent.getStringExtra(EXTRA_UPLOAD_API);
		
		this.uploadFile = new File(strFilePath);
		this.totalSize = uploadFile.length(); // Get size of file, bytes  
		
		this.package_name = intent.getStringExtra(EXTRA_PACKAGE);
		this.file_name = intent.getStringExtra(EXTRA_FILE_NAME);
		this.version_code = intent.getIntExtra(EXTRA_VERCODE, 0);

		String filesize = "";
		int count;
		String bytes = "";
		if(totalSize > 1024*1024 ){
			count = 1024*1024;
			bytes = "M";
			
		}
		else {
			count = 1024;
			bytes = "K";
		}
		filesize = (int)(Math.floor(totalSize/count)) + "." + Math.round((totalSize%(count))*10/(count)) + bytes;

		TextView tvName = (TextView) findViewById(R.id.textViewFileName);
		TextView tvSize = (TextView) findViewById(R.id.textViewFileSize);
		tvPercent = (TextView) findViewById(R.id.textViewPercent);
		
		tvName.setText(this.file_name);
		tvSize.setText(filesize);
		
		pBar = (ProgressBar) findViewById(R.id.progressBar);
		pBar.setMax(100);
		pBar.setProgress(0);
		
		findViewById(R.id.buttonCancel).setOnClickListener(lsrButton);
		findViewById(R.id.buttonBackgroud).setOnClickListener(lsrButton);
		
		super.onCreate(savedInstanceState);

		thread.start();
	}
	
	public static void setOnHttpResponse(OnHttpResponse httpResponse){
		onHttpResponse = httpResponse;
	}
	
	Handler handler = new Handler(){

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case HANDLER_MSG_PROGRESS:
				int progress = msg.getData().getInt("progress");
				pBar.setProgress(progress);
				tvPercent.setText(progress+"%");
				break;
				
			case HANDLER_MSG_FINISH:
				setResult(Activity.RESULT_OK);
;				finish();
				break;
				

			}
			super.handleMessage(msg);
		}
		
	};
	
	
    OnClickListener lsrButton = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.buttonCancel:
				running = false;
				setResult(Activity.RESULT_CANCELED);
				finish();
				break;
			case R.id.buttonBackgroud:
				setResult(Activity.RESULT_OK);
				finish();
				break;
			}
		}
    	
    };
    
	Thread thread = new Thread(){

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			running = true;
	        long length = 0;  
	        int progress;  
	        int bytesRead, bytesAvailable, bufferSize;  
	        byte[] buffer;  
	        int maxBufferSize = 64 * 1024;// 256KB  

	        try {  
	        	sha1 = getFileSha1(strFilePath);
	        	Log.i(this.getClass().getName(), "sha1:"+sha1);
	        	
	            FileInputStream fileInputStream = new FileInputStream(new File(  
	                    strFilePath));  

	            URL url = new URL(strScriptUrl);  
	            connection = (HttpURLConnection) url.openConnection();  
	            
	            
	            // Set size of every block for post  
	            connection.setChunkedStreamingMode(256 * 1024);// 256KB  

	            // Allow Inputs & Outputs  
	            connection.setDoInput(true);  
	            connection.setDoOutput(true);  
	            connection.setUseCaches(false);  

	            // Enable POST method  
	            connection.setRequestMethod("POST");  
	            connection.setRequestProperty("Connection", "Keep-Alive");  
	            connection.setRequestProperty("Charset", "UTF-8");  
	            connection.setRequestProperty("Content-Type",  
	                    "multipart/form-data;boundary=" + boundary);  

	            outputStream = new DataOutputStream(  
	                    connection.getOutputStream());  
	// test params
	            outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
	            outputStream.writeBytes("Content-Disposition: form-data; name=\"package\"" + lineEnd);  
	            outputStream.writeBytes(lineEnd);  
	            outputStream.writeBytes(package_name);  
	            outputStream.writeBytes(lineEnd);  
	            
	            outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
	            outputStream.writeBytes("Content-Disposition: form-data; name=\"original_file\"" + lineEnd);  
	            outputStream.writeBytes(lineEnd);  
	            outputStream.writeBytes(file_name);  
	            outputStream.writeBytes(lineEnd);  
	            
	            outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
	            outputStream.writeBytes("Content-Disposition: form-data; name=\"version_code\"" + lineEnd);  
	            outputStream.writeBytes(lineEnd);  
	            outputStream.writeBytes(""+version_code);  
	            outputStream.writeBytes(lineEnd);  

	            outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
	            outputStream.writeBytes("Content-Disposition: form-data; name=\"sha_from\"" + lineEnd);  
	            outputStream.writeBytes(lineEnd);  
	            outputStream.writeBytes(sha1);  
	            outputStream.writeBytes(lineEnd);  

	            outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
	            outputStream.writeBytes("Content-Disposition: form-data; name=\"customer_serial\"" + lineEnd);  
	            outputStream.writeBytes(lineEnd);  
	            outputStream.writeBytes(StaticData.customer_serial+"");  
	            outputStream.writeBytes(lineEnd);  

	            outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
	            outputStream.writeBytes("Content-Disposition: form-data; name=\"brand_serial\"" + lineEnd);  
	            outputStream.writeBytes(lineEnd);  
	            outputStream.writeBytes(StaticData.brand_serial+"");  
	            outputStream.writeBytes(lineEnd);  

	            outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
	            outputStream.writeBytes("Content-Disposition: form-data; name=\"model_serial\"" + lineEnd);  
	            outputStream.writeBytes(lineEnd);  
	            outputStream.writeBytes(StaticData.model_serial+"");  
	            outputStream.writeBytes(lineEnd);  

	            outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
	            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""+strFilePath  
	                    		+"\"" + lineEnd);  
	            outputStream.writeBytes(lineEnd);  

	            
	            bytesAvailable = fileInputStream.available();  
	            bufferSize = Math.min(bytesAvailable, maxBufferSize);  
	            buffer = new byte[bufferSize];  

	            // Read file  
	            bytesRead = fileInputStream.read(buffer, 0, bufferSize);  

	            while (bytesRead > 0 && running) {  
	                outputStream.write(buffer, 0, bufferSize);  
	                length += bufferSize;  
	                progress = (int) ((length * 100) / totalSize);  
	                publishProgress(progress);  

	                bytesAvailable = fileInputStream.available();  
	                bufferSize = Math.min(bytesAvailable, maxBufferSize);  
	                bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
	            }  
	            outputStream.writeBytes(lineEnd);  
	            outputStream.writeBytes(twoHyphens + boundary + twoHyphens  
	                    + lineEnd);  
	            publishProgress(100);  

	            fileInputStream.close();  
	            outputStream.flush();  
	            outputStream.close();  
	            
	            // Responses from the server (code and message)  
	            //int serverResponseCode = connection.getResponseCode();  
	            //String serverResponseMessage = connection.getResponseMessage();  
	            
	            /* Response Dialog */  
	            // Toast toast = Toast.makeText(UploadtestActivity.this, ""  
	            // + serverResponseMessage.toString().trim(),  
	            // Toast.LENGTH_LONG);  
	            // showDialog(serverResponseMessage.toString().trim());  
	            /* Response*/  
	            response = "";
	            try{
		            InputStream is = connection.getInputStream();  
		            int ch;  
		            StringBuffer sbf = new StringBuffer();  
		            while ((ch = is.read()) != -1) {  
		            	sbf.append((char) ch);  
		            	Log.i("upload", "resp:" + ch);
		            }  
		            response = sbf.toString().trim();
	            }
	            catch(Exception eResp){
	            	eResp.printStackTrace();
	            }
	            // showDialog(sbf.toString().trim());  

	            
	            if(onHttpResponse != null){
	            	onHttpResponse.onHttpResponse(response);
	            }
	        	handler.sendEmptyMessage(HANDLER_MSG_FINISH);
	            Log.i("upload","finish!");

	        } catch (Exception ex) {  
	            // Exception handling  
	            // showDialog("" + ex);  
	            // Toast toast = Toast.makeText(UploadtestActivity.this, "" +  
	            // ex,  
	            // Toast.LENGTH_LONG);  

	        }  

		}
		
	};
	
	private void publishProgress(int progress){
		Message msg = new Message();
		msg.what = HANDLER_MSG_PROGRESS;
		Bundle bundle = new Bundle();
		
		bundle.putInt("progress", progress);
		msg.setData(bundle);
		
		handler.sendMessage(msg);
	}
	
	
	public static String getFileSha1(String path) throws OutOfMemoryError,IOException {  
		  
		File file=new File(path);  
		FileInputStream in = new FileInputStream(file);  
		MessageDigest messagedigest;  
		
		try {  
			
		    messagedigest = MessageDigest.getInstance("SHA-1");  
		  
		    byte[] buffer = new byte[1024 * 16];  
		    int len = 0;  
		      
		    while ((len = in.read(buffer)) >0) {  
		   //该对象通过使用 update（）方法处理数据  
		     messagedigest.update(buffer, 0, len);  
		    }  
		     
		  //对于给定数量的更新数据，digest 方法只能被调用一次。在调用 digest 之后，MessageDigest 对象被重新设置成其初始状态。  
		    return toHexString(messagedigest.digest(),"");  
		}   
		catch (NoSuchAlgorithmException e) {  
		    e.printStackTrace();  
		}  
		catch (OutOfMemoryError e) {  
		    e.printStackTrace();  
		    throw e;  
		}  
		finally{  
		     in.close();  
		}  
		return null;  
	}  
	
	private static String toHexString(byte[] bytes, String separator) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
           // editTextResponse.append(Integer.toString(0xFF & b)+" ");  
           // editTextResponse.append(Integer.toHexString(0xFF & b)+" | ");  
            String hex = Integer.toHexString(0xFF & b);
            if(hex.length()==1)
            	hex = "0"+hex;
        	hexString.append(hex);//.append(separator);
        }
        return hexString.toString();
	}
	

}
