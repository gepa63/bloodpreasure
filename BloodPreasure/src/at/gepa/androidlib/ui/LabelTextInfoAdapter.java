package at.gepa.androidlib.ui;

import android.app.Activity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import at.gepa.androidlib.AndroidTools;


public class LabelTextInfoAdapter extends ArrayAdapter<LabelTextInfo> 
{
	public static class ViewHolder{
        
        public TextView txtLabel;
        public TextView txtText;
        public ImageView imgView;
        public ViewHolder()
        {
        }
		public void showOrHide(String txt, TextView tv) {
			tv.setText( txt );
			boolean showIt = true;
			if( txt == null || txt.isEmpty() )
			{
				showIt = false;
			}
			tv.setVisibility( showIt ? View.VISIBLE : View.GONE );
		}
		public void setOnLongClickListener(OnLongClickListener lcl) {
			txtText.setOnLongClickListener(lcl);
		}
		public void setTags() {
			txtLabel.setTag(txtText);
			txtText.setTag(txtLabel);
			if( imgView != null )
				imgView.setTag(txtText);
		}
		public void set(LabelTextInfo bp) {
			txtLabel.setText( bp.getLabel()+ (bp.getLabel().isEmpty() ? "" : LabelTextInfo.getLabelPrefix()) );
			
			txtText.setText( bp.getText() );
			if( bp.hasColor() )
				txtText.setTextColor( bp.getColor() );
			if( bp.hasTextSize() )
				txtText.setTextSize( bp.getTextSize() );
			
			if( bp.hasIconBitmap() )
			{
				LabelIconInfo lii = (LabelIconInfo)bp;
				imgView.setImageBitmap(lii.getIconBitmap());
			}
			
			if( bp.hasTextSize() )
				txtLabel.setTextSize( bp.getTextSize() );
			
			if( bp.hasLabelWidth() )
			{
				txtLabel.getLayoutParams().width = bp.getLabelWidth();
			}
		}
    }
	private int row_resource;
	private int idLabel;
	private int idInfo;
	private int idImage;
	

	public LabelTextInfoAdapter(Activity context, int row_resource, int idLabel, int idInfo, int idImage) {
		super(context, row_resource);
		this.row_resource = row_resource;
		this.idLabel = idLabel;
		this.idInfo = idInfo;
		this.idImage = idImage;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;//
		ViewHolder vh = null;
		
		LabelTextInfo bp = (LabelTextInfo)getItem(position);
		if( convertView != null )
		{
			view = convertView;
			vh = (ViewHolder)view.getTag();
		}
		else
		{
            view =  ((Activity)getContext()).getLayoutInflater().inflate(row_resource,parent,false);
            vh = bp.createViewHolder(view, idLabel, idInfo, idImage);
            
    		vh.setTags();
            view.setTag(vh);
		}
		vh.set( bp );
		
		if( bp.addLongClickMessangerToCopy())
		{
			OnLongClickListener lcl = new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					String text = null;
					String label = "";
					TextView tv = (TextView)v;
					if( tv.getId() == idLabel )
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
					
					AndroidTools.setClipboardData(getContext(), label, text);
					
//					int sdk = android.os.Build.VERSION.SDK_INT;
//					if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
//					    android.text.ClipboardManager clipboard = (android.text.ClipboardManager)((Activity)getContext()).getSystemService(Context.CLIPBOARD_SERVICE);
//					    clipboard.setText(text);
//					} else {
//					    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) ((Activity)getContext()).getSystemService(Context.CLIPBOARD_SERVICE); 
//					    android.content.ClipData clip = android.content.ClipData.newPlainText(label, text);
//					    clipboard.setPrimaryClip(clip);
//					}
					Toast.makeText( (Activity)getContext(), "Text wurde kopiert", Toast.LENGTH_SHORT).show();
					return false;
				}
			};
			vh.setOnLongClickListener(lcl);
			vh.txtLabel.setOnLongClickListener(lcl);
		}
		
		return view;
	}
}
