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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * @author wangpeifeng
 *
 */
public class ActivitySupplierSelect extends Activity {

	/////////////////////////////////////////////////
    // PROPERTIES, PUBLIC
    /////////////////////////////////////////////////

    /////////////////////////////////////////////////
    // PROPERTIES, PROTECTED
    /////////////////////////////////////////////////


	/////////////////////////////////////////////////
    // PROPERTIES, PRIVATE
    /////////////////////////////////////////////////
	private ArrayList<JSONObject> listSuppliers;
	private Context context;
	private ListAdapterRadio adapter;
	private ListView listView;
	private int index;
	
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////

	private static final int HANDLER_MSG_UPDATE_LISTVIEW 			= 1;
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
		Intent intent = getIntent();
		index = intent.getIntExtra(ApkManagerActivity.INTENT_EXTRA_INDEX, -1);
		
		context = this;
		this.fetchSuppliers();
		
		this.adapter = new ListAdapterRadio(context, this.listSuppliers, lsrRadioButton);
		
		setContentView(R.layout.supplier_select);
		
		listView = (ListView) findViewById(R.id.listView);
		if(this.listSuppliers.size() > 0){
			listView.setAdapter(adapter);
		}
		((ImageButton)findViewById(R.id.imageButtonBack)).setOnClickListener(lsrButton);
		((Button)findViewById(R.id.buttonSet)).setOnClickListener(lsrButton);
	}
	
	private OnClickListener lsrRadioButton = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i("click:",""+v.getTag());
			adapter.setSelectedId((Long) v.getTag());
			Log.i("select:", ""+adapter.getSelectedId());
			handler.sendEmptyMessage(HANDLER_MSG_UPDATE_LISTVIEW);
			
		}
		
	};
	
	private OnClickListener lsrButton = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			switch(v.getId()){
			case R.id.imageButtonBack:
				((ActivitySupplierSelect)context).setResult(Activity.RESULT_CANCELED, intent);
				finish();
				
				break;
				
			case R.id.buttonSet:
				intent.putExtra(ApkManagerActivity.INTENT_EXTRA_VALUE_SELECTED, (int)(adapter.getSelectedId()));
				intent.putExtra(ApkManagerActivity.INTENT_EXTRA_INDEX, index);
				((ActivitySupplierSelect)context).setResult(Activity.RESULT_OK, intent);
				finish();
				break;
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
			super.handleMessage(msg);
			switch(msg.what){
			case HANDLER_MSG_UPDATE_LISTVIEW:
				adapter.notifyDataSetChanged();
				listView.invalidate();
				break;
			}
		}
		
	};
	
	private void fetchSuppliers(){
		this.listSuppliers = new ArrayList<JSONObject>();
		String response = PrefProxy.getSupplierList(context);
		Log.i("fetchSuppliers", response);
		try{
			//JSONObject obj = new JSONObject(response);
			//JSONArray array = obj.getJSONArray(WebApi.API_RESP_ARRAY);
			JSONArray array = new JSONArray(response);
			for(int i =0; i<array.length(); i++){
				this.listSuppliers.add(array.getJSONObject(i));
				Log.i("fetchSuppliers", array.getJSONObject(i).toString());
			}
			
		}
		catch (JSONException e){
			e.printStackTrace();
		}
	}
}
