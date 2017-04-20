package at.gepa.bloodpreasure.ui.multipage;

import java.io.Serializable;
import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.net.IElement;

public class PageFragmentAdapter 
extends FragmentPagerAdapter
implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1348756636393071967L;
	private IElement element;
	private EditFragment fragments[];
	private int maxPages;
	private IBloodPreasureChangeListener changeListener;
	private ArrayList<PrevNextInfo> infoListPrevNext;
	private IPageChangeListener iPageChangeListener;

	public PageFragmentAdapter(FragmentManager fm, IElement e, int maxPages, IPageChangeListener iPageChangeListener) {
		super(fm);
		this.element = e;
		this.maxPages = maxPages;
		infoListPrevNext = new ArrayList<PrevNextInfo>();
		infoListPrevNext.add(new PrevNextInfo(null, BloodPreasure.COL_SYS )); //Datum+Zeit
		infoListPrevNext.add(new PrevNextInfo("Datum+Zeit", BloodPreasure.COL_DIA)); //Systolisch
		infoListPrevNext.add(new PrevNextInfo(BloodPreasure.COL_SYS, BloodPreasure.COL_PULS)); //Diastolisch
		infoListPrevNext.add(new PrevNextInfo(BloodPreasure.COL_DIA, BloodPreasure.COL_DESC)); //Puls
		infoListPrevNext.add(new PrevNextInfo(BloodPreasure.COL_PULS, BloodPreasure.COL_TAGS)); //Bemerkung
		infoListPrevNext.add(new PrevNextInfo(BloodPreasure.COL_DESC, BloodPreasure.COL_GEWICHT)); //Tags
		infoListPrevNext.add(new PrevNextInfo(BloodPreasure.COL_GEWICHT, BloodPreasure.COL_ZUCKER)); //Zucker
		infoListPrevNext.add(new PrevNextInfo(BloodPreasure.COL_ZUCKER, BloodPreasure.COL_TEMP)); //Zucker
		infoListPrevNext.add(new PrevNextInfo(BloodPreasure.COL_TEMP, BloodPreasure.COL_MED)); //Temperatur
		infoListPrevNext.add(new PrevNextInfo(BloodPreasure.COL_MED, null)); //Medikation
		this.iPageChangeListener = iPageChangeListener; 
	}

	@Override
	public Fragment getItem(int page) {
		if( fragments == null )
			fragments = new EditFragment[getCount()];
		if( fragments[page] == null )
			fragments[page] = new EditFragment(element, page, changeListener, infoListPrevNext.get(page), iPageChangeListener);
		return fragments[page];
	}

	@Override
	public int getCount() {
		return maxPages;
	}

	public void apply() {
		for( EditFragment f : fragments )
		{
			if( f == null ) continue;
			f.apply();
		}
	}

	public void addChangeListener(IBloodPreasureChangeListener iChangeListener) {
		this.changeListener = iChangeListener;
	}

	public void setFillInfos(EditFragment editFragment, int page,
			IPageChangeListener iPageChangeListener) {
		editFragment.setFillInfos( page, infoListPrevNext.get(page), changeListener );
		
	}

	public void applyCurrentPage(int i) {
		fragments[i].apply();
		
	}

}
