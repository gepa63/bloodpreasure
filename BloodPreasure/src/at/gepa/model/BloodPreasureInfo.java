package at.gepa.model;

public class BloodPreasureInfo 
{
	private static final float DEF_TEXT_SIZE = 20f;
	private String label;
	private String text;
	private int color;
	private boolean addALongClickMessangerToCopy;
	private float textSize;
	
	public int getColor() {
		return color;
	}
	public BloodPreasureInfo(String l, String t)
	{
		this(l, t, 0);
	}	
	public BloodPreasureInfo(String l, String t, boolean add)
	{
		this(l, t, 0, add, DEF_TEXT_SIZE);
	}	
	public BloodPreasureInfo(String l, String t, int color)
	{
		this(l,t,color,false, DEF_TEXT_SIZE);
	}
	public BloodPreasureInfo(String l, String t, float textSize)
	{
		this(l,t,0,false, textSize);
	}
	public BloodPreasureInfo(String l, String t, boolean add, float txtsize)
	{
		this(l, t, 0, add, txtsize);
	}	
	public BloodPreasureInfo(String l, String t, int color, boolean add)
	{
		this(l, t, color, add, DEF_TEXT_SIZE);
	}
	public BloodPreasureInfo(String l, String t, int color, boolean add, float textSize)
	{
		this.label = l;
		this.text = t;
		this.color = color;
		addALongClickMessangerToCopy = add;
		this.textSize = textSize;
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
	
}
