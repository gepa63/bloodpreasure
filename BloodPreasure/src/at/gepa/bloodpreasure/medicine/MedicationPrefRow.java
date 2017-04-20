package at.gepa.bloodpreasure.medicine;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.gepa.bloodpreasure.medicine.DailyMedicationView.MedicationDataHolder;
import at.gepa.bloodpreasure.pref.EditMedicationActivity;
import at.gepa.lib.model.medicine.Medication;
import at.gepa.lib.model.medicine.Medication.eMedicationTime;
import at.gepa.lib.model.medicine.MedicineAmountList;

public class MedicationPrefRow extends LinearLayout
{

	private MedicineAmountList medicineAmountList;
	private TextView txtMedView;
	private boolean useSmallText;
	private eMedicationTime medicationTime;

	public MedicationPrefRow(final Context context, MedicineAmountList m, eMedicationTime mt, final MedicationDataHolder dlg) 
	{
		super(context);
		this.medicineAmountList = m;
		this.medicationTime = mt;
		
		setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams layp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
		layp.setMargins(4, 8, 4, 8);
		setLayoutParams( layp );

		float textSize = 24f;
		float textSizeSmall = 20f;
		int width = 250;
		int height = LinearLayout.LayoutParams.WRAP_CONTENT;// 60;
		TextView txtView = new TextView( context );
		txtView.setTextSize(textSize);
		txtView.setText( mt.name() + ": " );
		txtView.setWidth(width);
		//txtView.setHeight(height);
		addView( txtView, getChildCount() );
		
		txtMedView = new TextView( context );
		this.useSmallText = dlg.useSmallText();
		if( dlg.useSmallText() )
			txtMedView.setText( m.toShortString() );
		else
			txtMedView.setText( m.toString() );
		txtMedView.setTextSize(textSizeSmall);
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height, 1f );
		lp.setMargins(4, 2, 4, 2);
		lp.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
		txtMedView.setLayoutParams( lp );
		
		addView( txtMedView, getChildCount() );
		
		OnClickListener onclickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				EditMedicationActivity.setMedicineAmount( medicineAmountList, medicationTime, new EditMedicationActivity.ApplyListener(){

					@Override
					public void onApply(MedicineAmountList ma) {
						medicineAmountList = ma;
						dlg.updateMedicine( medicationTime, medicineAmountList);
						txtMedView.setText( medicineAmountList.toString() );
					}} );
				
				Intent i = new Intent(dlg.getContext(), EditMedicationActivity.class);
				try
				{
					getContext().startActivity(i);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		};
		txtMedView.setOnClickListener(onclickListener );
	}

	public void updateMedication(Medication med) {

		switch( medicationTime )
		{
		case Morgen:
			medicineAmountList = med.getMorning();
			break;
		case Mittag:
			medicineAmountList = med.getNoon();
			break;
		case Abend:
			medicineAmountList = med.getEvening();
			break;
		case Nacht:
			medicineAmountList = med.getNight();
			break;
		}
		
		if( useSmallText )
			txtMedView.setText( medicineAmountList.toShortString() );
		else
			txtMedView.setText( medicineAmountList.toString() );
		
	}

	public void getMedication(Medication med) 
	{
		med.set(medicationTime, medicineAmountList);
	}

}

