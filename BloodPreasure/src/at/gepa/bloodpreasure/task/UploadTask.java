package at.gepa.bloodpreasure.task;

import android.widget.Toast;
import at.gepa.net.DataAccess;

public class UploadTask extends DownloadTask
{
    private boolean withReload;

	protected CharSequence getStartMessage() {
		return "Start uploading ...";
	}

	public UploadTask(ICallbackTaskListener context) {
		this(context, false);
	}
    public UploadTask(ICallbackTaskListener context, boolean withReload) 
    {
		super(context);
		this.withReload = withReload;
    	
	}

	@Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        context.getProgressDialog().dismiss();
        if (result != null)
            Toast.makeText(context.getContext(), "Upload Fehler: "+result, Toast.LENGTH_LONG).show();
        else
        {
			if( this.withReload )
			{
				context._createDownloadTask();
				//MainActivityGrid.createDownloadTask();
			}
			else
			{
				context.setModelDone(false);
				if( context instanceof ICachedCallbackTaskListener )
					((ICachedCallbackTaskListener)context).saveCache(getList());
				context.doUIRefresh();
	            if( context.getBloodPreasureCount() > 0 )
	            	context.setListSelection(0);
	        	if( getEndMessage() != null )
	        		Toast.makeText(context.getContext(),getEndMessage(), Toast.LENGTH_LONG).show();
	            
			}
        }
    }
    @Override
    protected String doInBackground(DataAccess ... da) 
    {
    	String result = null;
    	result = da[0].saveFile(getList(), this, getList().getHeaderListener() );
    	
    	return result;
    }

	
}
