package at.gepa.bloodpreasure.ui.multipage;

import java.io.Serializable;

public class PrevNextInfo
implements Serializable
{
	private static final long serialVersionUID = 1677084133693872530L;
	private String prevInfo;
	private String nextInfo;
	public PrevNextInfo(String p, String n)
	{
		this.prevInfo =p;
		this.nextInfo =n;
	}
	public String getPrevInfo()
	{
		return this.prevInfo;
	}
	public String getNextInfo()
	{
		return this.nextInfo;
	}
}

