/**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author wangpeifeng
 *
 */
public class ListAdaptorApkManager extends BaseAdapter {
	
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
	private ArrayList<ApkInfo> listApkInfo;
	private OnApkActionSelected onApkActionSelected;
    /////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////

	/////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////
	

	/**
	 * @param context
	 * @param listApkInfo
	 */
	public ListAdaptorApkManager(Context context, ArrayList<ApkInfo> listApkInfo,
			OnApkActionSelected onApkActionSelected) {
		super();
		this.context = context;
		this.listApkInfo = listApkInfo;
		this.onApkActionSelected = onApkActionSelected;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		// TODO Auto-generated method stub
		return this.listApkInfo.size();
	}


	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.listApkInfo.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			try{
			
				convertView = LayoutInflater.from(context).inflate(R.layout.apklistitem, null);
		
			}
			catch(InflateException e){
				e.printStackTrace();
			}
		}
		
		((TextView)convertView.findViewById(R.id.textViewApkInfo)).setText(
					this.listApkInfo.get(position).getLabel()
					+ "   V"  + this.listApkInfo.get(position).getVerCode()
					+ "\n"  + this.listApkInfo.get(position).getPackage()
					);
	
		//((TextView)convertView.findViewById(R.id.textViewPackage)).setText(
		//		this.listApkInfo.get(position).getPackage()
		//		);
	
		((ImageView)convertView.findViewById(R.id.imageViewIcon)).setImageDrawable(this.listApkInfo.get(position).getIcon());
		
		Button btn = (Button)convertView.findViewById(R.id.buttonAction);
		Resources res = context.getResources();
		switch(this.listApkInfo.get(position).getOnlineState()){
		case ApkInfo.ONLINE_STATE_UNKNOWN:
			btn.setText(res.getString(R.string.add_application));
			btn.setTextColor(Color.WHITE);
			break;
		case ApkInfo.ONLINE_STATE_APP_ONLINE:
			btn.setText(res.getString(R.string.add_apkinfo));
			btn.setTextColor(Color.RED);
			break;
		case ApkInfo.ONLINE_STATE_APK_ONLINE:
			btn.setText(res.getString(R.string.upload_apkfile));
			btn.setTextColor(Color.YELLOW);
			break;
		case ApkInfo.ONLINE_STATE_APK_UPLOADED:
			btn.setText(res.getString(R.string.apk_uploaded));
			btn.setTextColor(Color.GREEN);
			break;
		case ApkInfo.ONLINE_STATE_ICON_UPLOAD:
			btn.setText(res.getString(R.string.update_icon));
			btn.setTextColor(Color.YELLOW);
			break;
		}
		btn.setTag(position);
		btn.setOnClickListener(lsrButton);
		return convertView;
	}
	
	private OnClickListener lsrButton = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			int index = (Integer) v.getTag();
			onApkActionSelected.onApkAction(index);
		}
		
	};

}
