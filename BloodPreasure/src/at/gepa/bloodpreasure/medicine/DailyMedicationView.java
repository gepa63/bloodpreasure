package at.gepa.bloodpreasure.medicine;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import at.gepa.lib.model.medicine.Medication;
import at.gepa.lib.model.medicine.Medication.eMedicationTime;
import at.gepa.lib.model.medicine.MedicineAmountList;

public class DailyMedicationView extends LinearLayout {
	
	public static interface MedicationDataHolder
	{
		public void updateMedicine(eMedicationTime mt, MedicineAmountList medicineAmountList);
		public Context getContext();
		public boolean useSmallText();
	}

	public DailyMedicationView(Context context, Medication medicationPerDay, MedicationDataHolder dlg) {
		super(context);

		LinearLayout llMain = this;//new LinearLayout(getContext() );
		llMain.setOrientation(LinearLayout.VERTICAL);
		llMain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1) );
	
		MedicationPrefRow rowMorning = new MedicationPrefRow(getContext(), medicationPerDay.getMorning(), eMedicationTime.Morgen, dlg );
		llMain.addView(rowMorning);
		
		MedicationPrefRow rowNoon = new MedicationPrefRow(getContext(), medicationPerDay.getNoon(), eMedicationTime.Mittag, dlg );
		llMain.addView(rowNoon);
		
		MedicationPrefRow rowEvening = new MedicationPrefRow(getContext(), medicationPerDay.getEvening(), eMedicationTime.Abend, dlg );
		llMain.addView(rowEvening);

		MedicationPrefRow rowNight = new MedicationPrefRow(getContext(), medicationPerDay.getNight(), eMedicationTime.Nacht, dlg );
		llMain.addView(rowNight);
		
	}

	public void updateMedication(Medication med) {
		for( int i=0; i < getChildCount(); i++ )
		{
			View child = getChildAt(i);
			if( child instanceof MedicationPrefRow )
			{
				MedicationPrefRow r = (MedicationPrefRow)child;
				r.updateMedication(med);
			}
		}
	}

	public Object getMedication() {
		Medication med = new Medication();
		for( int i=0; i < getChildCount(); i++ )
		{
			View child = getChildAt(i);
			if( child instanceof MedicationPrefRow )
			{
				MedicationPrefRow r = (MedicationPrefRow)child;
				r.getMedication(med);
			}
		}
		return med;
	}

}
