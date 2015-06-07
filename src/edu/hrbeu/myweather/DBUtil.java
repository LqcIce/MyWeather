package edu.hrbeu.myweather;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
  
/** 
 * copy���ݿ⵽apk�� 
 *  
 * @author NGJ 
 *  
 */  
public class DBUtil {  
  
    private Context context;  
    public static String dbName = "weather.db";// ���ݿ������  
    private static String DATABASE_PATH;// ���ݿ����ֻ����·��  
  
    public DBUtil(Context context) {  
        this.context = context;  
        String packageName = context.getPackageName();  
        DATABASE_PATH="/data/data/"+packageName+"/databases/";  
    }  
  
    /** 
     * �ж����ݿ��Ƿ���� 
     *  
     * @return false or true 
     */  
    public boolean checkDataBase() {  
        SQLiteDatabase db = null;  
        try {  
            String databaseFilename = DATABASE_PATH + dbName;  
            db = SQLiteDatabase.openDatabase(databaseFilename, null,SQLiteDatabase.OPEN_READONLY);  
        } catch (SQLiteException e) {  
  
        }  
        if (db != null) {  
            db.close();  
        }  
        return db != null ? true : false;  
    }  
  
    /** 
     * �������ݿ⵽�ֻ�ָ���ļ����� 
     *  
     * @throws IOException 
     */  
    public void copyDataBase() throws IOException {  
        String databaseFilenames = DATABASE_PATH + dbName;  
        File dir = new File(DATABASE_PATH);  
        if (!dir.exists())// �ж��ļ����Ƿ���ڣ������ھ��½�һ��  
            dir.mkdir();  
        FileOutputStream os = new FileOutputStream(databaseFilenames);// �õ����ݿ��ļ���д����  
        InputStream is = context.getResources().openRawResource(R.raw.weather);// �õ����ݿ��ļ���������  
        byte[] buffer = new byte[8192];  
        int count = 0;  
        while ((count = is.read(buffer)) > 0) {  
            os.write(buffer, 0, count);  
            os.flush();  
        }  
        is.close();  
        os.close();  
    }  
}  