package at.gepa.bloodpreasure.ui;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.model.BloodPreasureInfo;



public class BloodPreasureInfoAdapter extends ArrayAdapter<BloodPreasureInfo> 
{
	public static class ViewHolder{
        
        public TextView txtLabel;
        public TextView txtText;
		public void showOrHide(String txt, TextView tv) {
			tv.setText( txt );
			boolean showIt = true;
			if( txt == null || txt.isEmpty() )
			{
				showIt = false;
			}
			tv.setVisibility( showIt ? View.VISIBLE : View.GONE );
		}
    }
	

	public BloodPreasureInfoAdapter(Context context, int resource) {
		super(context, resource);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;//
		ViewHolder vh = null;
		
		if( convertView != null )
		{
			view = convertView;
			vh = (ViewHolder)view.getTag();
		}
		else
		{
            view =  MainActivityGrid.self.getLayoutInflater().inflate(R.layout.bloodpreasureinfo_row,parent,false);
            vh = new ViewHolder();
            vh.txtLabel = (TextView)view.findViewById(R.id.cell_Label);
            vh.txtText = (TextView)view.findViewById(R.id.cell_Text);
            view.setTag(vh);
		}
		BloodPreasureInfo bp = (BloodPreasureInfo)getItem(position);
		vh.txtLabel.setText( bp.getLabel()+ (bp.getLabel().isEmpty() ? "" : ":") );
		vh.txtLabel.setTag(vh.txtText);
		vh.txtText.setTag(vh.txtLabel);
		vh.txtText.setText( bp.getText() );
		if( bp.hasColor() )
			vh.txtText.setTextColor( bp.getColor() );
		if( bp.hasTextSize() )
		{
			vh.txtLabel.setTextSize( bp.getTextSize() );
			vh.txtText.setTextSize( bp.getTextSize() );
		}
		if( bp.addLongClickMessangerToCopy())
		{
			OnLongClickListener lcl = new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					String text = null;
					String label = "";
					TextView tv = (TextView)v;
					if( tv.getId() == R.id.cell_Label )
					{
						label = tv.getText().toString();
						tv = (TextView)tv.getTag();
					}
					else
					{
						label = ((TextView)tv.getTag()).getText().toString();
					}
					text = tv.getText().toString();
					if( label.equalsIgnoreCase("kontakt:") )
					{
						text = text.replace(" ", "");
					}	
					int sdk = android.os.Build.VERSION.SDK_INT;
					if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
					    android.text.ClipboardManager clipboard = (android.text.ClipboardManager)MainActivityGrid.self.getSystemService(Context.CLIPBOARD_SERVICE);
					    clipboard.setText(text);
					} else {
					    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) MainActivityGrid.self.getSystemService(Context.CLIPBOARD_SERVICE); 
					    android.content.ClipData clip = android.content.ClipData.newPlainText(label, text);
					    clipboard.setPrimaryClip(clip);
					}
					Toast.makeText(MainActivityGrid.self, "Text wurde kopiert", Toast.LENGTH_SHORT).show();
					return false;
				}
			};
			vh.txtText.setOnLongClickListener(lcl);
			vh.txtLabel.setOnLongClickListener(lcl);
		}
		
		return view;
	}
}
