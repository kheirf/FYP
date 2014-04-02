package ie.dit.dt211.mt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBManager 
{
	public static final String KEY_ID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_DATE_CREATED = "timestamp";
	//public static final String KEY_COMPOSER = "composer";
	public static final String KEY_TEMPO = "tempo";
	public static final String KEY_TIME_SIGNATURE = "time";
	public static final String KEY_FILE_PATH = "path";
	private static final String DATABASE_NAME = "MT";
	private static final String DATABASE_TABLE = "Composition";
	private static final int DATABASE_VERSION = 1;
	
	public static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + "(" +
			KEY_ID + " integer primary key autoincrement, " + KEY_TITLE + " text not null, " +
			KEY_DATE_CREATED + " text not null, " + KEY_TEMPO + " text null, " + 
			KEY_TIME_SIGNATURE + " text null, " + KEY_FILE_PATH + " text not null );";
	
	private final Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	
	/*********************
	 * Constructor class
	 * *******************/
	public DBManager(Context ctx)
	{
		this.context = ctx;
		dbHelper = new DatabaseHelper(context);
	}
	
	
	/***************
	 * Inner class for databasehelper
	 * *************/
	private static class DatabaseHelper extends SQLiteOpenHelper
	{

		public DatabaseHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			Log.w("TABLE UPGRADE", "Upgrading database from version " + oldVersion 
					+ " to " + newVersion + ".");
			db.execSQL("DROP TABLE IF EXISTS Composition");
			onCreate(db);
		}
	}
	
	/* * * * * * * * * * * *
	 * 		Methods here
	 * * * *  * * * * * * * */
	
	public DBManager open()
	{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		dbHelper.close();
	}
	
	public Cursor getAllRowsDesc()
	{
		return db.query(DATABASE_TABLE, new String[]{
				KEY_ID, KEY_TITLE, KEY_DATE_CREATED, KEY_TEMPO, KEY_TIME_SIGNATURE, KEY_FILE_PATH}, 
				null, null, null, null, KEY_ID + " DESC", null);
	}
	
	public Cursor getRow(long ID) throws SQLException
	{
		Cursor theCursor = db.query(true, DATABASE_TABLE, new String[]{
				KEY_ID, KEY_TITLE, KEY_DATE_CREATED, KEY_TEMPO, KEY_TIME_SIGNATURE, KEY_FILE_PATH}, 
							KEY_ID + " = " + ID, 
							null, null, null, null, null);
		if (theCursor != null)
			theCursor.moveToFirst();
		return theCursor;
	}
	
	public long insert(String title, String timestamp, String tempo, String timeSignature, String path)
	{
		ContentValues initVal = new ContentValues();
		initVal.put(KEY_TITLE, title);
		initVal.put(KEY_DATE_CREATED, timestamp);
		initVal.put(KEY_TEMPO, tempo);
		initVal.put(KEY_TIME_SIGNATURE, timeSignature);
		initVal.put(KEY_FILE_PATH, path);

		return db.insert(DATABASE_TABLE, null, initVal);
	}
	
	public int deleteRow(long ID)
	{
		return db.delete(DATABASE_TABLE, KEY_ID + "=?", new String[] {Long.toString(ID)});
	}
	
	
	//Custom methods
	public int editTitle(long ID, String title)
	{
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		return db.update(DATABASE_TABLE, args, "_id = " + String.valueOf(ID), null);
	}
	
	public int countRows()
	{
		Cursor cursor = db.query(DATABASE_TABLE, new String[]{
				KEY_ID, KEY_TITLE, KEY_DATE_CREATED, KEY_TEMPO, KEY_TIME_SIGNATURE, KEY_FILE_PATH}, 
				null, null, null, null, KEY_ID + " DESC", null);
		if(cursor.getCount() > 0)
			return cursor.getCount();
		
		return 0;
	}
	
	public int getHighID()
	{
		Cursor cursor = db.query(DATABASE_TABLE, new String[]{
				KEY_ID, KEY_TITLE, KEY_DATE_CREATED, KEY_TEMPO, KEY_TIME_SIGNATURE, KEY_FILE_PATH}, 
				null, null, null, null, KEY_ID + " DESC", null);
		if(cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			return cursor.getInt(0);
		}
		return 0;
	}
	
	public int nextAutoincrement()
	{
		Cursor cursor = db.rawQuery("SELECT * FROM SQLITE_SEQUENCE", null);
		if(cursor.moveToFirst())
			return cursor.getInt(1);
		
		return 0;
		
	}

}
