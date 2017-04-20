package at.gepa.database;

import android.content.ContentValues;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.net.IElement;

public class BloodPreasureDBObject extends CallableObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "bloodpreasure";
	
	private String[] allColumns = { //COLUMN_ID,
			BloodPreasure.COL_DATE, 
			BloodPreasure.COL_SYS,
			BloodPreasure.COL_DIA,
			BloodPreasure.COL_PULS,
			BloodPreasure.COL_DESC,
			BloodPreasure.COL_TAGS,
			BloodPreasure.COL_GEWICHT,
			BloodPreasure.COL_ZUCKER,
			BloodPreasure.COL_TEMP,
			BloodPreasure.COL_MED};
	
	
	static final String TABLE_CREATE = null;
//	"create table "
//			+ TABLE_NAME + "( " + COLUMN_ID
//			+ " integer primary key autoincrement, " + 
//			COLUMN_NAME + " text not null, " +
//			COLUMN_ASCONTACT + " integer null, " +
//			//COLUMN_LOOKUPKEY + " text null, " +
//			COLUMN_TEXTCOLOR + " integer null, " +
//			COLUMN_CONTACTID + " integer null, " +
//			COLUMN_SORTPOS + " integer null," +
//			COLUMN_BACKGROUNDCOLOR + " integer null)";

	/*
	public static final int COL_IDX_DATUM			= 0;
	public static final int COL_IDX_SYSTOLISCH		= 1;
	public static final int COL_IDX_DIASTOLISCH		= 2;
	public static final int COL_IDX_PULS			= 3;
	public static final int COL_IDX_BEMERKUNG		= 4;
	public static final int COL_IDX_TAGS			= 5;
	public static final int COL_IDX_GEWICHT			= 6;
	public static final int COL_IDX_ZUCKER			= 7;
	public static final int COL_IDX_TEMP			= 8;
	public static final int COL_IDX_MED				= 9;
	*/

	public BloodPreasureDBObject() {
	}

	@Override
	public String[] getColumns() {
		return allColumns;
	}
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
	
	@Override
	public String getOrderBy() {
		return BloodPreasure.COL_DATE + " desc";
	}
	
	@Override
	public void fillContentValues(ContentValues values) {
//		values.put(MySQLiteHelper.COLUMN_ASCONTACT, o.isAsContact() ? 1 : 0 );
//		values.put(MySQLiteHelper.COLUMN_NAME, o.getNumberOrContact());
//		//values.put(MySQLiteHelper.COLUMN_LOOKUPKEY, o.getContactLookup());
//		values.put(MySQLiteHelper.COLUMN_CONTACTID, o.getContactID() );			
//		values.put(MySQLiteHelper.COLUMN_TEXTCOLOR, o.getTextColor() );
//		values.put(MySQLiteHelper.COLUMN_SORTPOS, o.getSortPos() );
//		values.put(MySQLiteHelper.COLUMN_BACKGROUNDCOLOR, o.getBackground());
		
	}
	public void setFromElement(IElement element) {
		for( String column : allColumns )
		{
			put( column, element.get(column) );
		}
	}
	
}
