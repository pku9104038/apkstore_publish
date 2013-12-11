/**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * @author wangpeifeng
 *
 */
public class HostSettingActivity extends Activity {
	
    /////////////////////////////////////////////////
    // PROPERTIES, PUBLIC
    /////////////////////////////////////////////////
	
	/////////////////////////////////////////////////
    // PROPERTIES, PROTECTED
    /////////////////////////////////////////////////

    /////////////////////////////////////////////////
    // PROPERTIES, PRIVATE
    /////////////////////////////////////////////////
    
	private static final int HANDLER_MSG_TOAST			= 1;
	
	private Context context;
	
	private EditText 		etHost,
							etPort,
							etSocketPort;
	
	
	private Button			btnSubmit;
	
	
	private String strApkDir;
	private ArrayList<ApkInfo> listApkInfo;
	
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////
	
	public static final String DIR_DIFF					= "/diff";
	
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////
	
    /////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		context = this;
		
		setContentView(R.layout.network_setting);
		
		etHost = (EditText)findViewById(R.id.editTextHost);
		etHost.setText(PrefProxy.getHost(context));
		
		etPort = (EditText)findViewById(R.id.editTextPort);
		etPort.setText(PrefProxy.getPort(context));
		
		etSocketPort = (EditText)findViewById(R.id.editTextSocketPort);
		etSocketPort.setText(""+PrefProxy.getSocketPort(context));
		
		btnSubmit = (Button)findViewById(R.id.buttonSubmit);
		btnSubmit.setOnClickListener(lsrButton);
		
		findViewById(R.id.buttonApkCompare).setOnClickListener(lsrButton);
		
	}
	
	
	Handler handler = new Handler(){

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case HANDLER_MSG_TOAST:
				Toast.makeText(context, msg.getData().getCharSequence("toast"), Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	private OnClickListener lsrButton = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.buttonSubmit:
				PrefProxy.setHost(context, etHost.getText().toString());
				PrefProxy.setPort(context, etPort.getText().toString());
				PrefProxy.setSocketPort(context, Integer.parseInt(etSocketPort.getText().toString()));
				
				finish();
				break;
			case R.id.buttonApkCompare:
				initDir();
				fetchApkInfos();
				Iterator<ApkInfo> iterator = listApkInfo.iterator();
				String sha1 = null;
				boolean checked = false;
				boolean samefile = true;
				while(iterator.hasNext()){
					ApkInfo info = iterator.next();
					Log.i(this.getClass().getName(), info.getFileName()+"sha1:"+info.getSHA1());
					if(!checked){
						sha1 = info.getSHA1();
						checked = true;
					}
					else{
						if(!info.getSHA1().equals(sha1)){
							samefile = false;
							break;
						}
					}
				}
				Message msg = new Message();
				msg.what = HANDLER_MSG_TOAST;
				Bundle bundle = new Bundle();
				if(samefile){
					bundle.putCharSequence("toast", "Apk files compare PASS!");	
				}
				else{
					bundle.putCharSequence("toast", "Apk files compare FAILED!");			
				}
				msg.setData(bundle);
				handler.sendMessage(msg);
				
				break;
			}
			
		}
		
	};
	
	private void initDir(){
		File download = 
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		if(!download.exists()){
			download.mkdirs();
		}
		this.strApkDir = download.getAbsoluteFile() + DIR_DIFF;
		File repo = new File(this.strApkDir);
		if(!repo.exists()){
			repo.mkdir();
		}
		
	}
	
	private void fetchApkInfos(){
		ArrayList<String> listApkFiles = new ArrayList<String>();
		listApkFiles = ApkInfo.fetchApkFiles(listApkFiles
				, this.strApkDir
				, false);
		
		this.listApkInfo = new ArrayList<ApkInfo>();
		Iterator<String> iterator = listApkFiles.iterator();
		while(iterator.hasNext()){
			String strApkFile = iterator.next();
			Log.i("fetchApkInfos", strApkFile);
			ApkInfo apkinfo = new ApkInfo(this.context, strApkFile);
			if(apkinfo.getPackage() != null){
				this.listApkInfo.add(apkinfo);
			}
		}
	}

}
