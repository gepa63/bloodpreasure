package at.gepa.bloodpreasure.print;

import java.io.Serializable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.pref.BloodPreasurePreferenceActivity;

public class PrintFragmentPagerAdapter extends FragmentPagerAdapter {
	
	public static class PrintConfig
	implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5700884661222964407L;
		public PrintConfig()
		{
			this(true, false, false, false);
		}
		private boolean printBloodPreasureList;
		public boolean isPrintBloodPreasureList() {
			return printBloodPreasureList;
		}

		public void setPrintBloodPreasureList(boolean printBloodPreasureList) {
			this.printBloodPreasureList = printBloodPreasureList;
		}

		public boolean isPrintBloodPreasureChart() {
			return printBloodPreasureChart;
		}

		public void setPrintBloodPreasureChart(boolean printBloodPreasureChart) {
			this.printBloodPreasureChart = printBloodPreasureChart;
		}

		public boolean isPrintBloodPreasureAnalyze() {
			return printBloodPreasureAnalyze;
		}

		public void setPrintBloodPreasureAnalyze(boolean printBloodPreasureAnalyze) {
			this.printBloodPreasureAnalyze = printBloodPreasureAnalyze;
		}

		private boolean printBloodPreasureChart;
		private boolean printBloodPreasureAnalyze;
		private boolean printBloodPreasureListDetails;
		private boolean chartIsEnabeld;
		
		public PrintConfig( boolean plist, boolean pchart, boolean pana, boolean pd )
		{
			this.printBloodPreasureAnalyze = pana;
			this.printBloodPreasureChart = pchart;
			this.printBloodPreasureList = plist;
			this.printBloodPreasureListDetails = pd;
			chartIsEnabeld = true;
		}

		public String createListFile(boolean details) throws Exception {
			return MainActivityGrid.self.createPdfListFile(details);
		}

		public String createAnalyzeFile() throws Exception {
			return MainActivityGrid.self.createPdfAnalyzeFile();
		}

		public String createChartFile(int orientation) throws Exception {
			return MainActivityGrid.self.createPdfChartFile(orientation);
		}

		public boolean isPrintBloodPreasureListDetails() {
			return printBloodPreasureListDetails;
		}

		public void setPrintBloodPreasureListDetals(boolean checked) {
			printBloodPreasureListDetails =  checked;			
		}

		public void setChartIsEnabled(boolean enabled) {
			chartIsEnabeld = enabled;
		}

		public boolean isChartEnabled() {
			return chartIsEnabeld;
		}
	}

	private PrintFragment[] fragments;
	private PrintConfig printConfig;
	public PrintFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		fragments = new PrintFragment[getCount()];
		
		printConfig = new PrintConfig();
		
		BloodPreasurePreferenceActivity.getPrintConfig( printConfig );
	}

	@Override
	public Fragment getItem(int page) 
	{
		if( fragments[page] == null )
		{
			if( page == 0 )
				fragments[page] = new PrintConfigFragment(printConfig);
			else
				fragments[page] = new PrintExecuteFragment(printConfig);
			fragments[page].updateFields(false);
		}
		return fragments[page];
	}

	@Override
	public int getCount() {
		return 2;
	}

	public void updateFields(boolean b) {
		if( fragments[0] == null && fragments[1] == null ) return;
		
		if( fragments[0] != null )
			fragments[0].updateFields(b);
		if( fragments[1] != null )
			fragments[1].updateFields(b);
		
		BloodPreasurePreferenceActivity.setPrintConfig(printConfig);
				
	}

	public void execute() {
		if( fragments[1] != null )
			fragments[1].execute();		
	}

}
