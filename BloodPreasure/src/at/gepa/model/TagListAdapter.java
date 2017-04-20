package at.gepa.model;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.TextView;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.ui.TagListActivity;


public class TagListAdapter extends android.widget.ArrayAdapter<TagElement> {

	private LayoutInflater inflater;
	private OnItemSelectedListener onItemSelectedListener;

	public TagListAdapter(Context context, int resource, int textViewResourceId, List<TagElement> objects) {
		super(context, resource, textViewResourceId, objects);
		
		inflater = LayoutInflater.from(context);
		
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Planet to display
		TagElement te = (TagElement) this.getItem(position);

        // The child views in each row.
        CheckBox checkBox;
        TextView textView;

        // Create a new row view
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.taglist_row, null);

            // Find the child views.
            textView = (TextView) convertView.findViewById(R.id.taglist_name);
            checkBox = (CheckBox) convertView.findViewById(R.id.chkTag);
            checkBox.setFocusable(false);
            checkBox.setFocusableInTouchMode(false);
            
            // Optimization: Tag the row with it's child views, so we don't
            // have to
            // call findViewById() later when we reuse the row.
            convertView.setTag(new TagListActivity.TagViewHolder(textView, checkBox));

            final int _pos = position;
            // If CheckBox is toggled, update the planet it is tagged with.
            checkBox.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    CheckBox cb = (CheckBox) v;
                    TagElement te = (TagElement) cb.getTag();
                    te.setChecked(cb.isChecked());
                    if( cb.isChecked() )
                    	onItemSelectedListener.onItemSelected(null, cb, _pos, 0L);
                    else
                    	onItemSelectedListener.onNothingSelected(null);
                }
            });
        }
        // Reuse existing row view
        else
        {
            // Because we use a ViewHolder, we avoid having to call
            // findViewById().
        	TagListActivity.TagViewHolder viewHolder = (TagListActivity.TagViewHolder)convertView.getTag();
            checkBox = viewHolder.getCheckBox();
            textView = viewHolder.getTextView();
        }

        // Tag the CheckBox with the Planet it is displaying, so that we can
        // access the planet in onClick() when the CheckBox is toggled.
        checkBox.setTag(te);

        checkBox.setChecked(te.isChecked());
        textView.setText(te.getText() );

        return convertView;
    }

	public void remove(String result) {
		for( int i=0; i < getCount(); i++ )
		{
			TagElement te = getItem(i);
			if( te.getText().equalsIgnoreCase(result) )
			{
				super.remove(te);
				break;
			}
		}
	}

	public void setOnItemSelectedListener( android.widget.AdapterView.OnItemSelectedListener onItemSelectedListener) {
		this.onItemSelectedListener = onItemSelectedListener;
	}

}

