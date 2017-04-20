package at.gepa.listener;

import java.util.ArrayList;


public interface IChangeListener
{
	public boolean isChanged();
	public void setChanged(boolean flag);
	public void saveTags(ArrayList<String> convertToTagStringArray);
	public ArrayList<String> getTags();

}
