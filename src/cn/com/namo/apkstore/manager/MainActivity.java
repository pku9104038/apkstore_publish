package cn.com.namo.apkstore.manager;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {
	
	
	private WebView webView;
	private MyWebViewClient webViewClient;
	private MyWebChromeClient webChromeClient ;
	private WebSettings webSettings;
	
	private ValueCallback<Uri> mUploadMessage;
	
	public static int FILECHOOSER_RESULTCODE = 1;
	
	public static MainActivity activity;

	  private String uploadFile = "/icon/com.UCMobile.png";  
	  private String srcPath = "/icon/com.UCMobile.png";  
	  //  
	  private String actionUrl = "http://www.pu-up.com/ApkStore/api/manager_app_add.php";  
	  private TextView mText1;  
	  private TextView mText2;  
	  private Button mButton;  
	  
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = this;
	
	/*	
		setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webView);
        
        webViewClient = new MyWebViewClient();
        webView.setWebViewClient(webViewClient);
        
        webChromeClient = new MyWebChromeClient();
        webView.setWebChromeClient(webChromeClient);
        
        webSettings = webView.getSettings();
        
        webSettings.setJavaScriptEnabled(true);
        
        webSettings.setDatabaseEnabled(true);

        webSettings.setDatabasePath(this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath());

        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        
        webView.loadUrl("http://192.168.1.92/ApkStore/");
        //webView.loadUrl("http://www.namo.com.cn:8088/ApkStore/");
		*/
		setContentView(R.layout.main);  
		
		srcPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
					+ srcPath;
		  
		   mText1 = (TextView) findViewById(R.id.textViewFileName);  
		    mText1.setText("�ļ�·����\n" + uploadFile);  
		    mText2 = (TextView) findViewById(R.id.textViewFileSize);  
		    mText2.setText("�ϴ���ַ��\n" + actionUrl);  
		    /* ����mButton��onClick�¼����� */  
		    mButton = (Button) findViewById(R.id.buttonApkCompare);  
		    mButton.setOnClickListener(new View.OnClickListener()  
		    {  
		      public void onClick(View v)  
		      {  
		        //uploadFile(actionUrl);
	                FileUploadTask fileuploadtask = new FileUploadTask();  
	                fileuploadtask.execute();  
	 		    	
		      }  
		    });  
		
	}
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode,  
            Intent intent) {  
        if (requestCode == FILECHOOSER_RESULTCODE) {  
            if (null == mUploadMessage)  
                return;  
            Uri result = intent == null || resultCode != RESULT_OK ? null  
                    : intent.getData();  
            Log.i("APK", result.toString());
            
            mUploadMessage.onReceiveValue(result);  
            mUploadMessage = null;  
       
        }  
    }  


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
    public void setUploadMessage(ValueCallback<Uri> uploadMsg){
    	mUploadMessage = uploadMsg;
    }
    
      
    private void uploadFile(String uploadUrl)  
    {  
      String end = "\r\n";  
      String twoHyphens = "--";  
      String boundary = "******";  
      try  
      {  
        URL url = new URL(uploadUrl);  
        HttpURLConnection httpURLConnection = (HttpURLConnection) url  
            .openConnection();  
        
        httpURLConnection.setChunkedStreamingMode(0);//使用系统缺省值  
        //httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K  
        
        // 要想使用InputStream和OutputStream，必须使用下面两行代码 
        httpURLConnection.setDoInput(true);  
        httpURLConnection.setDoOutput(true);  
        httpURLConnection.setUseCaches(false);  
        // 设置HTTP请求方法，方法名必须大写，例如，GET、POST 
        httpURLConnection.setRequestMethod("POST");  
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");  
        httpURLConnection.setRequestProperty("Charset", "UTF-8");  
        
        // 必须在Content-Type请求头中指定分界符中的任意字符串
        httpURLConnection.setRequestProperty("Content-Type",  
            "multipart/form-data;boundary=" + boundary);  
        // 获得OutputSream对象，准备上传文件 
        DataOutputStream dos = new DataOutputStream(  
            httpURLConnection.getOutputStream());  
        // 设置分界符，加end表示为单独一行
        dos.writeBytes(twoHyphens + boundary + end);  
        // 设置与上传文件相关的信息
        dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""  
            + srcPath.substring(srcPath.lastIndexOf("/") + 1)  
            + "\""  
            + end);  
        // 在上传文件信息与文件内容之间必须有一个空行
        dos.writeBytes(end);  
    
        // 开始上传文件
        FileInputStream fis = new FileInputStream(srcPath);  
        byte[] buffer = new byte[8192]; // 8k  
        int count = 0;  
        //读取文件内容，并写入OutputStream对象 
        while ((count = fis.read(buffer)) != -1)  
        {  
          dos.write(buffer, 0, count);  
        }  
        fis.close();  
     
        // 新起一行 
        dos.writeBytes(end);  
        // 设置结束符号(在分界符后面加两个连字符)
        dos.writeBytes(twoHyphens + boundary + twoHyphens + end);  
        dos.flush();  
    
        //开始读取从服务器端传过来的信息
        InputStream is = httpURLConnection.getInputStream();  
        InputStreamReader isr = new InputStreamReader(is, "utf-8");  
        BufferedReader br = new BufferedReader(isr);  
        String result = br.readLine();  
    
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();  
        dos.close();  
        is.close();  
    
      } catch (Exception e)  
      {  
        e.printStackTrace();  
        setTitle(e.getMessage());  
      }  
    }  
    
    
    
    // show Dialog method  
    private void showDialog(String mess) {  
        new AlertDialog.Builder(MainActivity.this).setTitle("Message")  
                .setMessage(mess)  
                .setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) {  
                    }  
                }).show();  
    }  
  
    class FileUploadTask extends AsyncTask<Object, Integer, Void> {  
  
        private ProgressDialog dialog = null;  
        HttpURLConnection connection = null;  
        DataOutputStream outputStream = null;  
        DataInputStream inputStream = null;  
        //the file path to upload  
        String pathToOurFile = "/AndroidAid_20.apk";  
        //the server address to process uploaded file  
        String urlServer = "http://www.namo.com.cn:8088/ApkStore/api/manager_app_add.php";  
        String lineEnd = "\r\n";  
        String twoHyphens = "--";  
        String boundary = "*****";  
  
        File uploadFile = null;//new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+pathToOurFile);  
        long totalSize = 0;//uploadFile.length(); // Get size of file, bytes  
  
        @Override  
        protected void onPreExecute() {  
        	pathToOurFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+pathToOurFile;
        	uploadFile = new File(pathToOurFile);  
        	totalSize = uploadFile.length(); // Get size of file, bytes  
        	      	
            dialog = new ProgressDialog(MainActivity.this);  
            dialog.setMessage("�����ϴ�...");  
            dialog.setIndeterminate(false);  
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
            dialog.setProgress(0);  
            dialog.show();  
        }  
  
        @Override  
        protected Void doInBackground(Object... arg0) {  
  
            long length = 0;  
            int progress;  
            int bytesRead, bytesAvailable, bufferSize;  
            byte[] buffer;  
            int maxBufferSize = 256 * 1024;// 256KB  
  
            try {  
                FileInputStream fileInputStream = new FileInputStream(new File(  
                        pathToOurFile));  
  
                URL url = new URL(urlServer);  
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
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);  
                outputStream  
                        .writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""  
                                + pathToOurFile + "\"" + lineEnd);  
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
                int serverResponseCode = connection.getResponseCode();  
                String serverResponseMessage = connection.getResponseMessage();  
  
                /* ��Response��ʾ��Dialog */  
                // Toast toast = Toast.makeText(UploadtestActivity.this, ""  
                // + serverResponseMessage.toString().trim(),  
                // Toast.LENGTH_LONG);  
                // showDialog(serverResponseMessage.toString().trim());  
                /* ȡ��Response���� */  
                // InputStream is = connection.getInputStream();  
                // int ch;  
                // StringBuffer sbf = new StringBuffer();  
                // while ((ch = is.read()) != -1) {  
                // sbf.append((char) ch);  
                // }  
                //  
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
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {  
            dialog.setProgress(progress[0]);  
        }  
  
        @Override  
        protected void onPostExecute(Void result) {  
            try {  
                dialog.dismiss();  
                // TODO Auto-generated method stub  
            } catch (Exception e) {  
            }  
        }  
  
    }  
}
