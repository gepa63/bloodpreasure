package at.gepa.database;

import java.util.HashMap;

import android.content.ContentValues;
import at.gepa.net.IElement;

@SuppressWarnings("serial")
public abstract class CallableObject extends HashMap<String, Object> {

	public static final String COLUMN_ID = "id";
	public static final int IDX_COLUMN_ID = 		0;
	
	public CallableObject() {
	}

	
	private long id;
	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void dump() {
	}

	public String[] getColumns() {
		return null;
	}

	public String getTableName() {
		return null;
	}

	public String getOrderBy() {
		return "";
	}

	public void fillContentValues(ContentValues values) {
		
	}

	public void setFromElement(IElement element) {
	
		for( String key : element.getKeyList() )
		{
			put(key, element.get(key));
		}
	}


}
