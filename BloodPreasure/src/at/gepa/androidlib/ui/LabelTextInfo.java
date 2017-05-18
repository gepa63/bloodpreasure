package at.gepa.androidlib.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import at.gepa.androidlib.ui.LabelTextInfoAdapter.ViewHolder;

public class LabelTextInfo {

	public static final float DEF_TEXT_SIZE = 20f;
	
	private String label;
	private String text;
	private int color;
	private boolean addALongClickMessangerToCopy;
	private float textSize;
	
	public int getColor() {
		return color;
	}
	public LabelTextInfo(String l, String t)
	{
		this(l, t, 0);
	}
	public LabelTextInfo()
	{
		this("","");
	}
	public LabelTextInfo(String l, String t, boolean add)
	{
		this(l, t, 0, add, DEF_TEXT_SIZE);
	}	
	public LabelTextInfo(String l, String t, int color)
	{
		this(l,t,color,false, DEF_TEXT_SIZE);
	}
	public LabelTextInfo(String l, String t, float textSize)
	{
		this(l,t,0,false, textSize);
	}
	public LabelTextInfo(String l, String t, float textSize, int labelWidth)
	{
		this(l,t,0,false, textSize);
		setLabelWidth(labelWidth);
	}
	public LabelTextInfo(String l, String t, boolean add, float txtsize)
	{
		this(l, t, 0, add, txtsize);
	}	
	public LabelTextInfo(String l, String t, int color, boolean add)
	{
		this(l, t, color, add, DEF_TEXT_SIZE);
	}
	public LabelTextInfo(String l, String t, int color, boolean add, float textSize)
	{
		this.label = l;
		this.text = t;
		this.color = color;
		addALongClickMessangerToCopy = add;
		this.textSize = textSize;
		setLabelWidth(0);
	}
	
	public String getLabel() {
		return label;
	}

	public String getText() {
		return text;
	}
	
	public boolean hasColor()
	{
		return color != 0;
	}
	public boolean addLongClickMessangerToCopy()
	{
		return addALongClickMessangerToCopy;
	}
	public boolean hasTextSize()
	{
		return textSize > 0;
	}
	public float getTextSize()
	{
		return textSize;
	}
	public String toString()
	{
		return getLabel() + ": " + getText();
	}
	
	private static String labelprefix = ":";
	public static String getLabelPrefix() {
		return labelprefix;
	}
	public static void setLabelPrefix(String p) {
		labelprefix = p;
	}
	
	private int labelWidth;
	public boolean hasLabelWidth() {
		return labelWidth > 0;
	}
	public int getLabelWidth() {
		return labelWidth;
	}
	public void setLabelWidth(int w ) {
		labelWidth = w;
	}
//	public ViewHolder createViewHolder(View view, int idLabel, int idInfo) {
//		return createViewHolder(view, idLabel, idInfo, 0);
//	}
	public ViewHolder createViewHolder(View view, int idLabel, int idInfo, int idImage) {
		ViewHolder vh = new ViewHolder();
        vh.txtLabel = (TextView)view.findViewById(idLabel);
		vh.txtText = (TextView)view.findViewById(idInfo);
		if( idImage > 0 )
			vh.imgView = (ImageView)view.findViewById(idImage);
		else
			vh.imgView = null;
		return vh;
	}

	public boolean hasIconBitmap() {
		return false;
	}
}
