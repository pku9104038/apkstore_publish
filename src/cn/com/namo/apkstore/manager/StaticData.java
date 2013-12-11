package cn.com.namo.apkstore.manager;

public class StaticData {
	public static String account = "";
	public static int customer_serial = 0;
	public static String customer = "非客户定制";
	public static String brand = "全部品牌";
	public static String model = "全部机型";
	public static int brand_serial = 0;
	public static int model_serial = 0;
	public static int supplier_serial = 1;
	public static int role_id = 0;
	

	public static void reset(){
		account = "";
		customer_serial = 0;
		brand_serial = 0;
		model_serial = 0;
		supplier_serial = 1;
		role_id = 0;
		customer = "非客户定制";
		brand = "全部品牌";
		model = "全部机型";
		
	}
}
