/**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.lang.Thread.State;
import java.net.Socket;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author wangpeifeng
 *
 */
public class SocketUploadActivity extends Activity {
	
	/////////////////////////////////////////////////
    // PROPERTIES, PUBLIC
    /////////////////////////////////////////////////

    /////////////////////////////////////////////////
    // PROPERTIES, PROTECTED
    /////////////////////////////////////////////////

    /////////////////////////////////////////////////
    // PROPERTIES, PRIVATE
    /////////////////////////////////////////////////
	
	private static class StreamTool {
		
	    public static void save(File file, byte[] data) throws Exception {  
	        FileOutputStream outStream = new FileOutputStream(file);  
	        outStream.write(data);  
	        outStream.close();  
	    }  
	      
	    public static String readLine(PushbackInputStream in) throws IOException {  
	           char buf[] = new char[128];  
	           int room = buf.length;  
	           int offset = 0;  
	           int c;  
	loop:          while (true) {  
	               switch (c = in.read()) {  
	                   case -1:  
	                   case '\n':  
	                       break loop;  
	                   case '\r':  
	                       int c2 = in.read();  
	                       if ((c2 != '\n') && (c2 != -1)) in.unread(c2);  
	                       break loop;  
	                   default:  
	                       if (--room < 0) {  
	                           char[] lineBuffer = buf;  
	                           buf = new char[offset + 128];  
	                           room = buf.length - offset - 1;  
	                           System.arraycopy(lineBuffer, 0, buf, 0, offset);  
	                            
	                       }  
	                       buf[offset++] = (char) c;  
	                       break;  
	               }  
	           }  
	           if ((c == -1) && (offset == 0)) return null;  
	           return String.copyValueOf(buf, 0, offset);  
	   }  
	      
	   /** 
	   * 读取流 
	   * @param inStream 
	   * @return 字节数组 
	   * @throws Exception 
	   */  
	   public static byte[] readStream(InputStream inStream) throws Exception{  
	           ByteArrayOutputStream outSteam = new ByteArrayOutputStream();  
	           byte[] buffer = new byte[1024];  
	           int len = -1;  
	           while( (len=inStream.read(buffer)) != -1){  
	               outSteam.write(buffer, 0, len);  
	           }  
	           outSteam.close();  
	           inStream.close();  
	           return outSteam.toByteArray();  
	   }  
	}
	
    private EditText filenameText;  
    private Context context;
    private TextView resulView;  
    private Button button;
    private ProgressBar uploadbar;  
    private SocketUploadLog logService;  
    private String uploadFileName, uploadFilePath;
    private File uploadFile;
    private long filelength;
    private static boolean running;
    private int apk_index;
    
    private String host;
    private int port;
    
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////

	public static final String INTENT_EXTRA_FILE_PATH				= "file_path";
	public static final String INTENT_EXTRA_APK_INDEX				= "apk_index";
	public static final String INTENT_EXTRA_HOST					= "host";
	public static final String INTENT_EXTRA_PORT					= "port";
	
    /////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
    
    private Handler handler = new Handler(){  
        @Override  
        public void handleMessage(Message msg) {  
        	if(running){
        		button.setText("停止上传");
        	}
        	else{
        		button.setText("断点续传");
        	}
        	
            int length = msg.getData().getInt("size");  
            uploadbar.setProgress(length);  
            float num = (float)uploadbar.getProgress()/(float)uploadbar.getMax();  
            int result = (int)(num * 100);  
            resulView.setText(result+ "%");  
            if(filelength ==length ){  
                Toast.makeText(SocketUploadActivity.this, R.string.success, 5).show();  
    			Intent intent = new Intent();
                intent.putExtra(INTENT_EXTRA_APK_INDEX, apk_index);
                ((SocketUploadActivity)context).setResult(Activity.RESULT_OK,intent);
    			finish();            
            }  
        }  
    };  
      
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.socketupload);  
        context = this;
        
        Intent intent = getIntent();
        this.apk_index = intent.getIntExtra(INTENT_EXTRA_APK_INDEX, -1);
        Log.i("uploading index", ""+apk_index);
		
        this.uploadFilePath = intent.getStringExtra(INTENT_EXTRA_FILE_PATH);
        this.port = intent.getIntExtra(INTENT_EXTRA_PORT, -1);
        this.host = intent.getStringExtra(INTENT_EXTRA_HOST);
        this.uploadFileName = this.uploadFilePath.substring(this.uploadFilePath.lastIndexOf("/")+1);
        uploadFile = new File(uploadFilePath);
        
        logService = new SocketUploadLog(this);  
        filenameText = (EditText)this.findViewById(R.id.filename);  
        filenameText.setText(uploadFileName);
        
        uploadbar = (ProgressBar) this.findViewById(R.id.uploadbar);  
        resulView = (TextView)this.findViewById(R.id.result);  
        button =(Button)this.findViewById(R.id.button);  
        
        button.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) { 
            	/*
            	if(running){
            		running = false;
            		button.setText("断点续传");
            	}
            	else{
            		if(uploadFile.exists()){  
            			//uploadFile();  
            			if(thread.getState().equals(State.TERMINATED)){
            				thread.start();
            				button.setText("停止上传");
            			}
            		}else{  
            			Toast.makeText(SocketUploadActivity.this, R.string.filenotexsit, 1).show();  
            		}
            	  
            	}
            	*/
            }
        });  
        
          
        if(uploadFile.exists()){  
            //thread.start();
        	uploadFile();
        }else{  
            Toast.makeText(SocketUploadActivity.this, R.string.filenotexsit, 5).show();  
        }  

    }  
    
    
    /** 
     * 上传文件 
     * @param uploadFile 
     */  
    private void uploadFile() {  
        new Thread(new Runnable() {           
            public void run() {  
            	try {  
                	filelength = (int)uploadFile.length();
                    uploadbar.setMax((int)uploadFile.length());  
                    String souceid = logService.getBindId(uploadFile);  
                    String head = "Content-Length="+ uploadFile.length() + ";filename="+ uploadFile.getName() + ";sourceid="+  
                        (souceid==null? "" : souceid)+"\r\n";  
                    //Socket socket = new Socket("114.90.32.145", 8787);  
                    Socket socket = new Socket(host
                    		, port);  
                    OutputStream outStream = socket.getOutputStream();  
                    outStream.write(head.getBytes());  
                    Log.i("head:",head);
                      
                    PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());      
                    String response = StreamTool.readLine(inStream);  
                    String[] items = response.split(";");  
                    String responseid = items[0].substring(items[0].indexOf("=")+1);  
                    String position = items[1].substring(items[1].indexOf("=")+1);  
                    Log.i("resp:",response);
                    
                    if(souceid==null){//代表原来没有上传过此文件，往数据库添加一条绑定记录   
                        logService.save(responseid, uploadFile);  
                    }  
                    RandomAccessFile fileOutStream = new RandomAccessFile(uploadFile, "r");  
                    fileOutStream.seek(Integer.valueOf(position));  
                    byte[] buffer = new byte[1024];  
                    int len = -1;  
                    int length = Integer.valueOf(position);  
                    while( (len = fileOutStream.read(buffer)) != -1 ){  
                        outStream.write(buffer, 0, len);  
                        length += len;  
                        Log.i("write:",""+length);
                        Message msg = new Message();  
                        msg.getData().putInt("size", length);  
                        handler.sendMessage(msg);  
                    }
                    
                    fileOutStream.close();  
                    outStream.close();  
                    inStream.close();  
                    socket.close();  
                    if(length==uploadFile.length()) {
                    	logService.delete(uploadFile);  
                    }
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }).start();  
    }  
    
    private Thread thread = new Thread(){
    	
        public void run() {  
            try {  
            	filelength = (int)uploadFile.length();
                uploadbar.setMax((int)uploadFile.length());  
                String souceid = logService.getBindId(uploadFile);  
                String head = "Content-Length="+ uploadFile.length() + ";filename="+ uploadFile.getName() + ";sourceid="+  
                    (souceid==null? "" : souceid)+"\r\n";  
                //Socket socket = new Socket("114.90.32.145", 8787);  
                Socket socket = new Socket(host
                		, port);  
                OutputStream outStream = socket.getOutputStream();  
                outStream.write(head.getBytes());  
                Log.i("head:",head);
                  
                PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());      
                String response = StreamTool.readLine(inStream);  
                String[] items = response.split(";");  
                String responseid = items[0].substring(items[0].indexOf("=")+1);  
                String position = items[1].substring(items[1].indexOf("=")+1);  
                Log.i("resp:",response);
                
                if(souceid==null){//代表原来没有上传过此文件，往数据库添加一条绑定记录   
                    logService.save(responseid, uploadFile);  
                }  
                RandomAccessFile fileOutStream = new RandomAccessFile(uploadFile, "r");  
                fileOutStream.seek(Integer.valueOf(position));  
                byte[] buffer = new byte[1024];  
                int len = -1;  
                int length = Integer.valueOf(position);  
                while( (len = fileOutStream.read(buffer)) != -1 && running){  
                    outStream.write(buffer, 0, len);  
                    length += len;  
                    Log.i("write:",""+length);
                    Message msg = new Message();  
                    msg.getData().putInt("size", length);  
                    handler.sendMessage(msg);  
                }
                
                fileOutStream.close();  
                outStream.close();  
                inStream.close();  
                socket.close();  
                if(length==uploadFile.length()) {
                	logService.delete(uploadFile);  
                }
            } catch (Exception e) {  
                e.printStackTrace();  
            }
            
            running = false;
        }  
    	
    };

	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		running = false;
	}
    
    
}
