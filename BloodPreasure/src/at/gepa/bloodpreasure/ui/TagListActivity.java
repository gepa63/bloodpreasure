package at.gepa.bloodpreasure.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import at.gepa.bloodpreasure.R;
import at.gepa.listener.IChangeListener;
import at.gepa.model.TagElement;
import at.gepa.model.TagListAdapter;

public class TagListActivity extends Activity
implements EditTextDialog.MyIChangeListener
{
	
	
	public static class TagViewHolder
    {
        private CheckBox checkBox;
        private TextView textView;

        public TagViewHolder()
        {
        	this(null, null);
        }

        public TagViewHolder(TextView textView, CheckBox checkBox)
        {
            this.checkBox = checkBox;
            this.textView = textView;
        }

        public CheckBox getCheckBox()
        {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox)
        {
            this.checkBox = checkBox;
        }

        public TextView getTextView()
        {
            return textView;
        }

        public void setTextView(TextView textView)
        {
            this.textView = textView;
        }
    }

	public static final String SEL_DELIM = ";";
	public static final int ACTIVITY_REQUEST_CODE = 999;
	public static final String KEY = "tags";	
	
	private ListView tagListView;
	private TagListAdapter taglistAdapter;
	private ImageButton btAdd;
	private ImageButton btApply;
	protected TagElement selectedItem;
	
	public TagListActivity()
	{
	}
	
	private static TagListActivity context;
	// private static IChangeListener changeListener;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		context = this;
		setContentView(R.layout.activity_tags_list);
		
//		btAdd = (ImageButton)findViewById(R.id.button);
//		btAdd.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				EditTextDialog edlg = new EditTextDialog(R.layout.prompts, "", context);
//				edlg.setListener( (EditTextDialog.MyIChangeListener)context );
//				edlg.show();
//			}
//		});
//
//		btApply = (ImageButton)findViewById(R.id.btApply);
//		btApply.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//
//				String selItems = "";
//				for( int i=0; i < taglistAdapter.getCount(); i++ )
//				{
//					TagElement te = taglistAdapter.getItem(i);
//					if( te.isChecked() )
//					{
//						if( !selItems.isEmpty() )
//							selItems += SEL_DELIM;
//						selItems += te.getText();
//					}
//				}
//				Intent data = new Intent();
//				data.putExtra(TagListActivity.KEY, selItems);
//				setResult(RESULT_OK, data);
//				finish();
//			}
//		});
//		
//		tagListView = (ListView)findViewById(R.id.taglist);
//		
//		tagListView.setLongClickable(true);
//		tagListView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) 
//			{
//				selectedItem = taglistAdapter.getItem(position);
//				return false;
//			}
//		});
//		tagListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
//				TagElement te = taglistAdapter.getItem(position);
//				te.toggleChecked();
//				TagViewHolder viewHolder = (TagViewHolder)item.getTag();
//				viewHolder.getCheckBox().setChecked( te.isChecked() );
//			}
//		});
//		
//		registerForContextMenu( tagListView );
//
//		taglistAdapter = new TagListAdapter( this, R.layout.taglist_row, R.id.taglist, getTags()  );
//		
//		tagListView.setAdapter(taglistAdapter);
//		
//		if( getIntent() != null )
//		{
//			Bundle b = getIntent().getExtras();
//			if( b != null )
//			{
//				Object o = b.get(KEY);
//				String str = b.getString(KEY);
//				selectTags( str );
//			}
//		}		
	}

//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
//	{
//	    super.onCreateContextMenu(menu, v, menuInfo);
//
//	    menu.add(0, GridFragment.CMD_EDIT, 0, R.string.menu_edit).setIcon(R.drawable.ic_launcher);
//	    menu.add(0, GridFragment.CMD_DEL, 10, R.string.menu_del);
//	    
//	}
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//
//	    if (item.getItemId() == GridFragment.CMD_DEL) 
//	    {
//	    	if( selectedItem != null )
//	    	{
//	    		taglistAdapter.remove( selectedItem );
//	    		changeListener.saveTags( convertToTagStringArray(taglistAdapter) );
//	    		taglistAdapter.notifyDataSetChanged();
//	    		selectedItem = null;
//	    	}
//	    	return true;
//	    }
//	    else if (item.getItemId() == GridFragment.CMD_EDIT) 
//	    {
//	    	if( selectedItem != null )
//	    	{
//				EditTextDialog edlg = new EditTextDialog(R.layout.prompts, selectedItem.getText(), context);
//				edlg.setListener( (EditTextDialog.MyIChangeListener)context );
//				edlg.show();
//	    		
//	    		taglistAdapter.notifyDataSetChanged();
//	    	}
//	    	return true;
//	    }
//	    return false;
//	}
//	private void selectTags(String selTags) {
//		if( selTags == null || selTags.isEmpty() ) return;
//		
//		String sa [] = selTags.split(SEL_DELIM);
//		boolean foundInTagList = false;
//		boolean saveTags = false;
//		for( int i=0; i < sa.length; i++ )
//		{
//			foundInTagList = false;
//			for( int y = 0; y < taglistAdapter.getCount(); y++ )
//			{
//				TagElement te = taglistAdapter.getItem(y);
//				if( te.getText().equalsIgnoreCase(sa[i]) )
//				{
//					te.setChecked(true);
//					foundInTagList = true;
//					break;
//				}
//			}
//			if( !foundInTagList )
//			{
//				TagElement te = new TagElement(sa[i]);
//				taglistAdapter.add(te);
//				te.setChecked(true);
//				saveTags = true;
//			}
//		}
//		
//		if( saveTags )
//			changeListener.saveTags( convertToTagStringArray(taglistAdapter) );
//	}
//
//	private ArrayList<String> convertToTagStringArray( TagListAdapter adapter) 
//	{
//		ArrayList<String> list = new ArrayList<String>();
//		for( int i=0; i < adapter.getCount(); i++ )
//		{
//			list.add( adapter.getItem(i).getText());
//		}
//		return list;
//	}
//
//	private ArrayList<TagElement> getTags() {
//		
//		if( changeListener == null ) return new ArrayList<TagElement>();
//		ArrayList<TagElement> l = toTagElements( changeListener.getTags() );
//		return l;
//	}
//
//	private ArrayList<TagElement> toTagElements(ArrayList<String> tags) {
//		ArrayList<TagElement> list = new ArrayList<TagElement>();
//		for( String tag : tags )
//			list.add(new TagElement(tag));
//		return list;
//	}

	@Override
	public void newEntry(EditTextDialog dlg) 
	{
//		if( dlg.isOKClicked() )
//		{
//			for( int i=0; i < taglistAdapter.getCount(); i++ )
//			{
//				TagElement te = taglistAdapter.getItem(i);
//				if( te.getText().equalsIgnoreCase(dlg.getResult() ))
//				{
//					Toast.makeText(this, dlg.getResult() + " existiert schon!", Toast.LENGTH_LONG).show();
//					return;
//				}
//			}
//			TagElement te = new TagElement(dlg.getResult());
//			te.setChecked(true);
//			taglistAdapter.add(te);
//			changeListener.saveTags( convertToTagStringArray(taglistAdapter) );
//			taglistAdapter.notifyDataSetChanged();
//		}
	}

	@Override
	public void editEntry(EditTextDialog dlg) {
//    	if( selectedItem != null )
//    	{
//    		selectedItem.setText(dlg.getResult());
//    		changeListener.saveTags( convertToTagStringArray(taglistAdapter) );
//			taglistAdapter.notifyDataSetChanged();
//			selectedItem = null;
//    	}
	}

	public static void setChangeListener(IChangeListener iChangeListener) {
		//changeListener = iChangeListener;
	}
}
