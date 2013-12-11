 /**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.util.ArrayList;
import java.util.zip.Inflater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * @author wangpeifeng
 *
 */
public class ExpListAdapterRadioGroup extends BaseExpandableListAdapter {

	/////////////////////////////////////////////////
    // PROPERTIES, PUBLIC
    /////////////////////////////////////////////////

    /////////////////////////////////////////////////
    // PROPERTIES, PROTECTED
    /////////////////////////////////////////////////


	/////////////////////////////////////////////////
    // PROPERTIES, PRIVATE
    /////////////////////////////////////////////////
	private ArrayList<JSONObject> listGroups;
	private Context context;
	private long selected_id;
	private OnClickListener lsrRadioButton;
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////

	
    /////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
	
    /**
	 * @param listGroups
	 * @param context
	 */
	public ExpListAdapterRadioGroup(ArrayList<JSONObject> listGroups,
			Context context, OnClickListener listener) {
		super();
		this.listGroups = listGroups;
		this.context = context;
		this.selected_id = -1;
		this.lsrRadioButton = listener;
	}
	
	public void setSelectedId(long id){
		this.selected_id = id;
	}
	
	public long getSelectId(){
		return this.selected_id;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		JSONObject child = null;
		try{
			JSONObject group = listGroups.get(groupPosition);
			JSONArray childs = group.getJSONArray(WebApi.API_PARAM_CATEGORIES);
			if(childs != null){
				child = childs.getJSONObject(childPosition);
			}
		}
		catch (JSONException e){
			e.printStackTrace();
		}
		return child;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		int id = 0;
		JSONObject child = (JSONObject) this.getChild(groupPosition, childPosition);
		if (child != null){
			try{
				id = child.getInt(WebApi.API_PARAM_CATEGORY_SERIAL);
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		return id;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)
	 */
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
//		if(convertView == null){
			try{
			
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.explistitem_radio, null);
				
				JSONObject child = (JSONObject) this.getChild(groupPosition, childPosition);
				String name = child.getString(WebApi.API_PARAM_CATEGORY);
				RadioButton rbtn = (RadioButton)convertView.findViewById(R.id.radioButton);
				rbtn.setText(name);
				rbtn.setTag(this.getChildId(groupPosition, childPosition));
				rbtn.setOnClickListener(lsrRadioButton);
				Log.i("id:", ""+this.getChildId(groupPosition, childPosition));
				Log.i("tag:",""+rbtn.getTag());
				if(this.selected_id == this.getChildId(groupPosition, childPosition)){
					rbtn.setChecked(true);
				}
				else{
					rbtn.setChecked(false);
				}
			}
			catch(InflateException e){
				e.printStackTrace();
			}
			catch (JSONException e1){
				e1.printStackTrace();
			}
			
//		}
		 

		return convertView;
	}
	/*
	private OnClickListener lsrRadioButton = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			selected_id = (Long) v.getTag();
		}
		
	};
*/
	
	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		int count = 0;
		try{
			JSONObject group = listGroups.get(groupPosition);
			JSONArray childs = group.getJSONArray(WebApi.API_PARAM_CATEGORIES);
			if(childs != null){
				count = childs.length();
			}
		}
		catch (JSONException e){
			e.printStackTrace();
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return this.listGroups.get(groupPosition);
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return this.listGroups.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		int id = 0;
		try{
			JSONObject group = listGroups.get(groupPosition);
			id = group.getInt(WebApi.API_PARAM_GROUP_SERIAL);
		}
		catch (JSONException e){
			e.printStackTrace();
		}
		return id;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
	 */
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
//		if(convertView == null){
			try{
			
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.explistitem_group, null);
				
				JSONObject group = (JSONObject) this.getGroup(groupPosition);
				
				String name = group.getString(WebApi.API_PARAM_GROUP_NAME);
				((TextView)convertView.findViewById(R.id.textView)).setText(name);
			}
			catch(InflateException e){
				e.printStackTrace();
			}
			catch (JSONException e1){
				e1.printStackTrace();
			}
			
//		}
		 

		return convertView;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
