package at.gepa.bloodpreasure.ui.multipage;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.pref.BloodPreasurePreferenceActivity;
import at.gepa.bloodpreasure.ui.TagListActivity;
import at.gepa.listener.IChangeListener;
import at.gepa.net.IElement;

public class EditFragment extends Fragment
implements IChangeListener
{

	private boolean changed;

	private int page;
	private IElement element;
	private EditViewHolder editViewHolder;

	private RelativeLayout rootView;

	private ElementDataToFieldMapper elementDataToFieldMapper;

	private IBloodPreasureChangeListener changeListener;

	private PrevNextInfo prevNextInfo;

	private IPageChangeListener iPageChangeListener;

	public RelativeLayout getRootView() {
		return rootView;
	}
	public EditFragment() {
		this(null, -1, null, null, null);
	}
	public EditFragment(IElement element, int page, IBloodPreasureChangeListener changeListener, PrevNextInfo prevNextInfo, IPageChangeListener pagelistener) {
		this.page = page;
		this.element = element;
		this.changeListener = changeListener;
		this.prevNextInfo = prevNextInfo;
		this.iPageChangeListener = pagelistener;
	}
	public String getTitle() {
		return element.getTitle(page);
	}
	public void onSaveInstanceState(Bundle outState)
	{
		if( outState != null )
		{
			outState.putInt("page", page);
			outState.putSerializable("element", element);
		}
		
	}
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if( savedInstanceState != null )
		{
			page = savedInstanceState.getInt("page");
			element = (IElement)savedInstanceState.getSerializable("element");
			MainActivityGrid.self.fillInfos(this, page );	
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = (RelativeLayout)inflater.inflate(R.layout.edit_fragment, container, false);

		rootView.setBackgroundResource( R.xml.background_editdlg );
	
		TextView title = (TextView)rootView.findViewById(R.id.editfragment_titleId);
		title.setText(element.getTitle(page));
		
		LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.editfragment_fields);
		
		elementDataToFieldMapper = new ElementDataToFieldMapper(changeListener, page, ll, element, getContext(), inflater, this);
	
		addPrevNextInfo(container);
		
		editViewHolder = elementDataToFieldMapper.addEditFields(  );
		
		return rootView;
	}

	private void addPrevNextInfo(ViewGroup container) {
		
		TextView tvLeft = (TextView)rootView.findViewById(R.id.infoLeftId);
		
		if( prevNextInfo.getPrevInfo() == null )
			tvLeft.setVisibility( TextView.INVISIBLE );
		else
			tvLeft.setText( "<- "+ prevNextInfo.getPrevInfo() );
//		tvLeft.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				iPageChangeListener.gotoPage( page-1 );
//			}
//		});

		TextView tvRight = (TextView)rootView.findViewById(R.id.infoRightId);
		if( prevNextInfo.getNextInfo() == null )
			tvRight.setVisibility( TextView.INVISIBLE );
		else
			tvRight.setText( prevNextInfo.getNextInfo() + " ->" );
//		tvRight.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				iPageChangeListener.gotoPage( page+1 );
//			}
//		});
		
		tvLeft.setTextSize(16f);
		tvRight.setTextSize(16f);
		tvLeft.setTextColor(Color.parseColor("#2f6699"));
		tvRight.setTextColor(Color.parseColor("#2f6699"));
	}
	public void apply() {
		if( isChanged() && tagListToSave != null )
			BloodPreasurePreferenceActivity.saveTags( tagListToSave );
		elementDataToFieldMapper.fromFieldtoElement( page, element, editViewHolder );
	}	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if( requestCode == TagListActivity.ACTIVITY_REQUEST_CODE )
		{
			if (resultCode == Activity.RESULT_OK)
			{
				String seltags = data.getStringExtra(TagListActivity.KEY);
				editViewHolder.setText( page, seltags);
				setChanged(true);
			}
		}
	}
	public boolean isChanged() {
		return changed;
	}
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	
	private ArrayList<String> tagListToSave = null;
	@Override
	public void saveTags(ArrayList<String> convertToTagStringArray) {
		tagListToSave = convertToTagStringArray;
		setChanged(true);
	}
	@Override
	public ArrayList<String> getTags() 
	{
		if( tagListToSave == null )
			tagListToSave = BloodPreasurePreferenceActivity.getTags();
		return tagListToSave;
	}
	public void startTagActivity(EditText txTags) {

		final String tags = txTags.getText().toString();
        MainActivityGrid.self.runOnUiThread(new Runnable() {
            @Override
            public void run() {
        		Intent i = new Intent( MainActivityGrid.self.getContext(), TagListActivity.class);
        		
//        		Bundle b = new Bundle();
//        		b.putString(TagListActivity.KEY, tags );
//        		i.putExtras(b);
        		startActivity(i);
        		//startActivityForResult(i, TagListActivity.ACTIVITY_REQUEST_CODE );
	            
            }
        });
	}
	public void setFillInfos(int page2, PrevNextInfo prevNextInfo2,
			IBloodPreasureChangeListener changeListener2) {
		this.changeListener = changeListener2;
		this.prevNextInfo = prevNextInfo2;
		
	}

}
