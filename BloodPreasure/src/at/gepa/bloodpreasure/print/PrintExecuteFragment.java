package at.gepa.bloodpreasure.print;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.print.PrintFragmentPagerAdapter.PrintConfig;
import at.gepa.bloodpreasure.task.PrintChartTask;


public class PrintExecuteFragment extends PrintFragment {

	private TextView txtPrintResults;
	private PrintManager printManager;

	public PrintExecuteFragment() {
		this(null);
	}
	public PrintExecuteFragment(PrintConfig printConfig) {
		super(printConfig);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = (RelativeLayout)inflater.inflate(R.layout.print_execute_fragment, container, false);
		
		txtPrintResults = (TextView)rootView.findViewById(R.id.txtPrintResults);
		
		txtPrintResults.setText("Warte auf Druckjob/PDF-Export...");
		
		return rootView;
	}
	
	public void execute() {
		
		super.execute();
		
		if( !somethingToExport() )
		{
			addText("Nothing to Export!");
			return;
		}
		addText("PDF Export");
		
		printManager = (PrintManager) MainActivityGrid.self.getSystemService(Context.PRINT_SERVICE);

		try
		{
			if( super.printConfig.isPrintBloodPreasureList() )
			{
				String jobName = this.getString(R.string.app_name) + " Dokument - List";
				printDocument( printConfig.createListFile(false), jobName, true );
			}
			if( super.printConfig.isPrintBloodPreasureListDetails() )
			{
				String jobName = this.getString(R.string.app_name) + " Dokument - Detallist";
				printDocument( printConfig.createListFile(true), jobName, true );
			}
			if( super.printConfig.isPrintBloodPreasureAnalyze() )
			{
				String jobName = this.getString(R.string.app_name) + " Dokument - Analyse";
				printDocument( printConfig.createAnalyzeFile(), jobName, true );
			}
			if( super.printConfig.isPrintBloodPreasureChart() && printConfig.isChartEnabled() )
			{
				String jobName = this.getString(R.string.app_name) + " Dokument - Chart";
				if( MainActivityGrid.self.isChartExists() )
				{
					printDocument( printConfig.createChartFile(MainActivityGrid.self.getResources().getConfiguration().orientation), jobName, false );
				}
				else
				{
					MainActivityGrid.self.makeChartVisible();
//					//1. set landscape
					PrintChartTask task = new PrintChartTask(printConfig, MainActivityGrid.self.getResources().getConfiguration().orientation, this);
//					
//					MainActivityGrid.self.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//					
					task.execute(jobName);
					
				}
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	private boolean somethingToExport() {
		boolean ret =	super.printConfig.isPrintBloodPreasureList() || 
						super.printConfig.isPrintBloodPreasureListDetails() ||
						super.printConfig.isPrintBloodPreasureAnalyze() || 
						( super.printConfig.isPrintBloodPreasureChart() && printConfig.isChartEnabled() );
		return ret;
	}
	private void addText(String s) {
		String crlf = "\r\n";
		CharSequence ct = txtPrintResults.getText(); 
		txtPrintResults.setText(ct + crlf + s );
		
	}
	private void printDocument( final String fileToPrint, String jobName, boolean orientationPortraet )
	{
		openPDFDocument(fileToPrint);
//		
//		PrintDocumentAdapter pda = new PrintDocumentAdapter(){
//
//			private int computePageCount(PrintAttributes newAttributes) {
//				return 1;
//			}
//
//			@Override
//			public void onLayout(PrintAttributes oldAttributes,
//					PrintAttributes newAttributes,
//					CancellationSignal cancellationSignal,
//					LayoutResultCallback callback, Bundle extras) {
//		        if (cancellationSignal.isCanceled()) {
//		            callback.onLayoutCancelled();
//		            addText("Abbruch");
//		            return;
//		        }
//		        PrintDocumentInfo.Builder b = new PrintDocumentInfo.Builder(fileToPrint);
//		        //int pages = computePageCount(newAttributes);
//		        //b.setPageCount(pages);
//		        PrintDocumentInfo pdi = b.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
//		        
//		        callback.onLayoutFinished(pdi, true);
//			}
//
//			@Override
//			public void onWrite(PageRange[] pages,
//					ParcelFileDescriptor destination,
//					CancellationSignal cancellationSignal,
//					WriteResultCallback callback) {
//		        InputStream input = null;
//		        OutputStream output = null;
//
//		        addText( "schreibe " + fileToPrint );
//		        try {
//
//		            input = new FileInputStream( fileToPrint );
//		            output = new FileOutputStream(destination.getFileDescriptor());
//
//		            byte[] buf = new byte[1024];
//		            int bytesRead;
//
//		            while ((bytesRead = input.read(buf)) > 0) {
//		                 output.write(buf, 0, bytesRead);
//		            }
//
//		            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
//
//		        } catch (FileNotFoundException ee){
//		            //Catch exception
//		        } catch (Exception e) {
//		            //Catch exception
//		        } finally {
//		            try {
//		                input.close();
//		                output.close();
//		            } catch (IOException e) {
//		                e.printStackTrace();
//		            }
//		        }
//			}
//		};
//		PrintAttributes.Builder b = new PrintAttributes.Builder();
//		
//		addText("Starte Druckjob/PDF-Export '" + jobName + "'");
//		printManager.print(jobName, pda, b.build() );
	}
	public void openPDFDocument(String file) 
	{
		if( file == null ) return;
		Uri filepath = Uri.fromFile(new java.io.File(file) );
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(filepath, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        } catch (Exception e) {
        	e.printStackTrace();
        	Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
	}

}
