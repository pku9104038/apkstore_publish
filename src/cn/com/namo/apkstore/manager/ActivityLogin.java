/**
 * 
 */
package cn.com.namo.apkstore.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * @author wangpeifeng
 *
 */
public class ActivityLogin extends Activity 
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
	private Context context;
	
	private EditText 		etAccount,
							etPwd;
	
	private Button			btnLogin;
	
	private ProgressBar 	progressBar;
    
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////

	private static final int HANDLER_MSG_LOGIN_FAILED					= 1;
	private static final int HANDLER_MSG_LOGIN_SUCCESS					= 1 + HANDLER_MSG_LOGIN_FAILED;
	private static final int HANDLER_MSG_LOGIN_ROLE_ERR					= 1 + HANDLER_MSG_LOGIN_SUCCESS;
	
    /////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
	
    /////////////////////////////////////////////////
    // METHODS, CREATE
    /////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		context = this;
		
		setContentView(R.layout.account_login);
		
		etAccount = (EditText)findViewById(R.id.editTextAccount);
		etAccount.setText(PrefProxy.getAccount(context));
		etPwd = (EditText)findViewById(R.id.editTextPwd);
		etPwd.setText(PrefProxy.getPwd(context));
		
		btnLogin = (Button)findViewById(R.id.buttonLogin);
		btnLogin.setOnClickListener(lsrButton);
		((Button)findViewById(R.id.buttonSetting)).setOnClickListener(lsrButton);
		
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		progressBar.setVisibility(View.INVISIBLE);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
	}

	
    /////////////////////////////////////////////////
    // METHODS, ACTION
    /////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch(item.getItemId())
		{
		case R.id.itemHostSetting:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), HostSettingActivity.class);
			startActivityForResult(intent,0);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private OnClickListener lsrButton = new OnClickListener()
	{
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.buttonLogin:
				String account = etAccount.getText().toString();
				PrefProxy.setAccount(context, account);
				String pwd = etPwd.getText().toString();
				PrefProxy.setPwd(context, pwd);
				progressBar.setVisibility(View.VISIBLE);
				WebApi webApi = new WebApi(context);
				webApi.setOnHttpResponse(onHttpResponse);
				webApi.loginDevice(account, pwd);
				
				StaticData.reset();
				StaticData.account = account;
				
				break;
			case R.id.buttonSetting:
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), HostSettingActivity.class);
				startActivityForResult(intent,0);
				break;
			}
		}
		
	};
	
	private OnHttpResponse onHttpResponse = new OnHttpResponse()
	{

		@Override
		public void onHttpResponse(String response) {
			// TODO Auto-generated method stub
			Log.i(this.getClass().getName(), "http resp:"+response);
			if (WebApi.isRespSuccess(response)){
				
				handler.sendEmptyMessage(ActivityLogin.HANDLER_MSG_LOGIN_SUCCESS);

				StaticData.customer_serial = WebApi.getRespInt(response, WebApi.API_PARAM_CUSTOMER_SERIAL, 0);
				StaticData.supplier_serial = WebApi.getRespInt(response, WebApi.API_PARAM_SUPPLIER_SERIAL, 0);
				StaticData.role_id = WebApi.getRespInt(response, WebApi.API_PARAM_ROLE_ID, 0);
				
				if(StaticData.role_id == 1 || StaticData.role_id == 2 || StaticData.role_id == 7){
				Intent intent = new Intent();
				//intent.setClass(getApplicationContext(), ApkToolsActivity.class);
				if(StaticData.customer_serial==0){
					intent.setClass(getApplicationContext(), ActivityCustomerSelect.class);
				}
				else{
					intent.setClass(getApplicationContext(), ActivityBrandSelect.class);	
				}
				startActivityForResult(intent,0);
				//finish();
				}
				else{
					handler.sendEmptyMessage(ActivityLogin.HANDLER_MSG_LOGIN_ROLE_ERR);
							
				}
			}
			else{
				handler.sendEmptyMessage(ActivityLogin.HANDLER_MSG_LOGIN_FAILED);
			}
		}
		
	};
		

	private Handler handler = new Handler(){

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case HANDLER_MSG_LOGIN_FAILED:
				Toast.makeText(context, "登录失败!", Toast.LENGTH_LONG).show();
				progressBar.setVisibility(View.INVISIBLE);
				
				break;
			case HANDLER_MSG_LOGIN_ROLE_ERR:
				Toast.makeText(context, "该帐号没有管理授权!", Toast.LENGTH_LONG).show();
				progressBar.setVisibility(View.INVISIBLE);
				
				break;
			case HANDLER_MSG_LOGIN_SUCCESS:
				Toast.makeText(context, "登录成功!", Toast.LENGTH_LONG).show();
				progressBar.setVisibility(View.INVISIBLE);
				
				break;
			}
			super.handleMessage(msg);
		}
		
		
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//finish();
	}

	
}
