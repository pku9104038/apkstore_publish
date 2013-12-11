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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 
 */

/**
 * @author wangpeifeng
 *
 */
public class TaskFileUpload extends AsyncTask<Object, Integer, Void> {


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
	 
	//the file path to upload  
    private String strFilePath;  
    private String strScriptUrl;
    
    private ProgressDialog dialog = null;  
    private HttpURLConnection connection = null;  
    private DataOutputStream outputStream = null;  
    private DataInputStream inputStream = null;  
   
    private File uploadFile;
    private long totalSize; 
    
    private OnHttpResponse onHttpResponse;
    private String response;
    private int index;
    
    private String package_name;
    private String file_name;
    private int version_code;
    
    private String sha1;
    
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////
    private static final String lineEnd = "\r\n";  
    private static final String twoHyphens = "--";  
    private static final String boundary = "*****";  

    private static final String PROTOCOL_HTTP		 	= "http://";  

	/////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
    
	/**
	 * @param context
	 * @param strFilePath
	 * @param strScriptUrl
	 */
	public TaskFileUpload(Context context, String strFilePath,
			String strScript, int index) {
		super();
		this.context = context;
		this.strFilePath = strFilePath;
		this.strScriptUrl = PROTOCOL_HTTP 
							+ PrefProxy.getHost(context)
							+ ":"
							+ PrefProxy.getPort(context)
							+ strScript;
		
		this.uploadFile = new File(strFilePath);
		this.totalSize = uploadFile.length(); // Get size of file, bytes  
		this.onHttpResponse = null;
		this.index = index;
		
		this.setPackageInfo("", "", 0);
	}
	
	public void setOnHttpResponse(OnHttpResponse onHttpResponse){
		this.onHttpResponse = onHttpResponse;
	}
    
	
	public void setPackageInfo(String package_name, String file_name, int version_code){
		this.package_name = package_name;
		this.file_name = file_name;
		this.version_code = version_code;
	}
    
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
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
		
		
        dialog = new ProgressDialog(context);  
        String filename = strFilePath.substring(strFilePath.lastIndexOf("/")+1);
        dialog.setMessage("上传文件:\n"+filename+"\n文件大小:"+ filesize );  
        dialog.setIndeterminate(false);  
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
        dialog.setProgress(0);  
        dialog.show();  

	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Object... params) {
		// TODO Auto-generated method stub
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
            
            Log.i("TaskFileUpload", connection.getURL().toString());
            
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
            outputStream.writeBytes(this.package_name);  
            outputStream.writeBytes(lineEnd);  
            
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
            outputStream.writeBytes("Content-Disposition: form-data; name=\"original_file\"" + lineEnd);  
            outputStream.writeBytes(lineEnd);  
            outputStream.writeBytes(this.file_name);  
            outputStream.writeBytes(lineEnd);  
            
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
            outputStream.writeBytes("Content-Disposition: form-data; name=\"version_code\"" + lineEnd);  
            outputStream.writeBytes(lineEnd);  
            outputStream.writeBytes(""+this.version_code);  
            outputStream.writeBytes(lineEnd);  

            outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
            outputStream.writeBytes("Content-Disposition: form-data; name=\"sha_from\"" + lineEnd);  
            outputStream.writeBytes(lineEnd);  
            outputStream.writeBytes(this.sha1);  
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

            while (bytesRead > 0) {  
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

            // Responses from the server (code and message)  
            //int serverResponseCode = connection.getResponseCode();  
            //String serverResponseMessage = connection.getResponseMessage();  
            
            /* Response Dialog */  
            // Toast toast = Toast.makeText(UploadtestActivity.this, ""  
            // + serverResponseMessage.toString().trim(),  
            // Toast.LENGTH_LONG);  
            // showDialog(serverResponseMessage.toString().trim());  
            /* Response*/  
            InputStream is = connection.getInputStream();  
            int ch;  
            StringBuffer sbf = new StringBuffer();  
            while ((ch = is.read()) != -1) {  
            	sbf.append((char) ch);  
            }  
            this.response = sbf.toString().trim();
            
            // showDialog(sbf.toString().trim());  

            fileInputStream.close();  
            outputStream.flush();  
            outputStream.close();  

        } catch (Exception ex) {  
            // Exception handling  
            // showDialog("" + ex);  
            // Toast toast = Toast.makeText(UploadtestActivity.this, "" +  
            // ex,  
            // Toast.LENGTH_LONG);  

        }  
        return null;  
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... progress) {
		// TODO Auto-generated method stub
        dialog.setProgress(progress[0]);  
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
        try {  
            dialog.dismiss();  
            
            if(this.onHttpResponse != null){
            	this.onHttpResponse.onHttpResponse(this.response);
            }
            // TODO Auto-generated method stub  
        } catch (Exception e) {  
        }  
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
