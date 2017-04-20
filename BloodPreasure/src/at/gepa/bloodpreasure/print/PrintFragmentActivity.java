package at.gepa.bloodpreasure.print;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;


public class PrintFragmentActivity extends FragmentActivity {

	private PrintFragmentPagerAdapter adapterViewPager;
	private ViewPager vpPager;

	public PrintFragmentActivity() {
	}

	@Override
	protected void onCreate(Bundle param) {
		super.onCreate(param);

		setContentView( at.gepa.bloodpreasure.R.layout.activity_print );

		vpPager = (ViewPager) findViewById(at.gepa.bloodpreasure.R.id.vpPagerPrintId);
		
		adapterViewPager = new PrintFragmentPagerAdapter(getSupportFragmentManager());
		vpPager.setAdapter(adapterViewPager);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuItem mi = menu.add("PDF-Export");
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mi.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				adapterViewPager.updateFields(true);
				vpPager.setCurrentItem(1);
				adapterViewPager.execute();
				return true;
			}
		});
		return true;
	}
	
}
