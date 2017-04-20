package at.gepa.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import at.gepa.net.FileStreamAccess;
import at.gepa.net.IBackgroundTask;
import at.gepa.net.IModel;
import at.gepa.net.IReadHeaderListener;
import at.gepa.net.IWriteHeaderListener;

public class LocalFileAccess {

	private String fname;
	private boolean isPrivate;
	private Context context;
	private Exception lastError;
	public Exception getLastError() {
		return lastError;
	}

	public LocalFileAccess(String f, boolean isPrivate, Context context) {
		fname = f;
		this.isPrivate = isPrivate;
		this.context = context;
		lastError = null;
	}

	//IFileLineFactory factory
	public boolean readTextFile(IModel model, IBackgroundTask downloadTask, IReadHeaderListener readHeaderListener)
	{
		boolean ret = false;
		FileInputStream inputStream = null;

		try {
			
			inputStream = openFileInput();
			
			FileStreamAccess.readFileFromStream(inputStream, calcFileLength(), downloadTask, model, readHeaderListener);
			
			ret = true;
		} catch (Exception e) {
		  e.printStackTrace();
		  lastError = e;
		}
		finally
		{
			if( inputStream != null )
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			inputStream = null;
		}
		return ret;
	}
	private FileInputStream openFileInput() throws FileNotFoundException 
	{
		File f = getFile();
		if( isPrivate )
			return context.openFileInput(fname);
		return new FileInputStream(f);
	}

	public File getFile() {
		return new File(fname);
	}

	private long calcFileLength() {
		long ret = 0;
		File f = new File(this.fname);
		if( f.exists() )
			ret = f.length();
		return ret;
	}

	public boolean writeTextFile(IModel model, IBackgroundTask uploadTask, IWriteHeaderListener headerFactory)
	{
		boolean ret = false;
		this.lastError = null;
		FileOutputStream writer = null;
        try {
        	writer = openFileOutput(model);
        	FileStreamAccess.writeToOutputStream( writer, model, uploadTask, headerFactory );
        	ret = true;
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e;
        } finally {
            try {
                // Close the writer regardless of what happens...
            	if( writer != null)
            		writer.close();
            } catch (Exception e) {
            }
        }		
        return ret;
	}

	private FileOutputStream openFileOutput(IModel model) throws FileNotFoundException {
    	File f = new File( this.fname );
    	if( f.exists() )
    	{
    		if( model.size() > 0 || f.length() == 0)
    			f.delete();
    	}
    	if( this.isPrivate && !fname.contains("/" ))
    	{
        	return context.openFileOutput(fname, Context.MODE_PRIVATE);
    	}
       	return new FileOutputStream(fname);
	}
	
	public int copyTo( String filename ) throws IOException
	{
		return copyTo( filename, true );
	}
	public int copyTo( String filename, boolean overwriteIfExists ) throws IOException
	{
		File fromFile = new File( this.fname );
		File toFile = new File( filename );
		return FileStreamAccess.copy( fromFile, toFile, overwriteIfExists );
	}
	public boolean containsData() {
		File f = getFile();
		if( f.exists() )
		{
			return FileStreamAccess.containsData( f );
		}
		return false;
	}

	
}
