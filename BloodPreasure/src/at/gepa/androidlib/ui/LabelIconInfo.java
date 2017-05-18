package at.gepa.androidlib.ui;

import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import at.gepa.androidlib.ui.LabelTextInfoAdapter.ViewHolder;


public class LabelIconInfo extends LabelTextInfo {
	
	private Bitmap iconBitmap;
	private boolean centerText;

	public Bitmap getIconBitmap() {
		return iconBitmap;
	}

	public void setIconBitmap(Bitmap iconBitmap) {
		this.iconBitmap = iconBitmap;
	}

	public LabelIconInfo(String text, Bitmap icon)
	{
		super("", text);
		this.iconBitmap = icon;
		setCenterText(false);
	}
	public LabelIconInfo(String text, Bitmap icon, int labelWidth)
	{
		super("", text, LabelTextInfo.DEF_TEXT_SIZE, labelWidth);
		this.iconBitmap = icon;
		setCenterText(false);
	}

	@Override
	public boolean hasIconBitmap() {
		return getIconBitmap() != null;
	}
	
	@Override
	public ViewHolder createViewHolder(View view, int idLabel, int idInfo, int idImage) 
	{
		ViewHolder vh = super.createViewHolder(view, idLabel, idInfo, idImage);
		if( hasIconBitmap() )
		{
			vh.txtLabel.setVisibility(View.GONE);
			vh.imgView.setVisibility(View.VISIBLE);
			
			if( getCenterText() )
			{
				LinearLayout.LayoutParams lllp =  (LinearLayout.LayoutParams)vh.txtText.getLayoutParams();
				lllp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
			}
		}
		return vh;
	}
	
	public void setCenterText(boolean flag)
	{
		centerText = flag;
	}
	public boolean getCenterText()
	{
		return centerText;
	}
}
