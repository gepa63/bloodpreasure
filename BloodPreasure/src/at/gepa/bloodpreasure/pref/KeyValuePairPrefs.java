package at.gepa.bloodpreasure.pref;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import at.gepa.net.FileStreamAccess;
import at.gepa.net.IElement;
import at.gepa.net.IModel;
import at.gepa.net.IReadWriteHeaderListener;
import at.gepa.net.IWriteable;

public class KeyValuePairPrefs {
	
	public static class KeyValue implements IElement	
	{
		private String key;
		private String value;

		public KeyValue( String key, String value )
		{
			this.key = key;
			this.value = value;
		}
		@Override
		public void write(OutputStreamWriter writer, String delimField) throws IOException {
			writer.write( toString(delimField) );
			writer.write( FileStreamAccess.crlf );
		}

		private String toString(String delimField) 
		{
			return String.format("%s%s%s", key, delimField, value);
		}
		@Override
		public boolean isChanged() {
			return false;
		}

		@Override
		public Object get(Object key) 
		{
			int iv = 0;
			if( key instanceof Integer )
			{
				iv = ((Integer)key).intValue();
			}
			else if( key instanceof String )
			{
				if( key.toString().equalsIgnoreCase("key") )
					iv = 0;
				else
					iv = 1;
			}
			if( iv == 0 )
				return key;
			return value;
		}

		@Override
		public void setChanged(boolean b) {
		}

		@Override
		public Object put(String key, Object o) {
			this.key = key;
			if( o == null )
				o = "";
			this.value = o.toString();
			return o;
		}

		@Override
		public String getTitle(int page) {
			return "";
		}

		@Override
		public Class<?> getType(int page) {
			return String.class;
		}

		@Override
		public int getLines(int page) 
		{
			return 1;
		}
		public String getKey() {
			return key;
		}
		public String getValue() {
			return value;
		}
		@Override
		public String getPrefix(int page) {
			return null;
		}
		@Override
		public List<String> getKeyList() {
			ArrayList<String> ret = new ArrayList<String>();
			ret.add(key);
			return ret;
		}
		
	}
	
	private HashMap<String, IElement> values;
	public KeyValuePairPrefs()
	{
		values = new HashMap<String, IElement>();
	}
	public IReadWriteHeaderListener createHeaderFactory() 
	{
		
		return new IReadWriteHeaderListener(){

			@Override
			public void writeHeader(OutputStreamWriter writer, IModel list) throws IOException {
			}

			@Override
			public void readHeader(BufferedReader reader) throws IOException {
			}};
	}
	public IModel createModel() {
		return new IModel(){

			@Override
			public void clearModel() {
				values.clear();
			}

			@Override
			public void checkPrevious(IElement prev, IElement bp) {
				//nothing to check
			}

			@Override
			public void add(IElement bp) {
				KeyValue kv = (KeyValue)bp;
				String key = kv.getKey();
				values.put( key, bp );
			}

			@Override
			public IElement createInstance(String[] split) 
			{
				if( split.length >= 2 )
					return new KeyValue(split[0], split[1]);
				if( split.length == 1 )
					return new KeyValue(split[0], "");
				return null;
			}

			@Override
			public int size() {
				return values.values().size();
			}

			@Override
			public void writeHeader(OutputStreamWriter writer, String header) throws IOException {
			}

			@Override
			public IWriteable get(int i) 
			{
				return (IWriteable)values.values().toArray()[i];
			}

			@Override
			public IReadWriteHeaderListener getHeaderListener() 
			{
				return new IReadWriteHeaderListener(){

					@Override
					public void readHeader(BufferedReader reader) throws IOException {
					}

					@Override
					public void writeHeader(OutputStreamWriter writer, IModel list) throws IOException {
					}};
			}

			@Override
			public void done() {
			}

			@Override
			public boolean contains(IElement bp) {
				return values.containsKey(bp.get(0).toString());
			}

			@Override
			public void add(int i, IElement bp) {
				values.put(bp.get(0).toString(), bp);
			}

			@Override
			public boolean checkLastModified(long lastModified) {
				return true;
			}

			@Override
			public void setLastModified(long lastModified) {
			}

			@Override
			public String setStream(InputStream input) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isLineToProceed(String line, String fieldDelim) {
				return !line.isEmpty() && line.contains(fieldDelim);
			}};
	}
	public void add(String key, String value) 
	{
		values.put(key, new KeyValue(key, value) );
	}
	public Collection<IElement> getValues() {
		return values.values();
	}
	public String toKeyValueString() {
		String ret = "";
		for( String key : values.keySet() )
		{
			IElement e = values.get(key);
			if( !ret.isEmpty() )
				ret += "#}#";
			KeyValue kv = (KeyValue)e;
			ret += kv.toString("{#}");
		}
		return ret;
	}
	
	public static KeyValuePairPrefs createInstancefromKeyValueString( String str )
	{
		KeyValuePairPrefs p = new KeyValuePairPrefs();
		String [] keyValues = str.split("#}#");
		if( keyValues != null )
		{
			for( String ve : keyValues )
			{
				String [] kva = ve.split("{#}");
				
				p.add(kva[0], kva[1]);
			}
		}
		return p;
	}
}
