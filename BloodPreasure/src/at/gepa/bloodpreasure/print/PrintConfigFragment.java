package at.gepa.bloodpreasure.print;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.print.PrintFragmentPagerAdapter.PrintConfig;


public class PrintConfigFragment extends PrintFragment {

	public PrintConfigFragment() {
		this(null);
	}
	public PrintConfigFragment(PrintConfig printConfig) {
		super(printConfig);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = (RelativeLayout)inflater.inflate(R.layout.print_config_fragment, container, false);
		
		updateFields(false);
		
		return rootView;
	}

	private void updateValue(int resource, boolean value, boolean toData) 
	{
		if( rootView == null ) return;
		CheckBox cb = (CheckBox)rootView.findViewById(resource);
		if( toData )
		{
			if( resource == R.id.cbPrintBloodPreasureList )
				printConfig.setPrintBloodPreasureList(cb.isChecked());
			else if( resource == R.id.cbPrintBloodPreasureListDetails )
				printConfig.setPrintBloodPreasureListDetals(cb.isChecked());
			else if( resource == R.id.cbPrintBloodPreasureAnalyze )
				printConfig.setPrintBloodPreasureAnalyze(cb.isChecked());
			else if( resource == R.id.cbPrintBloodPreasureChart )
			{
				printConfig.setPrintBloodPreasureChart(cb.isChecked());
				printConfig.setChartIsEnabled( cb.isEnabled() );
			}
		}
		else
		{
			if( resource == R.id.cbPrintBloodPreasureChart )
			{
				if( !MainActivityGrid.self.isChartVisible() )
				{
					cb.setEnabled(false);
				}
			}	
			cb.setChecked( value );
		}
	}
	public void updateFields( boolean intoData )
	{
		updateValue( R.id.cbPrintBloodPreasureList, printConfig.isPrintBloodPreasureList(), intoData );
		updateValue( R.id.cbPrintBloodPreasureChart, printConfig.isPrintBloodPreasureChart(), intoData );
		updateValue( R.id.cbPrintBloodPreasureAnalyze, printConfig.isPrintBloodPreasureAnalyze(), intoData );
		updateValue( R.id.cbPrintBloodPreasureListDetails, printConfig.isPrintBloodPreasureListDetails(), intoData );
		
	}

}
