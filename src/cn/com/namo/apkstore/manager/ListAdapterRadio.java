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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

/**
 * @author wangpeifeng
 *
 */
public class ListAdapterRadio extends BaseAdapter {

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
	private ArrayList<JSONObject> listItems;
	private OnClickListener lsrRadioButton;
	private long selected_id;
	
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
	public ListAdapterRadio(Context context, ArrayList<JSONObject> listItems,
			OnClickListener lsrRadioButton) {
		super();
		this.context = context;
		this.listItems = listItems;
		this.lsrRadioButton = lsrRadioButton;
		this.selected_id = -1;
	}

	public void setSelectedId(long id){
		this.selected_id = id;
	}
	
	public long getSelectedId(){
		return this.selected_id;
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
			id = item.getInt(WebApi.API_PARAM_SUPPLIER_SERIAL);
			
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
			name = item.getString(WebApi.API_PARAM_SUPPLIER);
			
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
		rbtn.setTag(id);
		if(this.selected_id == id){
			rbtn.setChecked(true);
		}
		else{
			rbtn.setChecked(false);
		}
		return convertView;
	}

}
