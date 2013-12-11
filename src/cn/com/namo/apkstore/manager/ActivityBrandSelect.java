/**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

/**
 * @author wangpeifeng
 *
 */
public class ActivityBrandSelect extends Activity{
	
	private static final int HANDLER_MSG_SYNC_BRAND 			= 1; 
	private static final int HANDLER_MSG_BRAND_SELECT 			= 1 + HANDLER_MSG_SYNC_BRAND; 
	private static final int HANDLER_MSG_UPDATE_LISTVIEW 		= 1 + HANDLER_MSG_BRAND_SELECT; 
	
	
	private Context context;
	private ListView listView;
	private ProgressBar pBar;
	private ListAdapterRadioKVPair adapter;
	private ArrayList<JSONObject> listArray;
	
	
	private Handler handler = new Handler(){

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case HANDLER_MSG_SYNC_BRAND:
				queryBrand();
				break;
				
			case HANDLER_MSG_BRAND_SELECT:
				
				adapter = new ListAdapterRadioKVPair(context, listArray,WebApi.API_PARAM_BRAND,
						WebApi.API_PARAM_BRAND_SERIAL,lsrRadioButton);
				adapter.setSelectedPosition(listArray.size()-1);
				listView.setAdapter(adapter);
				listView.setVisibility(View.VISIBLE);
				pBar.setVisibility(View.GONE);
				listView.invalidate();
				break;
				
				
			case HANDLER_MSG_UPDATE_LISTVIEW:
				adapter.notifyDataSetChanged();
				listView.invalidate();
				break;

			}
			super.handleMessage(msg);
		}
		
		
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context = this;
		
		setContentView(R.layout.brand_select);
		
		pBar = (ProgressBar) findViewById(R.id.progressBar);
		pBar.setVisibility(View.VISIBLE);
		
		listView = (ListView) findViewById(R.id.listView);
		listView.setVisibility(View.INVISIBLE);
		
		findViewById(R.id.buttonSet).setOnClickListener(lsrButton);
		super.onCreate(savedInstanceState);
		
		handler.sendEmptyMessage(HANDLER_MSG_SYNC_BRAND);
	}

	private OnClickListener lsrButton = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			switch(v.getId()){
			case R.id.imageButtonBack:
				((Activity)context).setResult(Activity.RESULT_CANCELED, intent);
				finish();
				
				break;
				
			case R.id.buttonSet:
				int position = adapter.getSelectedPosition();
				if(position>=0){
					StaticData.brand_serial = (int) adapter.getItemId(position);
				}
				else{
					StaticData.brand_serial = 0;
				
				}
				
				if(StaticData.brand_serial>0){
					StaticData.brand = adapter.getItemName(position);
				}
				
				intent = new Intent();
				if(StaticData.brand_serial<1){
					intent.setClass(getApplicationContext(), ActivityCustomerApkManager.class);
				}
				else{
					intent.setClass(getApplicationContext(), ActivityModelSelect.class);	
				}
				startActivityForResult(intent,0);

				break;
			}
			
		}
		
	};
	
	private OnClickListener lsrRadioButton = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			adapter.setSelectedPosition((Integer) v.getTag());
			handler.sendEmptyMessage(HANDLER_MSG_UPDATE_LISTVIEW);
			
		}
		
	};

	private OnHttpResponse onHttpRespBrandQuery = new OnHttpResponse(){

		@Override
		public void onHttpResponse(String response) {
			// TODO Auto-generated method stub
			if(WebApi.isRespSuccess(response)){
				try{
					JSONArray array = WebApi.getRespArray(response);
					listArray = new ArrayList<JSONObject>();
					for(int i =0; i<array.length(); i++){
						listArray.add(array.getJSONObject(i));
					}
					JSONObject obj = new JSONObject();
					obj.put("brand_serial", 0);
					obj.put("brand", "全部品牌");
					//listArray.add(obj);
					handler.sendEmptyMessage(HANDLER_MSG_BRAND_SELECT);
					
				}
				catch (JSONException e){
					e.printStackTrace();
				}
				
			}
		}
		
	};
	
	private void queryBrand(){
		WebApi webapi = new WebApi(context);
		webapi.setOnHttpResponse(this.onHttpRespBrandQuery);
		webapi.queryBrand(StaticData.customer_serial);
	}	

}
