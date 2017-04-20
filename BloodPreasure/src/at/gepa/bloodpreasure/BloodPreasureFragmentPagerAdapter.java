package at.gepa.bloodpreasure;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BloodPreasureFragmentPagerAdapter extends FragmentPagerAdapter {
	
	public static int NUM_ITEMS = 2;
	
	public BloodPreasureFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getCount() {
		return NUM_ITEMS;
	}
	@Override
    public Fragment getItem(int position) {
        switch (position) {
        case 0: // Fragment # 0 - This will show FirstFragment
        	return MainActivityGrid.setFragment(position, GridFragment.newInstance() );
        case 1: 
        	return MainActivityGrid.setFragment(position, ChartFragment.newInstance() );
        default:
        	return null;
        }
    }
    
    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
    	return "";
    }
}
