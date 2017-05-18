package at.gepa.bloodpreasure;

import java.util.ArrayList;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class EnableFunctionList {
	
	private static EnableFunctionList list;
	
	private ArrayList<MenuItem> menuItems;
	private ArrayList<View> buttons;
	private float defAplha;
	private int defAlphaBackground;
	
	public static EnableFunctionList getInstance()
	{
		if( list == null )
			list = new EnableFunctionList();
		return list;
	}
	private EnableFunctionList() {
		menuItems = new ArrayList<MenuItem>();
		buttons = new ArrayList<View>();
		defAlphaBackground = -1;
	}
	public void add(MenuItem item, boolean flag) {
		if( !menuItems.contains(item) )
		{
			menuItems.add(item);
			item.setEnabled(flag);
		}
	}
	public void add(View btId) {
		if( !buttons.contains(btId) )
		{
			if( defAlphaBackground < 0 )
			{
				defAlphaBackground = btId.getBackground().getAlpha();
				defAplha = btId.getAlpha();
			}
			buttons.add(btId);
		}
	}
	
	public void setEnabled( boolean flag )
	{
		for( MenuItem mi : menuItems )
		{
			String t = mi.getTitle().toString();
			mi.setEnabled(flag);
		}
		for( View v : buttons )
		{
			v.setEnabled(flag);
			ImageButton bt = (ImageButton)v;
			
			if( flag )
			{
				bt.setAlpha(defAplha);
				bt.getBackground().setAlpha(defAlphaBackground);
			}
			else
			{
				bt.setAlpha(0.4f);
				bt.getBackground().setAlpha(90);
			}
		}
	}
	public boolean isAddEnabled() {
		if( buttons.size() < 1 ) return false;
		return buttons.get(0).isEnabled();
	}
	
	

}
