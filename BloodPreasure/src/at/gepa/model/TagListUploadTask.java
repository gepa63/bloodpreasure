package at.gepa.model;

import android.content.Context;
import at.gepa.net.DataAccess;
import at.gepa.net.IModel;

public class TagListUploadTask extends TagListDownloadTask {

	public TagListUploadTask(Context context) {
		super(context);
	}
	
    @Override
    protected String doInBackground(DataAccess ... da) 
    {
    	String result = null;
    	
    	IModel model = context.getDownloadModel();
    	result = da[0].saveFile(model, this, model.getHeaderListener());
    	
    	return result;
    }
	

}
