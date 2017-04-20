package at.gepa.database;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.DownloadManager.Query;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import at.gepa.net.IElement;


public class CallableObjectDatabase {
	
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public CallableObjectDatabase(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void setDBVersion(int version) 
	{
		int dbVersion = getDBVersion(); 
		if( dbVersion < version )
		{
			//Toast.makeText(dbHelper.getContext(), "update version from oldVersion: " + dbVersion + " to newVersion: " + version, Toast.LENGTH_LONG).show();
			try
			{
				database.execSQL("PRAGMA user_version = " + version);
			}
			catch(Exception ex)
			{
				//Log.e( dbHelper.getContext().getPackageName(), ex.getMessage() );
				Toast.makeText(dbHelper.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
//			else
//				Toast.makeText(dbHelper.getContext(), "NO update of version from oldVersion: " + dbVersion + " to newVersion: " + version, Toast.LENGTH_LONG).show();
	}
	public int getDBVersion() 
	{
		int version = 2;
		try
		{
			Cursor rs = database.rawQuery("PRAGMA user_version", null);
			rs.moveToFirst();
			if( !rs.isAfterLast() )
				version = rs.getInt(0);
			rs.close();
		}
		catch(Exception ex)
		{
			Log.e( dbHelper.getContext().getPackageName(), ex.getMessage() );
			Toast.makeText(dbHelper.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
		}
		return version;
	}


	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void verifyDatabase()
	{
		try {
			int currentVersion = dbHelper.getContext().getPackageManager().getPackageInfo(dbHelper.getContext().getPackageName(), 0).versionCode;
			int dbVersion = getDBVersion();
			if( dbVersion < currentVersion )
			{
				setDBVersion(currentVersion);
			}
		} catch( Exception e) {
			Log.e( dbHelper.getContext().getPackageName(), e.getMessage() );
			Toast.makeText(dbHelper.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
	}

	public void close() {
		dbHelper.close();
		dbHelper = null;
	}
	
	@Override
	public void finalize()
	{
		if( dbHelper != null )
		{
			try
			{
				close();
			}
			catch(Throwable t) {}
		}
	}

	
	public CallableObject createCallableObject( IElement element, CallableObject co) {
		Cursor cursor = null;
		try {
			//Log.d("at.gepa.phone", "sortpos="+sortpos);
			ContentValues values = new ContentValues();

			co.setFromElement( element );
			co.fillContentValues(values);
			
//			values.put(MySQLiteHelper.COLUMN_ASCONTACT, asContact ? 1 : 0 );
//			values.put(MySQLiteHelper.COLUMN_NAME, name);
//			
//			if( tcolor == 0 )
//				tcolor = Color.BLACK;
//			values.put(MySQLiteHelper.COLUMN_TEXTCOLOR, tcolor);
//			values.put(MySQLiteHelper.COLUMN_CONTACTID, contactId);
//			if( sortpos == null || sortpos <= 0)
//				sortpos = queryMaxSortPos()+1;
//			values.put(MySQLiteHelper.COLUMN_SORTPOS, sortpos);
//			values.put(MySQLiteHelper.COLUMN_BACKGROUNDCOLOR, backgroundcolor);
			
			long insertId = database.insert(co.getTableName(), null, values);
			// To show how to query
			cursor = database.query(co.getTableName(), co.getColumns(), CallableObject.COLUMN_ID + " = " + insertId, null, null, null, null);
			cursor.moveToFirst();
		}
		catch(Exception ex)
		{
			printError( "error select: ", ex );
		}
		
		return cursorToCallableObject(cursor, co);
	}

	private void printError(String suffix, Exception ex) {
		String msg = ex.getMessage();
		if( msg == null )
		{
			ex.printStackTrace();
			msg = "null!";
		}
		Toast.makeText(dbHelper.getContext(), suffix + msg, Toast.LENGTH_SHORT).show();
	}

	public void deleteCallableObject(CallableObject o) {
		try {
			long id = o.getId();
			System.out.println("Comment deleted with id: " + id);
			database.delete(o.getTableName(), CallableObject.COLUMN_ID + " = " + id, null);
		}
		catch(Exception ex)
		{
			printError( "error delete: ", ex );
		}
	}

	public List<CallableObject> getAllObjects(CallableObject o) {
		List<CallableObject> l = new ArrayList<CallableObject>();
		try{
			Cursor cursor = database.query(o.getTableName(), o.getColumns(), null, null, null, null, o.getOrderBy());
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) 
			{
				o = cursorToCallableObject(cursor, o);
				o.dump();
				l.add(o);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
		}
		catch(Exception ex)
		{
			printError( "error getAllObjects: ", ex );
		}
		
		return l;
	}

	private CallableObject cursorToCallableObject(Cursor cursor, CallableObject o) {
		
		if( cursor == null ) return o;
		try
		{
			o.setId((int)cursor.getLong(CallableObject.IDX_COLUMN_ID));
			
//			o.setNumberOrContact(cursor.getString(MySQLiteHelper.IDX_COLUMN_NAME));
//			o.setAsContact( cursor.getInt(MySQLiteHelper.IDX_COLUMN_ASCONTACT) == 0 ? false : true);
//			o.setTextColor( cursor.getInt(MySQLiteHelper.IDX_COLUMN_TEXTCOLOR));
//			o.setContactID( cursor.getInt(MySQLiteHelper.IDX_COLUMN_CONTACTID));
//			o.setSortPos( cursor.getInt(MySQLiteHelper.IDX_COLUMN_SORTPOS) );
//			o.setBackground( cursor.getInt(MySQLiteHelper.IDX_COLUMN_BKCOLOR) );
		}
		catch(Exception ex)
		{
			printError( "error cursorToCallableObject: ", ex );
		}
		return o;
	}

	public void update(CallableObject o) {
		try {
			long id = o.getId();
			ContentValues values = new ContentValues();
			
			o.fillContentValues( values );

			database.update(o.getTableName(), values, CallableObject.COLUMN_ID + " = " + id, null);
		}
		catch(Exception ex)
		{
			printError( "error update: ", ex );
		}
	}

//	public void importFrom(File f) throws IOException 
//	{
//		database.beginTransaction();
//		database.execSQL("delete from " + MySQLiteHelper.TABLE_NAME);
//		FileReader fr = new FileReader(f);
//		BufferedReader br = new BufferedReader(fr);
//		String line = "";
//		int rowcount =0;
//		String header = br.readLine();
//		if( header != null )
//		{
//			//Log.d("at.gepa.phone", "*" + header + "*");
//			while( (line = br.readLine()) != null )
//			{
//				//Log.d("at.gepa.phone", "*" + line + "*");
//				CallableObject co = new CallableObject(line);
//				
//				if( createCallableObject(co.getNumberOrContact(), co.isAsContact(), /*co.getContactLookup(), */co.getId(), co.getTextColor(), co.getSortPos()-1, co.getBackground()) == null)
//					break;
//				rowcount++;
//			}
//		}
//		if( rowcount > 0 )
//			database.setTransactionSuccessful();
//		database.endTransaction();
//		br.close();
//	}

	public void clear(CallableObject o) {
		database.beginTransaction();
		database.execSQL("delete from " + o.getTableName());
		database.setTransactionSuccessful();
		database.endTransaction();
	}
	
}
