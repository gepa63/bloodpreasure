package at.gepa.bloodpreasure.ui.multipage;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import at.gepa.bloodpreasure.EnableFunctionList;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyze;
import at.gepa.bloodpreasure.pref.BloodPreasurePreferenceActivity;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.lib.model.BloodPreasureTags;
import at.gepa.model.TagListUploadTask;
import at.gepa.net.IElement;

public class EditElementActivity extends FragmentActivity
implements IBloodPreasureChangeListener, IPageChangeListener
{
	
	public static final String KEY = "element";

	private IElement element;
	private PageFragmentAdapter adapterViewPager;

	private TextView analyseTextId;

	private ViewPager vpPager;

	private boolean markTagsAsUntilRevoke;

	public EditElementActivity() {
	}

	@Override
	protected void onCreate(Bundle param) {
		super.onCreate(param);
		
		MainActivityGrid.self.editElementActivity = this;
		setContentView( at.gepa.bloodpreasure.R.layout.activity_edit );
		
		analyseTextId = (TextView)findViewById(R.id.analyseTextId);
		analyseTextId.setText("");
		
		element = (IElement) MainActivityGrid.self.getCurrentEditable().clone();

		vpPager = (ViewPager) findViewById(at.gepa.bloodpreasure.R.id.vpPager);
		adapterViewPager = new PageFragmentAdapter(getSupportFragmentManager(), element, BloodPreasure.EDIT_COLUMNS, (IPageChangeListener)this);
		vpPager.setAdapter(adapterViewPager);
		if( element.isChanged() )
			vpPager.setCurrentItem(1);
		else
			vpPager.setCurrentItem(0);
		
		adapterViewPager.addChangeListener((IBloodPreasureChangeListener)this);
		
		setMarkTagsAsUntilRevoke( BloodPreasurePreferenceActivity.isMarkTagsAsUntilRevoke() );
		BloodPreasure bp = (BloodPreasure)element;
		BloodPreasureAnalyze ba = new BloodPreasureAnalyze( bp.getSystolisch(), bp.getDiastolisch() );
		analyseTextId.setText(ba.analyzeText());
		analyseTextId.setTextColor(ba.analyzeColor());
		
		ElementDataToFieldMapper.setMarkTagsAsUntilRevoke( new android.widget.CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setMarkTagsAsUntilRevoke( isChecked );
			}
		} );
	}

	protected void setMarkTagsAsUntilRevoke(boolean selected) {
		this.markTagsAsUntilRevoke = selected;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(at.gepa.bloodpreasure.R.menu.edit_menu, menu);
		
		menu.getItem(0).setEnabled( EnableFunctionList.getInstance().isAddEnabled() );
		return true;
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		
		MainActivityGrid.self.setCurrentEditable(null);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == at.gepa.bloodpreasure.R.id.action_apply) {
			
			adapterViewPager.apply();
			try {
				BloodPreasure bp = (BloodPreasure)element;
				if( markTagsAsUntilRevoke )
				{
					String tags = bp.getTags();
					if( tags != null && !tags.isEmpty() )
						BloodPreasureTags.saveTagsUntilRevokes(new TagListUploadTask( MainActivityGrid.self ), MainActivityGrid.getDataAccess().buildLink(BloodPreasureTags.FILENAME), tags );
					BloodPreasurePreferenceActivity.setMarkTagsAsUntilRevoke( markTagsAsUntilRevoke );
				}
				
				MainActivityGrid.self.save( bp, false );
				MainActivityGrid.self.setCurrentEditable(null);
				finish();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText( MainActivityGrid.self, e.getMessage(), Toast.LENGTH_LONG).show();
			}
			return false;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void dataChanged(IElement element, int page, Object newValue) {
		BloodPreasure bp = (BloodPreasure)element;
		
		bp.set(page, newValue);
		
		int syst = bp.getSystolisch();
		int diast = bp.getDiastolisch();
		
		switch( page )
		{
		case BloodPreasure.COL_IDX_DIASTOLISCH:
		case BloodPreasure.COL_IDX_SYSTOLISCH:
			break;
		default:
			return;
		}
		BloodPreasureAnalyze ba = new BloodPreasureAnalyze(syst, diast);
		
		analyseTextId.setText(ba.analyzeText());
		analyseTextId.setTextColor(ba.analyzeColor());
	}

	@Override
	public void gotoPage(int page) {
		adapterViewPager.applyCurrentPage(vpPager.getCurrentItem());
		if( page >= adapterViewPager.getCount() )
			page = 0;
		else if ( page < 0 )
			page = adapterViewPager.getCount()-1;
		vpPager.setCurrentItem(page);
	}

	public void setFillInfos(EditFragment editFragment, int page) {
		adapterViewPager.setFillInfos( editFragment, page, this);
		
	}
	
}
