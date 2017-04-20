package at.gepa.bloodpreasure.exportimport.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import at.gepa.bloodpreasure.exportimport.ExportImportData;
import at.gepa.bloodpreasure.exportimport.ExportImportData.eFileType;
import at.gepa.bloodpreasure.exportimport.IExportImportTypListener;



public class ExportImportFragmentPagerAdapter extends FragmentPagerAdapter
implements IExportImportTypListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9222666009364064628L;
	private ExportImportData exportImportData;
	private ExportImportFragment fragments[];

	public ExportImportFragmentPagerAdapter(FragmentManager fm, ExportImportData exportImportData) {
		super(fm);
		this.exportImportData = exportImportData;
		fragments = new ExportImportFragment[getCount()];
	}

	@Override
	public Fragment getItem(int page) 
	{
		if( fragments[page] == null )
		{
			fragments[page] =  exportImportData.createInstance( page, (IExportImportTypListener)this);
		}
		
		return fragments[page];
	}

	@Override
	public int getCount() {
		return exportImportData.getCount();
	}

	@Override
	public void typeChanged(ExportImportData exportImportData, int page) 
	{
		if( page == 0 )
		{
			if( fragments[1] != null )
				fragments[1].reLayout();
		}
	}

	public void updateFields(boolean fromFieldToData) {
		for( int i=0; i < fragments.length; i++ )
			if( fragments[i] != null )
				fragments[i].updateFields( fromFieldToData );
		
	}

	@Override
	public void beforeTypeChanged(ExportImportData exportImportData, int page, eFileType t) {
		if( page == 0 )
		{
			if( fragments[1] != null )
				fragments[1].updateFields( t, true );
		}
		
	}

}
