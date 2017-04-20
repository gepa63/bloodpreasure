package at.gepa.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class MySQLiteHelper extends SQLiteOpenHelper {

	
	private static final String DATABASE_NAME = "bloodpreasure.db";
	private static final int DATABASE_VERSION = 1;

	private Context context;
	public Context getContext() {
		return context;
	}

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		try{
			database.execSQL(BloodPreasureDBObject.TABLE_CREATE);
		}
		catch(Exception ex)
		{
			Toast.makeText(context, "error creating table: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//if( oldVersion < 2 && newVersion == 8 )
//		Boolean columnExists = null;//ColumnExists(db, "SortPos"); 
//		if( columnExists != null )
//		{
//			Toast.makeText(getContext(), "onUpgrade oldVersion: " + oldVersion + " to newVersion: " + newVersion, Toast.LENGTH_LONG).show();
//			Toast.makeText(getContext(), "alter table add SortPos", Toast.LENGTH_LONG).show();
//			if( columnExists == false )
//			{
//				db.execSQL("alter TABLE " + TABLE_NAME + " add column SortPos integer null");
//				db.execSQL("alter TABLE " + TABLE_NAME + " add column Backgroundcolor integer null");
//				db.execSQL("update " + TABLE_NAME + " set SortPos = id where SortPos is null");
//			}
//		}
//		else 
		{
			String msg ="Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data"; 
			//Log.w(MySQLiteHelper.class.getName(), msg);
			Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
			db.execSQL("DROP TABLE IF EXISTS " + BloodPreasureDBObject.TABLE_CREATE);
			onCreate(db);
		}
	}

	private Boolean ColumnExists(SQLiteDatabase db, String tableName, String column) {
		Boolean ret = null;
		String cmd = "PRAGMA table_info('"+tableName+"')";
		try
		{
			Cursor rs = db.rawQuery(cmd, null);
			rs.moveToFirst();
			while( !rs.isAfterLast() )
			{
				String colname = rs.getString(0);
				if( colname.equalsIgnoreCase(column) )
					ret = true;
			}
			if( ret == null )
				ret = false;
			rs.close();
		}
		catch(Exception ex)
		{
			Log.d( getContext().getPackageName(), ex.getMessage() );
			Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		//This pragma returns one row for each column in the named table. Columns in the result set include the column name, data type, whether or not the column can be NULL, and the default value for the column.		

		return ret;
	}

	public void removeColumn(SQLiteDatabase database, String tableName, String colName) {
		if( !ColumnExists(database, tableName, colName) )
			return;
		
		try{
			database.execSQL("alter table " + tableName + " drop column " + colName );
		}
		catch(Exception ex)
		{
			Log.d("QuickPhoneCaller", ex.getMessage() );
			//Toast.makeText(context, "error create table: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	

}
