package at.gepa.bloodpreasure.task;

import android.os.AsyncTask;
import android.widget.Toast;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.lib.model.TensovalModel;
import at.gepa.net.IModel;


public class TensovalDownloadTask extends AsyncTask<TensovalModel, Integer, Integer> {

    private IModel listModel;
    private Exception lastError;

	public TensovalDownloadTask(IModel downloadModel) {
    	this.listModel = downloadModel;
    	lastError = null;
	}
	@Override
    protected void onPreExecute() {
        super.onPreExecute();
    }	
	@Override
	protected Integer doInBackground(TensovalModel... tmodel) {
		
		int anz = 0;
		try {
			anz = tmodel[0].importData(listModel, TensovalModel.DELIM_FIELD);
		} catch (Exception e) {
			e.printStackTrace();
			lastError = e;
		}
		return anz;
	}
	
	@Override
    public void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
	}
	
    @Override
    protected void onPostExecute(Integer anz) {
		String msg = null;
		if( lastError == null )
		{
			if( anz == 1 )
				msg = "Ein Datensatz importiert.";
			else if( anz == 0 )
				msg = "Keine Datensätze importiert.";
			else
				msg = anz + " Datensätze importiert.";
		}
		else
			msg = lastError.getMessage();
		Toast.makeText(MainActivityGrid.self, msg, Toast.LENGTH_LONG).show();
    	
    }	

}
