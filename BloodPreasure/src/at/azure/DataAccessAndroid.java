package at.azure;

import java.io.File;

import at.gepa.net.DataAccess;
import at.gepa.net.DataAccessController;
import at.gepa.net.IBackgroundTask;
import at.gepa.net.IModel;
import at.gepa.net.IReadHeaderListener;
import at.gepa.net.IWriteHeaderListener;


public class DataAccessAndroid extends DataAccess {

	public DataAccessAndroid(DataAccessController controller, eAccessType type) {
		super(controller, type);
	}

	public static DataAccess createInstance(String filename, String account, String key, String container) {
		DataAccess.eAccessType type = eAccessType.AzureAndroid;
		DataAccessController dac = null;
		
		dac = new DataAccessAzureAndroidController(account, key, filename, container );
		dac.setSubFolder("");

		DataAccessAndroid da = new DataAccessAndroid(dac, type);
		return da;
	}
	
	@Override
	public String saveFile( IModel list, IBackgroundTask uploadTask, IWriteHeaderListener headerFactory)
	{
		switch( accessType )
		{
		case AzureAndroid:
			return DataAccessAzureAndroid.uploadFile( getAzureController(), list, uploadTask, headerFactory );
		default:
			break;
		}
		return super.saveFile(list, uploadTask, headerFactory);
	}
	
	@Override
	public String loadFile(IBackgroundTask downloadTask, IModel list, IReadHeaderListener readHeaderListener)
	{
		switch( accessType )
		{
		case AzureAndroid:
			return DataAccessAzureAndroid.downloadFile( getAzureController(), downloadTask, list, readHeaderListener );
		default:
			break;
		}
		return super.loadFile(downloadTask, list, readHeaderListener);
	}
	
	@Override
	public void copyFrom(File fromFile) throws Exception 
	{
		Exception e2Throw = null;
		int lines = 0; 
		super.getController().setLastModified(0L);
		switch( accessType )
		{
		case AzureAndroid:
			DataAccessAzureAndroid.backupFileIfExists( (DataAccessAzureAndroidController)super.getController(), ".backup" );
			lines = DataAccessAzureAndroid.copy( fromFile, (DataAccessAzureAndroidController)super.getController() );
			e2Throw = new Exception( lines + " Cachezeilen erfolgreich nach '"+super.getController().getFileName()+"' hochgeladen" );
			break;
		default:
			super.copyFrom(fromFile);
			return;
		}

		if( e2Throw != null )
			throw e2Throw;
	}

	@Override
	public boolean isAzureActive() 
	{
		return ( accessType == eAccessType.AzureAndroid );
	}

}
