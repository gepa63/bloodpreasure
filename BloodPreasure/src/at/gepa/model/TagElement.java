package at.gepa.model;


public class TagElement {
	private boolean checked;
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	private String text;
	
	public TagElement(String t)
	{
		text = t;
		setChecked(false);
	}

	public void toggleChecked() {
		setChecked( !isChecked() );
	}
	
	public boolean equals(Object o)
	{
		if( o == null ) return false;
		if( text == null ) return false;
		if( o instanceof TagElement )
			o = ((TagElement)o).getText();
		return text.equalsIgnoreCase(o.toString());
	}
}
