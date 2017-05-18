package at.azure;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import at.gepa.net.DataAccessAzure;
import at.gepa.net.DataAccessAzureController;
import at.gepa.net.FileStreamAccess;
import at.gepa.net.IBackgroundTask;
import at.gepa.net.IModel;
import at.gepa.net.IReadHeaderListener;
import at.gepa.net.IWriteHeaderListener;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;


public class DataAccessAzureAndroid extends DataAccessAzure 
{

	public static int copy(File fileToUpload, DataAccessAzureAndroidController controller) throws Exception 
	{
	    CloudStorageAccount storageAccount = CloudStorageAccount.parse(controller.getStorageConnectionString());
	    
	    CloudBlobClient fileClient = storageAccount.createCloudBlobClient();
	    
	    CloudBlobContainer container = fileClient.getContainerReference(controller.getContainer());

	    container.createIfNotExists();
	    
    	CloudBlockBlob blob = container.getBlockBlobReference(fileToUpload.getName());
	    	
    	java.io.FileInputStream fis = new java.io.FileInputStream(fileToUpload);
    	int lines = fis.available();
		blob.upload( fis, fileToUpload.length());
		if( lines <= 0 )
			lines = 1;
		return lines;
	}

	public static void backupFileIfExists(DataAccessAzureAndroidController controller, String backupExtension) {
	    CloudStorageAccount storageAccount;
		try {
			storageAccount = CloudStorageAccount.parse(controller.getStorageConnectionString());
			
		    CloudBlobClient fileClient = storageAccount.createCloudBlobClient();
		    
		    CloudBlobContainer container = fileClient.getContainerReference(controller.getContainer());

		    container.createIfNotExists();
		    
	    	CloudBlockBlob fileToBackup = container.getBlockBlobReference(controller.getFileName());
		 
	    	if( fileToBackup.exists() )
	    	{
		    	CloudBlockBlob backupFile = container.getBlockBlobReference(controller.getFileName()+backupExtension);
	    		
		    	String buffer = fileToBackup.downloadText();
		    	
		    	ByteArrayInputStream stream = new ByteArrayInputStream(buffer.getBytes(StandardCharsets.UTF_8));
		    	backupFile.upload(stream, buffer.length());
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}	    
	}

	public static String downloadFile( DataAccessAzureController controller, IBackgroundTask downloadTask, 
			IModel list,
			IReadHeaderListener readHeaderListener) {
		
	    CloudStorageAccount storageAccount;
		String ret = null;
		InputStream[] input = null;
        try {
			storageAccount = CloudStorageAccount.parse(controller.getStorageConnectionString());
			
		    CloudBlobClient fileClient = storageAccount.createCloudBlobClient();
		    
		    CloudBlobContainer container = fileClient.getContainerReference(controller.getContainer());

		    //we where downloading, so all things must exists
		    //container.createIfNotExists();

		    int fileLength = 0;
	    	CloudBlockBlob file2Download = container.getBlockBlobReference(controller.getFileName());
			long lm = 0;
			Date lmdate = file2Download.getProperties().getLastModified();
			if( lmdate != null )
				lm = lmdate.getTime();
		    
	    	String buffer = file2Download.downloadText();
	    	
	    	if( buffer == null )
	    		throw new Exception("No Data!"); 
	    	ByteArrayInputStream stream = new ByteArrayInputStream(buffer.getBytes(StandardCharsets.UTF_8));
	    	
        	input = new InputStream[] {stream};
	    	
			ret = FileStreamAccess.readFileFromStream( input[0], fileLength, downloadTask, list, readHeaderListener, controller.getFieldDelimiter() );

			list.setLastModified(lm);
			
        }
        catch(Exception ex)
        {
        	ret = ex.getMessage();
        }
		return ret;
	}

	public static String uploadFile(DataAccessAzureController controller, IModel list, IBackgroundTask uploadTask,
			IWriteHeaderListener headerFactory) {

		OutputStream os = null;
        try {

		    CloudStorageAccount storageAccount = CloudStorageAccount.parse(controller.getStorageConnectionString());
		    
		    CloudBlobClient fileClient = storageAccount.createCloudBlobClient();
		    
		    CloudBlobContainer container = fileClient.getContainerReference(controller.getContainer());

	    	CloudBlockBlob destFile = container.getBlockBlobReference(controller.getFileName());
		    
    		long lm = 0;
    		if( destFile.exists() )
    		{
    			lm = destFile.getProperties().getLastModified().getTime();
    		}
        	if( !list.checkLastModified(lm) )
        	{
        		return "Die Daten wurden seit ihrem letzten Download geändert! Sie müssen vor dem Speichern neu Laden!\nAchtung, Änderungen gehen verloren!";
        	}

        	FileStreamAccess.writeToOutputStream( destFile.openOutputStream(), list, uploadTask, headerFactory );
        	
			list.setLastModified(lm);
			
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        } 
		
		return null;
	}


}
