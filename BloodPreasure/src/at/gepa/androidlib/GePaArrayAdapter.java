package at.gepa.androidlib;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GePaArrayAdapter<T> extends ArrayAdapter<T> {
	
	public static float DEFAUTL_TEXT_SIZE = 20f;

	private float textSize = DEFAUTL_TEXT_SIZE;
	
	public GePaArrayAdapter(Context context, int resource, float textSize ) {
		super(context, resource);
		this.textSize = textSize;
	}

	public GePaArrayAdapter(Context context, int resource, int textViewResourceId, float textSize) {
		super(context, resource, textViewResourceId);
		this.textSize = textSize;
	}

	public GePaArrayAdapter(Context context, int resource, T[] objects, float textSize) {
		super(context, resource, objects);
		this.textSize = textSize;
	}

	public GePaArrayAdapter(Context context, int resource, List<T> objects, float textSize) {
		super(context, resource, objects);
		this.textSize = textSize;
	}

	public GePaArrayAdapter(Context context, int resource,
			int textViewResourceId, T[] objects, float textSize) {
		super(context, resource, textViewResourceId, objects);
		this.textSize = textSize;
	}

	public GePaArrayAdapter(Context context, int resource,
			int textViewResourceId, List<T> objects, float textSize) {
		super(context, resource, textViewResourceId, objects);
		this.textSize = textSize;
	}

///////////////////////////////////////////////////////////
	public GePaArrayAdapter(Context context, int resource ) {
		super(context, resource);
	}

	public GePaArrayAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public GePaArrayAdapter(Context context, int resource, T[] objects) {
		super(context, resource, objects);
	}

	public GePaArrayAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
	}

	public GePaArrayAdapter(Context context, int resource,
			int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public GePaArrayAdapter(Context context, int resource,
			int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, objects);
	}
///////////////////////////////////////////////////////////
	
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =super.getView(position, convertView, parent);
        TextView textView=(TextView) view.findViewById(android.R.id.text1);
        
        setTextViewSize(textView);
		
        return view;
    }
	
	private void setTextViewSize(TextView textView) {
        textView.setTextSize(this.textSize);
        int padding = 6;
        int left = padding;
		int top = padding;
		int right = padding;
		int bottom = padding;
		textView.setPadding(left, top, right, bottom);
	}

	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view =super.getView(position, convertView, parent);
        
        setTextViewSize( (TextView)view );
        
        return view;
	}	
}
