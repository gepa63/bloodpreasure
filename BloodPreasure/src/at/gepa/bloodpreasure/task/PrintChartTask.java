package at.gepa.bloodpreasure.task;

import android.os.AsyncTask;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.print.PrintExecuteFragment;
import at.gepa.bloodpreasure.print.PrintFragmentPagerAdapter.PrintConfig;

public class PrintChartTask extends AsyncTask<String, Integer, String>  {
	
	private PrintConfig config;
	private int orientation;
	private PrintExecuteFragment pdfOutput;
	private String pdfFile;
	
	public PrintChartTask(PrintConfig printConfig, int orientation, PrintExecuteFragment pdfOutput)
	{
		this.config = printConfig;
		this.orientation = orientation;
		this.pdfOutput = pdfOutput;
		pdfFile = null;
	}

    @Override
    protected void onPostExecute(String result) {
    	
    	pdfOutput.openPDFDocument( pdfFile );
    }
    @Override
    protected void onPreExecute() {
    	
    	int cnt = 50;
    	while( !MainActivityGrid.self.isChartExists() )
    	{
    		try {
				Thread.yield();
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
    		cnt--;
    		if( cnt < 0 ) break;
    	}
    }
    
	@Override
	protected String doInBackground(String... jobNames) 
	{
		try {
			pdfFile = config.createChartFile(orientation);
		} catch (Exception e) {
			e.printStackTrace();
			pdfFile = null;
		}
		return "done";
	}

}
