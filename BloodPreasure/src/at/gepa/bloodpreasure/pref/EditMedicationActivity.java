package at.gepa.bloodpreasure.pref;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.medicine.MedicineAmountListAdapterSimple;
import at.gepa.bloodpreasure.medicine.MedicineAmountListAdapterSimple.MedicineAmountRemoveListener;
import at.gepa.lib.model.medicine.Medication.eMedicationTime;
import at.gepa.lib.model.medicine.Medicine;
import at.gepa.lib.model.medicine.MedicineAmount;
import at.gepa.lib.model.medicine.MedicineAmountList;
import at.gepa.lib.model.medicine.MedicineName;

public class EditMedicationActivity extends Activity implements MedicineAmountRemoveListener {

	private static MedicineAmountList _medicineAmountList;
	private static eMedicationTime medicationTime;
	private static ApplyListener applyListener;
	
	private MedicineAmountList medicineAmountList;
	private ListView medlistView;
	private MedicineAmountListAdapterSimple adapter;

	public EditMedicationActivity() 
	{
	}
	
	@Override
	public void onBackPressed()
	{
		MedicineAmountList newList = new MedicineAmountList();
		
		for( int i=0; i < adapter.getCount(); i++ )
		{
			MedicineAmount ma = adapter.getItem(i);
			newList.add(ma);
		}
		medicineAmountList.clear();
		medicineAmountList.merge(newList);
		applyListener.onApply( medicineAmountList );
		
		super.onBackPressed();

	}
	
	public void onCreate( Bundle save )
	{
		super.onCreate( save );
		
		medicineAmountList = (MedicineAmountList)_medicineAmountList.clone();
		
		setTitle( medicationTime.name() );
		
		setContentView(R.layout.activity_edit_medication);

		View incl = findViewById(R.id.editMedicationId);
		
		medlistView = (ListView)incl.findViewById(R.id.medicineListViewId);
		
		adapter = new MedicineAmountListAdapterSimple( this, R.layout.edit_medication_row_simple );
		MedicineAmountListAdapterSimple.setMedicineAmountRemoveListener(this);
        medlistView.setAdapter( adapter );
		
        for( MedicineAmount ma : medicineAmountList )
        {
        	adapter.add(ma);
        }
        MedicineAmountListAdapterSimple.setParentContext( this );
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.prefmed_menu, menu);
		//
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if( id == R.id.action_settingsmed_add )
		{
			onClick(null);
		}
		else if( id == R.id.action_settingsmed_cancel )
		{
			finish();
		}
		return true;
	}

	public interface ApplyListener
	{
		public void onApply(MedicineAmountList ma);
	}

	public static void setMedicineAmount( MedicineAmountList ma, eMedicationTime mt, ApplyListener applyListener) {
		EditMedicationActivity._medicineAmountList = ma;
		EditMedicationActivity.medicationTime = mt;
		EditMedicationActivity.applyListener = applyListener;
	}
	
	public void onClick(View v)
	{
		//add an new MedicineAmount
		MedicineAmount ma = new MedicineAmount(new Medicine(new MedicineName("")));
		if( medicineAmountList.add(ma) )
		{
			adapter.add(ma);
			adapter.notifyDataSetChanged();
			
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					int selIndex = adapter.getCount()-1;
			        medlistView.setSelection(selIndex);
			        View v = medlistView.getChildAt(selIndex);
			        if( v != null )
			        	adapter.startNew( v );
				}});
		}
	}
	
	@Override
	public void onRemove(MedicineAmount ma) {
    	if( ma == null )
    	{
    		Toast.makeText( this, "Kein Eintrag selektiert!", Toast.LENGTH_LONG).show();
    		return;
    	}
		adapter.remove(ma);
    	adapter.notifyDataSetChanged();
	}	
}

