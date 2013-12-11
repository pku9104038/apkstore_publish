/**
 * 
 */
package cn.com.namo.apkstore.manager;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author wangpeifeng
 *
 */
public class SocketUploadLog {

	class DBOpenHelper extends SQLiteOpenHelper {

		public DBOpenHelper(Context context) {  
	        super(context, "upload.db", null, 1);  
	    }  

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
	        db.execSQL("CREATE TABLE uploadlog (_id integer primary key autoincrement, uploadfilepath varchar(100), sourceid varchar(10))");  

		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
	        db.execSQL("DROP TABLE IF EXISTS uploadlog");  
	        onCreate(db);         

		}

	}

    private DBOpenHelper dbOpenHelper;  
    
    public SocketUploadLog(Context context){  
        this.dbOpenHelper = new DBOpenHelper(context);  
    }  
      
    public void save(String sourceid, File uploadFile){  
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();  
        db.execSQL("insert into uploadlog(uploadfilepath, sourceid) values(?,?)",  
                new Object[]{uploadFile.getAbsolutePath(),sourceid});  
    }  
      
    public void delete(File uploadFile){  
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();  
        db.execSQL("delete from uploadlog where uploadfilepath=?", new Object[]{uploadFile.getAbsolutePath()});  
    }  
      
    public String getBindId(File uploadFile){  
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select sourceid from uploadlog where uploadfilepath=?",   
                new String[]{uploadFile.getAbsolutePath()});  
        if(cursor.moveToFirst()){  
            return cursor.getString(0);  
        }  
        return null;  
    }  
}
