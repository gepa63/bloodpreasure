package at.gepa.bloodpreasure.medicine;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyze;
import at.gepa.bloodpreasure.medicine.DailyMedicationView.MedicationDataHolder;
import at.gepa.lib.model.medicine.Medication;
import at.gepa.lib.model.medicine.Medication.eMedicationTime;
import at.gepa.lib.model.medicine.MedicineAmountList;


public class MedicationPreference extends DialogPreference
implements MedicationDataHolder
{

	private Medication medicationPerDay;
	
	public MedicationPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		medicationPerDay = BloodPreasureAnalyze.getMedicationObject();
		setTextAndSummary();
	}
	public MedicationPreference(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		medicationPerDay = BloodPreasureAnalyze.getMedicationObject();
		setTextAndSummary();
	}
	
	private void setTheString(String value) {
		medicationPerDay = Medication.createInstance(value);
		updateMedication();
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if( which == DialogInterface.BUTTON_POSITIVE )
		{
			updateMedication();
		}
		super.onClick(dialog, which);
		
	}

	private void updateMedication() {
		BloodPreasureAnalyze.setMedicationObj(medicationPerDay);
		setTextAndSummary();
	}
	
	private void setTextAndSummary() {
		if( medicationPerDay == null ) return;
		String s = medicationPerDay.toString();
	    persistString(s);
	    
		String title = getTitle().toString();
		if( title.contains(":") )
		{
			String sa[] = title.split(":");
			title = sa[0].trim();
		}
	    
		setTitle(title + ": " + medicationPerDay.getMedicationEvalText() );
	    //setTitle(title + ": " + medicationPerDay.toString() );
	    setSummary( medicationPerDay.getSummary() );
	}
	@Override
	protected View onCreateDialogView() {

		DailyMedicationView llMain = new DailyMedicationView(getContext(), medicationPerDay, this);
		
		return llMain;
	}

	public void setMedication(String txt) {
		medicationPerDay = Medication.createInstance(txt);
	}

	public Medication getMedication() {
		return medicationPerDay;
	}
	
	/**
	   * Called when Android pauses the activity.
	   */
	@Override
	protected Parcelable onSaveInstanceState() 
	{
		if (isPersistent())
			return super.onSaveInstanceState();
		else
			return new SavedState(super.onSaveInstanceState());
	}
	/**
	 * Called when Android restores the activity.
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable state) 
	{
		if (state == null || !state.getClass().equals(SavedState.class)) 
		{
			super.onRestoreInstanceState(state);
			if( state instanceof SavedState)
				setTheString(((SavedState) state).dateValue);
		} 
		else if( state instanceof SavedState) 
		{
			SavedState s = (SavedState) state;
			super.onRestoreInstanceState(s.getSuperState());
			setTheString(s.dateValue);
		}
	}

	private static class SavedState extends BaseSavedState 
	{
		String dateValue;

		public SavedState(Parcel p) 
		{
			super(p);
			dateValue = p.readString();
		}

		public SavedState(Parcelable p) {
			super(p);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			//out.writeString(dateValue);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public void updateMedicine(eMedicationTime mt, MedicineAmountList medicineAmountList) {
		medicationPerDay.set(mt, medicineAmountList);
	}
	@Override
	public boolean useSmallText() {
		return true;
	}
}
