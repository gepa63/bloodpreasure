package at.gepa.bloodpreasure.exportimport;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.widget.Toast;
import at.gepa.androidlib.SystemInfo;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.exportimport.ui.ExportImportFragment;
import at.gepa.bloodpreasure.exportimport.ui.FileExportImportFragment;
import at.gepa.bloodpreasure.exportimport.ui.FileTypeExportImportFragment;
import at.gepa.bloodpreasure.pref.BloodPreasurePreferenceActivity;
import at.gepa.bloodpreasure.task.DownloadTask;
import at.gepa.bloodpreasure.task.ICallbackTaskListener;
import at.gepa.bloodpreasure.task.UploadTask;
import at.gepa.files.LocalFileAccess;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.net.DataAccess;
import at.gepa.net.FileNameModel;
import at.gepa.net.IModel;


public abstract class ExportImportData
implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8858719235991186296L;
	public static enum eFileType
	{
		LocalFile,
		Url
	}
	public static enum eMode
	{
		FileOpen,
		FileSave
	}
	
	private eFileType fileType;
	private eMode fileMode;
	
	private HashMap<eFileType,String> file;
	protected ICallbackTaskListener listener;
	
	public ExportImportData(String f)
	{
		this(f, eFileType.LocalFile);
	}
	public ExportImportData( String f, eFileType t)
	{
		this( f, t, eMode.FileOpen);
	}
	public ExportImportData( String f, eMode mode)
	{
		this(f, eFileType.LocalFile, mode);
	}
	public ExportImportData( String f, eFileType t, eMode mode)
	{
		file = new HashMap<eFileType, String>();
		setFile(eFileType.LocalFile, f);
		setFile(eFileType.Url, "http://"+f);
		this.fileType = t;
		fileMode = mode;
	}
	
	public ExportImportData(String[] files, eFileType t, eMode mode) {
		file = new HashMap<eFileType, String>();
		setFile(eFileType.LocalFile, files[0]);
		setFile(eFileType.Url, files[1]);
		this.fileType = t;
		fileMode = mode;
	}
	public String getLocalFile()
	{
		return getFile(eFileType.LocalFile);
	}
	public String getFile(eFileType t)
	{
		if( !file.containsKey(t) ) return "";
		String ret = file.get(t);
		if( ret.isEmpty() )
		{
			if( t == eFileType.Url )
				ret = "http://";
		}
		return ret;
	}
	public String getFile()
	{
		return getFile(fileType);
	}
	public boolean isLocalFile()
	{
		return fileType == eFileType.LocalFile;
	}
	public boolean isUrl()
	{
		return fileType == eFileType.Url;
	}
	public URL getURL() throws MalformedURLException
	{
		return new URL(getFile(eFileType.Url)); 
	}
	public int getCount() {
		return 2;
	}
	public void setFileType(eFileType t) {
		fileType = t;
	}
	
	public eMode getOpenSaveMode() {
		return fileMode;
	}
	public void setLocalFile(String chosenDir) {
		setFile(eFileType.LocalFile, chosenDir);
	}
	
	public void execute(ICallbackTaskListener listener)
	{
		setTaskCallbackListener(listener);
	}
	public void setFile(String value) {
		setFile( fileType, value);
	}
	public void save() {
		try {
			IModel m = MainActivityGrid.self.getUploadModel();
			if( isLocalFile() )
			{				
				File f = SystemInfo.getFileWithDefaultFolder( getFile(), SystemInfo.getApplicationName( MainActivityGrid.self ) );
				if( f.isDirectory() )
					f = new File(f, BloodPreasure.DEFAULT_BACKUPFILE());
				else if( f.exists() )
					f.delete();
				LocalFileAccess lfa = new LocalFileAccess(f.getAbsolutePath(), false, MainActivityGrid.self);
				if( lfa.writeTextFile( m, null, m.getHeaderListener() ) )
					Toast.makeText(MainActivityGrid.self, "Fertig!", Toast.LENGTH_LONG).show();
				else
					throw lfa.getLastError();
			}
			else
			{
				FileNameModel fnm = new FileNameModel(getFile());
				DataAccess da = DataAccess.createInstance(fnm);
				
				UploadTask task = new UploadTask(listener);
				task.setList(m);
				task.setEndMessage(getEndMessage());
				task.execute(da);
			}
			BloodPreasurePreferenceActivity.saveSettingsExportImportFileName(this, getClass().getSimpleName(), "" );
		} catch (Exception e) {
			Toast.makeText(MainActivityGrid.self, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	public String getEndMessage() {
		return "Fertig!";
	}
	public void load() {
		try {
			IModel m = MainActivityGrid.self.getDownloadModel();
			if( isLocalFile() )
			{				
				File f = SystemInfo.getFileWithDefaultFolder( getFile(), SystemInfo.getApplicationName( MainActivityGrid.self ) );
				if( f.isDirectory() )
					f = new File(f, BloodPreasure.DEFAULT_BACKUPFILE());
				try {
					LocalFileAccess lfa = new LocalFileAccess(f.getAbsolutePath(), false, MainActivityGrid.self);
					if( lfa.readTextFile( m, null, m.getHeaderListener()) )
					{
						MainActivityGrid.self.setModelDone(true);
						MainActivityGrid.self.createUploadTask();
					}
					else
						throw lfa.getLastError(); 
					Toast.makeText(MainActivityGrid.self, "Fertig!", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(MainActivityGrid.self, e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				FileNameModel fnm = new FileNameModel(getFile());
				DataAccess da = DataAccess.createInstance(fnm);
				
				DownloadTask task = new DownloadTask(listener);
				task.setList(m);
				task.execute(da);
			}
			BloodPreasurePreferenceActivity.saveSettingsExportImportFileName(this, getClass().getSimpleName(), "" );
		} catch (Exception e) {
			Toast.makeText(MainActivityGrid.self, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	public ICallbackTaskListener getTaskCallbackListener() {
		return listener;
	}
	private void setTaskCallbackListener(ICallbackTaskListener listener) 
	{
		this.listener = listener;
	}
	public void setUrlFile(String s) {
		setFile(eFileType.Url, s);
	}
	public void setFile(eFileType key, String value) {
		file.put(key, value);
	}
	public ExportImportFragment createInstance(int page, IExportImportTypListener listener) 
	{
		if( page == 0 )
			return new FileTypeExportImportFragment(page, this, listener);
		return new FileExportImportFragment(page, this, listener);
	}
	private Context context;
	public void setContext(Context c) {
		context = c;
	}
	public Context getContext() {
		if( context == null )
			context = MainActivityGrid.self;
		return context;
	}
	
}
