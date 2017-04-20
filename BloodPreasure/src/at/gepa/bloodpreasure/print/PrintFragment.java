package at.gepa.bloodpreasure.print;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;
import at.gepa.bloodpreasure.print.PrintFragmentPagerAdapter.PrintConfig;

public abstract class PrintFragment extends Fragment {

	protected PrintConfig printConfig;
	protected RelativeLayout rootView;

	public PrintFragment() {
		this(null);
	}
	
	public PrintFragment(PrintConfig printConfig) {
		this.printConfig = printConfig;
	}
	public void updateFields( boolean intoData )
	{
	}
	public void execute() {
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if( outState != null )
		{
			outState.putSerializable("config", printConfig);
		}
	}
	@Override
	public void onCreate(Bundle outState)
	{
		super.onCreate(outState);
		
		if( outState != null )
		{
			this.printConfig = (PrintConfig) outState.getSerializable("config");
		}
	}
	
}
