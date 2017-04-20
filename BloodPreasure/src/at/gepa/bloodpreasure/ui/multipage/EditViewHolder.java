package at.gepa.bloodpreasure.ui.multipage;

import java.util.Calendar;
import java.util.HashMap;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class EditViewHolder {

	private HashMap<Object, EditText> views;
	public EditViewHolder() {
		views = new HashMap<Object, EditText>();
	}

	public void add(int page, EditText tv) {
		views.put(page, tv);
	}

	public String getValue(int page ) 
	{
		if( !views.containsKey(page) ) return "0";
		return views.get(page).getText().toString();
	}

	public void setText(int page, String seltags) {
		views.get(page).setText(seltags);
	}

	public java.util.Date getDate(int page) 
	{
		DatePicker dt = (DatePicker)views.get(page).getTag();
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dt.getCalendarView().getDate());
		
		TimePicker tp = (TimePicker) dt.getTag();
		cal.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour() );
		cal.set(Calendar.MINUTE, tp.getCurrentMinute() );
		
//		Calendar cal = Calendar.getInstance();
//		int year = dt.getYear();
//		int month = dt.getMonth();
//		int day = dt.getDayOfMonth();
//		int hourOfDay = 0;
//		int minute = 0;
//		int second = 0;
//		cal.set(year, month, day, hourOfDay, minute, second );
		return cal.getTime();
	}

}
