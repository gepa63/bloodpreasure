package at.gepa.androidlib;

import java.io.File;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;


public class SystemInfo {

	public static String getVersion(Context context)
	{
		try {
			String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			int revisionCode = 2;
			if (android.os.Build.VERSION.SDK_INT >= 22 )
				revisionCode = getRevisionCode(context);
			return String.format(Locale.GERMAN, "%d.%d (%s)", versionCode, revisionCode, versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//@TargetApi(22)
	private static int getRevisionCode(Context context) {
		int revisionCode = 0;
		//try { revisionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).baseRevisionCode; } catch(Throwable t){}
		return revisionCode;
	}
	
	public static boolean isPROVersion(Context context)
	{
		boolean ret = false;
		String versionName = "";
	
		try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			ret = versionName.contains("PRO");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String getApplicationName( Context context )
	{
		int stringId = context.getApplicationInfo().labelRes;
	    return context.getString(stringId);
	}

	
	public static String getApplicationDirectory(String appName)
	{
		java.io.File base = new java.io.File( Environment.getDataDirectory().getParentFile(), appName);
		if( !base.exists() )
			base.mkdirs();
		if( !base.exists() )
		{
			base = new File( Environment.getExternalStorageDirectory(), appName);
			if( !base.exists() )
				base.mkdirs();
		}
		if( !base.exists() )
			base = Environment.getExternalStorageDirectory();
		//new ContextWrapper(this).getFilesDir().getPath()
		String ret = base.getAbsolutePath(); 
		if( !ret.endsWith("/") )
			ret += "/";
		return ret;
	}

	public static File getFileWithDefaultFolder(String file, String appName) 
	{
		if( file.contains("/") )
			return new File(file);
		return new File( getApplicationDirectory(appName), file);
	}
}
