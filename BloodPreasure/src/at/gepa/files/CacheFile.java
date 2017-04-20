package at.gepa.files;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import at.gepa.net.FileStreamAccess;
import at.gepa.net.IBackgroundTask;
import at.gepa.net.IModel;


public class CacheFile extends LocalFileAccess {
	
	public static final String NAME = "BloodPreasureCache.txt";
	
	private static boolean isAccessingCache = false;

	public static boolean isAccessingCache() {
		return isAccessingCache;
	}

	public CacheFile(Context context) {
		this(context, NAME);
	}
	
	public CacheFile( Context context, String fileName )
	{
		super(new java.io.File(context.getCacheDir(), fileName).getAbsolutePath(), false, context);
	}
	
	public boolean exists()
	{
		return super.getFile().exists();
	}
	public void clear()
	{
		File f = super.getFile();
		if( f.exists() )
			f.delete();
	}
	public boolean readTextFile(IModel model, IBackgroundTask downloadTask)
	{
		if( isAccessingCache ) return false;
		isAccessingCache = true;
		boolean ret = super.readTextFile(model, downloadTask, model.getHeaderListener());
		isAccessingCache = false;
		return ret;
	}
	public boolean writeTextFile(IModel model, IBackgroundTask uploadTask)
	{
		if( isAccessingCache ) return false;
		isAccessingCache = true;
		boolean ret = super.writeTextFile(model, uploadTask, model.getHeaderListener());
		isAccessingCache = false;
		return ret;
	}
	
}
