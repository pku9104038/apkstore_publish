/**
 * 
 */
package cn.com.namo.apkstore.manager;

/**
 * @author wangpeifeng
 *
 */
public abstract class OnHttpResponse 
{
	private int apk_index = -1;
	
	public int getApkIndex(){
		return apk_index;
	}
	
	public void setApkIndex(int index){
		apk_index = index;
	}
	
	public abstract void onHttpResponse(String response);

}
