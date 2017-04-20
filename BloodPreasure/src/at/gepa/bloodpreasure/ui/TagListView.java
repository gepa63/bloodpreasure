package at.gepa.bloodpreasure.ui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.pref.BloodPreasurePreferenceActivity;
import at.gepa.bloodpreasure.ui.EditTextDialog.MyIChangeListener;
import at.gepa.lib.tools.Util;
import at.gepa.listener.IChangeListener;
import at.gepa.model.TagElement;
import at.gepa.model.TagListAdapter;

public class TagListView extends ListView {

	private static IChangeListener changeListener;
	private TagListAdapter taglistAdapter;
	public TagListView(Context context, android.widget.AdapterView.OnItemSelectedListener onItemSelectedListener) {
		super(context);
		init(onItemSelectedListener);
	}

	public TagListView(Context context, AttributeSet attrs, android.widget.AdapterView.OnItemSelectedListener onItemSelectedListener) {
		super(context, attrs);
		init(onItemSelectedListener);
	}

	public TagListView(Context context, AttributeSet attrs, int defStyleAttr, android.widget.AdapterView.OnItemSelectedListener onItemSelectedListener) {
		super(context, attrs, defStyleAttr);
		init(onItemSelectedListener);
	}

	public TagListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, android.widget.AdapterView.OnItemSelectedListener onItemSelectedListener) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(onItemSelectedListener);
	}

	private void init(android.widget.AdapterView.OnItemSelectedListener onItemSelectedListener) {
		
		setLongClickable(true);
		setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				editElement( taglistAdapter.getItem(position) );
				return false;
			}
		});
		
		taglistAdapter = new TagListAdapter( getContext(), R.layout.taglist_row, R.id.taglist, getTags()  );
		taglistAdapter.setOnItemSelectedListener(onItemSelectedListener);
		
		super.setAdapter(taglistAdapter);	
	}
	
	public ArrayList<String> getCheckedTags( boolean onlyChecked )
	{
		return convertToTagStringArray( (TagListAdapter)getAdapter(), onlyChecked );
	}

	private ArrayList<String> convertToTagStringArray( TagListAdapter adapter, boolean onlyChecked) 
	{
		ArrayList<String> list = new ArrayList<String>();
		for( int i=0; i < adapter.getCount(); i++ )
		{
			if( onlyChecked == false || adapter.getItem(i).isChecked() )
				list.add( adapter.getItem(i).getText());
		}
		return list;
	}

	private ArrayList<TagElement> getTags() {
		
		ArrayList<String> fl = changeListener.getTags();
		ArrayList<TagElement> l = toTagElements( fl );
		return l;
	}

	private ArrayList<TagElement> toTagElements(ArrayList<String> tags) {
		ArrayList<TagElement> list = new ArrayList<TagElement>();
		for( String tag : tags )
		{
			TagElement te = new TagElement(tag); 
			if( !list.contains(te))
				list.add(te);
		}
		return list;
	}
	public static void setChangeListener(IChangeListener iChangeListener) {
		changeListener = iChangeListener;
	}

	public void addElement() {

		editElement(null);
	}
	private void editElement( final TagElement e )
	{
		EditTextDialog edlg = new EditTextDialog(R.layout.prompts, (e == null ? "" : e.getText() ), getContext());
		edlg.setListener(new MyIChangeListener(){

			@Override
			public void newEntry(EditTextDialog dlg) {
				TagElement e = new TagElement(dlg.getResult());
				taglistAdapter.add(e);
				taglistAdapter.notifyDataSetChanged();
				changeListener.setChanged(true);
				changeListener.saveTags(getCheckedTags(false));
			}

			@Override
			public void editEntry(EditTextDialog dlg) {
				e.setText(dlg.getResult());
				taglistAdapter.notifyDataSetInvalidated();	
				taglistAdapter.notifyDataSetChanged();
				changeListener.setChanged(true);
				changeListener.saveTags(getCheckedTags(false));
			}} );
		edlg.show();
	}

	public void setChecked(String tags) 
	{
		boolean doUpdate = false;
		ArrayList<String> tagList = Util.convertToArray(tags, BloodPreasurePreferenceActivity.TAG_DELIMITER);
		for( int i = 0; i < taglistAdapter.getCount(); i++ )
		{
			TagElement e = taglistAdapter.getItem(i);
			for( String s : tagList )
			{
				if( e.getText().equalsIgnoreCase(s))
				{
					e.setChecked(true);
					doUpdate = true;
					break;
				}
			}
		}
		if( doUpdate )
		{
			taglistAdapter.notifyDataSetInvalidated();
			taglistAdapter.notifyDataSetChanged();
		}
	}
	
}
