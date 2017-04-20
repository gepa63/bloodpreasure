package at.gepa.bloodpreasure.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.net.DataAccess;
import at.gepa.net.IBackgroundTask;
import at.gepa.net.IModel;

public class SilentDownloadTask extends AsyncTask<DataAccess, Integer, String> 
implements IBackgroundTask
{
    protected ICallbackTaskListener context;
    protected PowerManager.WakeLock mWakeLock;
    private IModel list;

    public SilentDownloadTask(ICallbackTaskListener context) {
        this.context = context;
        mWakeLock = null;
    }
    
    
    public final void doPublishProgress(int value) 
    {
    	super.publishProgress(value);
    }

    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user 
        // presses the power button during download
        PowerManager pm = (PowerManager) context._getSystemService(Context.POWER_SERVICE);
        if( pm == null ) pm = (PowerManager)MainActivityGrid.self._getSystemService(Context.POWER_SERVICE);
        if( pm != null )
        {
        	mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        	mWakeLock.acquire();
        }
    }

	@Override
    public void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(String result) {
    	if( mWakeLock != null )
    		mWakeLock.release();
       	context.setModelDone(false);
       	context.doUIRefresh();
    }
    @Override
    protected String doInBackground(DataAccess ... da) 
    {
    	String result = null;
    	
    	IModel model = context.getDownloadModel();
    	result = da[0].loadFile(this, model, model.getHeaderListener());
    	
    	return result;
    }
	public IModel getList() {
		if( list == null )
			setList(context.getUploadModel());
		return list;
	}

	public void setList(IModel list) {
		this.list = list;
	}
}
