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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * @author wangpeifeng
 *
 */
public class ActivityRadioSelect extends Activity{
	
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
	private RadioGroup radioGroup;

	/////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////

	public static final String INTENT_EXTRA_INDEX			= "index";
	public static final String INTENT_EXTRA_OPTIONS			= "options";
	public static final String INTENT_EXTRA_VALUE_NAME		= "value_name";
	public static final String INTENT_EXTRA_TEXT_NAME		= "text_name";
	public static final String INTENT_EXTRA_TITLE			= "title";
	public static final String INTENT_EXTRA_VALUE_SELECTED	= "value_selected";
	
	public static final String VALUE_NAME_CATEGORY_SERIAL	= "category_serial";
	public static final String VALUE_NAME_SUPPLIER_SERIAL	= "supplier_serial";
	
	public static final String TEXT_NAME_CATEGORY			= "category";
	public static final String TEXT_NAME_SUPPLIER			= "supplier";

	class ValueTextPair{
		int value;
		String text;
		ValueTextPair(int value, String text){
			this.value = value;
			this.text = text;
		}
	}
	
	private ArrayList<ValueTextPair> listOptions;
	
	private int index;
	
	private String value_name;
	private String text_name;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		context = this;
		
		//setTheme(R.style.Transparent);
		//setContentView(R.layout.radioselect);
		View view =  (View) LayoutInflater.from(context).inflate(R.layout.radioselect, null);
		LinearLayout layoutRadioGroup = (LinearLayout) view.findViewById(R.id.layoutRadioGroup);
		//LinearLayout layoutRadioGroup = new LinearLayout(context);// (LinearLayout) view.findViewById(R.id.layoutRadioGroup);
		Intent intent = getIntent();
		
		index = intent.getIntExtra(INTENT_EXTRA_INDEX, -1);
		value_name = intent.getStringExtra(INTENT_EXTRA_VALUE_NAME);
		text_name = intent.getStringExtra(INTENT_EXTRA_TEXT_NAME);
		
		String options = intent.getStringExtra(INTENT_EXTRA_OPTIONS);
		
		String title = intent.getStringExtra(INTENT_EXTRA_TITLE);
		((TextView)(view.findViewById(R.id.textViewTitle))).setText(title);
		Button btn;
		((Button)(view.findViewById(R.id.buttonSubmit))).setOnClickListener(lsrButton);
		
		parseOptions(options);
		radioGroup = new RadioGroup(context);
		for( int i=0; i<listOptions.size(); i++){
			RadioButton radioButton = new RadioButton(context);
			radioButton.setText(listOptions.get(i).text);
			radioButton.setId(listOptions.get(i).value);
			radioGroup.addView(radioButton);
		}
		
		layoutRadioGroup.addView(radioGroup);
		//setContentView(R.layout.radioselect);
		
		setContentView(view);
		//setContentView(layoutRadioGroup);
		
		
	}
	
	private OnClickListener lsrButton = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra(INTENT_EXTRA_VALUE_SELECTED, radioGroup.getCheckedRadioButtonId());
			intent.putExtra(INTENT_EXTRA_INDEX, index);
			((ActivityRadioSelect)context).setResult(Activity.RESULT_OK, intent);
			finish();
			
		}
		
	};
	
	private ArrayList<ValueTextPair> parseOptions(String options){
		listOptions = new ArrayList<ValueTextPair>();
		Log.i("parseOptions", options);
		try{
			JSONArray array = new JSONArray(options);
			for (int i=0; i< array.length(); i++){
				JSONObject obj = array.getJSONObject(i);
				int value = obj.getInt(value_name);
				String text = obj.getString(text_name);
				ValueTextPair item = new ValueTextPair(value, text);
				listOptions.add(item);
				Log.i("parseOptions", value_name+":"+value+",text_name:"+text);
			}
		}
		catch (JSONException e){
			e.printStackTrace();
		}
		
		return listOptions;
	}
	

}
