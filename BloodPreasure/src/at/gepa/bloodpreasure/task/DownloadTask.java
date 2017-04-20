package at.gepa.bloodpreasure.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.net.DataAccess;
import at.gepa.net.IBackgroundTask;
import at.gepa.net.IModel;

public class DownloadTask extends AsyncTask<DataAccess, Integer, String> 
implements IBackgroundTask
{
    protected ICallbackTaskListener context;
    protected PowerManager.WakeLock mWakeLock;
    private IModel list;

    public DownloadTask(ICallbackTaskListener context) {
        this.context = context;
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
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();
        context.getProgressDialog().setMessage(getStartMessage());
        context.getProgressDialog().show();
    }

    protected CharSequence getStartMessage() {
		return "Start downloading...";
	}


	@Override
    public void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        context.getProgressDialog().setIndeterminate(false);
        context.getProgressDialog().setMax(100);
        context.getProgressDialog().setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        try { context.getProgressDialog().dismiss(); } catch(Throwable t){}
        if (result != null)
            Toast.makeText(context.getContext(),"Download Fehler: "+result, Toast.LENGTH_LONG).show();
        else
        {
        	context.setModelDone(false);
        	context.doUIRefresh();
        	if( getEndMessage() != null )
        		Toast.makeText(context.getContext(),getEndMessage(), Toast.LENGTH_LONG).show();
        }
        
        MainActivityGrid.self.setGridRefreshing(false);
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
	
	private String endMessage;
	public void setEndMessage(String endMessage) {
		this.endMessage = endMessage;
	}
	public String getEndMessage() {
		return this.endMessage;
	}
	
}
