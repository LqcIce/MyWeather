package edu.hrbeu.myweather;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * copy���ݿ⵽apk��
 * 
 * @author NGJ
 * 
 */
public class DBUtil extends SQLiteOpenHelper {

	private Context context;
	public static String dbName = "weather.db";// ���ݿ������
	private static String DATABASE_PATH;// ���ݿ����ֻ����·��
	

	private SQLiteDatabase myDataBase;
	SQLiteDatabase db;

	public DBUtil(Context context) {

		super(context, dbName, null, 1);

		this.context = context;
		String packageName = context.getPackageName();
		DATABASE_PATH = "/data/data/" + packageName + "/databases/";
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
			Log.i("tag", "The database is exist.");
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {
				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * �ж����ݿ��Ƿ����
	 * 
	 * @return false or true
	 */
	public boolean checkDataBase() {
		 db = null;
		try {
			String databaseFilename = DATABASE_PATH + dbName;
			db = SQLiteDatabase.openDatabase(databaseFilename, null,
					SQLiteDatabase.OPEN_READONLY);
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
			// os.flush();
		}
		os.flush();
		is.close();
		os.close();
	}

	/**
	 * �����ݿ�
	 * 
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DATABASE_PATH + dbName;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);

	}

	/**
	 * ���ݿ�ر�
	 */
	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	// ��ѯ����
	public String queryOneData(String name) {

		Cursor cursor = myDataBase.rawQuery(
				"select * from areaid_v where NAMECN ='" + name + "'", null);

		int resultCounts = cursor.getCount();
		if (resultCounts == 0 || !cursor.moveToFirst()) {
			return null;
		}

		String results = cursor.getString(cursor.getColumnIndex("AREAID"));

		return results;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
}