/**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

/**
 * @author wangpeifeng
 *
 */
public class ListAdapterRadioKVPair extends BaseAdapter{
	/////////////////////////////////////////////////
    // PROPERTIES, PRIVATE
    /////////////////////////////////////////////////
	private Context context;
	private ArrayList<JSONObject> listItems;
	private OnClickListener lsrRadioButton;
	private long selected_id;
	private int selected_position;
	private String key_name;
	private String key_value;
	
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////

	
    /////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
	/**
	 * @param context
	 * @param listItems
	 * @param lsrRadioButton
	 */
	public ListAdapterRadioKVPair(Context context, ArrayList<JSONObject> listItems,
			String key_name, String key_value, OnClickListener lsrRadioButton) {
		super();
		this.context = context;
		this.listItems = listItems;
		this.lsrRadioButton = lsrRadioButton;
		this.selected_id = -1;
		this.selected_position = -1;
		this.key_name = key_name;
		this.key_value = key_value;
	}

	public void setSelectedId(long id){
		this.selected_id = id;
	}
	
	public long getSelectedId(){
		return this.selected_id;
	}
	
	public void setSelectedPosition(int position){
		this.selected_position = position;
	}
	
	public int getSelectedPosition(){
		return this.selected_position;
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		// TODO Auto-generated method stub
		return this.listItems.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.listItems.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		long id = -1;
		try{
			JSONObject item = this.listItems.get(position);
			id = item.getInt(this.key_value);
			
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		return id;
	}

	/* (non-Javadoc)
	 * 
	 */
	public String getItemName(int position) {
		// TODO Auto-generated method stub
		String name = "";
		try{
			JSONObject item = this.listItems.get(position);
			name = item.getString(this.key_name);
			
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		return name;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		convertView = LayoutInflater.from(context).inflate(R.layout.explistitem_radio, null);
		RadioButton rbtn = (RadioButton) convertView.findViewById(R.id.radioButton);
		long id = this.getItemId(position);
		rbtn.setText(this.getItemName(position));
		rbtn.setOnClickListener(lsrRadioButton);
		rbtn.setTag(Integer.valueOf(position));
		if(this.selected_position == position){
			rbtn.setChecked(true);
		}
		else{
			rbtn.setChecked(false);
		}
		return convertView;
	}

}
