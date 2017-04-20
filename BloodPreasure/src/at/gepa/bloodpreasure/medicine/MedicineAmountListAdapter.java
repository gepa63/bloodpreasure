package at.gepa.bloodpreasure.medicine;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.lib.model.medicine.Medicine;
import at.gepa.lib.model.medicine.Medicine.MedicineQuantity;
import at.gepa.lib.model.medicine.Medicine.eMedicinType;
import at.gepa.lib.model.medicine.MedicineAmount;
import at.gepa.lib.model.medicine.MedicineAmount.eMedicationQuantity;
import at.gepa.lib.model.medicine.MedicineAmountList;
import at.gepa.lib.model.medicine.MedicineName;


public class MedicineAmountListAdapter extends ArrayAdapter<MedicineAmount> {

	public MedicineAmountListAdapter( Context activity, int layout )
	{
		super(activity, layout);
	}
	
	public static class ViewHolder{
		public ArrayAdapter<eMedicationQuantity> dataAdapterMedQuant;
		public ArrayAdapter<eMedicinType> dataAdapterMedType;
		public AutoCompleteTextView editTextMed; 
		public EditText editTextMedQuantity;
		public EditText editTextMedQuantityUnit;
		public Spinner spinnerMedType;
		public Button txtButtonMed;
		public ArrayAdapter<MedicineName> dataAdapterMedicinName;
		public Spinner spinnerMedQuant;
		public MedicineAmount medicineAmount;
		private static ApplyListener applyListener;
		
		public void updateMedText() {
			MedicineName medicineName = getCurrentMedicineName();
			Medicine currMed = Medicine.findMed(medicineName);
			if( currMed == null )
				currMed = new Medicine(medicineName);
			MedicineQuantity mq = currMed.getQuantity();
			if( mq == null )
			{
				mq = new MedicineQuantity(getQuantity(), editTextMedQuantityUnit.getText().toString() );
				currMed.setQuantity( mq );
			}
			else
			{
				mq.setQuantity(getQuantity());
				mq.setQuantityUnit(editTextMedQuantityUnit.getText().toString() );
			}
			medicineAmount.setMedicine( currMed );
		}

		public OnClickListener createTxtButtonMedClickListener() {
			return 	new OnClickListener() {
					@Override
				public void onClick(View v) {
				
					updateMedText();
					dataAdapterMedicinName.notifyDataSetChanged();
				}
			};
		}
		public MedicineName getCurrentMedicineName() 
		{
			MedicineName medicineName = null;
			int pos = editTextMed.getListSelection();
			if( pos >= 0 )
				medicineName = (MedicineName)dataAdapterMedicinName.getItem(pos);
			else
				medicineName = MedicineName.createInstance(editTextMed.getText().toString());
			return medicineName;
		}
	
		protected Medicine getNewMedicine(int positionType) {
			float quantity = getQuantity();
			return new Medicine( getCurrentMedicineName(), dataAdapterMedType.getItem(positionType), new MedicineQuantity( quantity, this.editTextMedQuantityUnit.getText().toString()) );
		}
	
		private float getQuantity() {
			String str = this.editTextMedQuantity.getText().toString();
			str = str.replace(',', '.');
			if( str.isEmpty() )
				str = "0";
			return  Float.parseFloat(str);
		}
		
		public static interface ApplyListener
		{
			public void onApply( MedicineAmountList ma );
		}

		public OnItemSelectedListener creatEditTextMedOnItemSelectListener() {
			return new OnItemSelectedListener() {
    			@Override
    			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
    			{
    				MedicineName medName = dataAdapterMedicinName.getItem(position);
    				Medicine medSel = Medicine.findMed( medName );
    				
    				if( medSel != null )
    				{
    					medicineAmount.setMedicine( medSel );
    					editTextMedQuantity.setText( "" + medSel.getQuantity().getQuantity() );
    					editTextMedQuantityUnit.setText( medSel.getQuantity().getQuantityUnit() );
    				}
    			}
    			
    			@Override
    			public void onNothingSelected(AdapterView<?> arg0) {
    			}
    		};
		}

		public OnItemSelectedListener createSpinnerMedQuantOnItemSelectListener() {
			return new OnItemSelectedListener() {

    			@Override
    			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    				if( selectedItemView != null )
    					((TextView) selectedItemView).setTextColor(Color.BLACK);
    				medicineAmount.setQuantity( dataAdapterMedQuant.getItem(position) );
    			}

    			@Override
    			public void onNothingSelected(AdapterView<?> arg0) {
    			}
    		};
		}

		public OnItemSelectedListener createSpinnerMedTypOnItemSelectListener() {
			return new OnItemSelectedListener() {

    			@Override
    			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    				if( selectedItemView != null )
    					((TextView) selectedItemView).setTextColor(Color.BLACK);
    				if( medicineAmount.getMedicine() == null )
    					medicineAmount.setMedicine( getNewMedicine(position) );
    				else
    					medicineAmount.getMedicine().setType( dataAdapterMedType.getItem(position) );
    			}

    			@Override
    			public void onNothingSelected(AdapterView<?> arg0) {
    			}
    		};
		}

		public TextWatcher createEditTextMedQuantityChangeListener() {
			return new TextWatcher(){

    			@Override
    			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    			}

    			@Override
    			public void onTextChanged(CharSequence s, int start, int before, int count) {
    			}

    			@Override
    			public void afterTextChanged(Editable s) 
    			{
    				if( medicineAmount.getMedicine() == null )
    					medicineAmount.setMedicine( getNewMedicine(spinnerMedType.getSelectedItemPosition()) );
    				else
    				{
    					medicineAmount.getMedicine().getQuantity().setQuantity(getQuantity());
    				}
    			}
    		};
		}

		public TextWatcher createEditTextMedQuantUnitChangeListener() {
			return new TextWatcher(){

    			@Override
    			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    			}

    			@Override
    			public void onTextChanged(CharSequence s, int start, int before, int count) {
    			}

    			@Override
    			public void afterTextChanged(Editable s) 
    			{
    				if( medicineAmount.getMedicine() == null )
    					medicineAmount.setMedicine( getNewMedicine(spinnerMedType.getSelectedItemPosition()) );
    				else
    				{
    					medicineAmount.getMedicine().getQuantity().setQuantityUnit(s.toString());
    				}
    			}
    		};
		}

		public OnFocusChangeListener createOnFocusChangedListener() {
			return new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if( hasFocus )
					{
						String s = editTextMed.getText().toString(); 
						editTextMed.setSelection(0, s.length());
					}
					else
					{
						String s = editTextMed.getText().toString();
						MedicineName mn = MedicineName.createInstance(s);
						if( dataAdapterMedicinName.getPosition(mn) < 0 )
						{
							dataAdapterMedicinName.add(mn);
							dataAdapterMedicinName.notifyDataSetChanged();
						}
					}
				}
			};
		}
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;//
		ViewHolder vh = null;
		
		if( convertView != null )
		{
			view = convertView;
			vh = (ViewHolder)view.getTag();
		}
		else
		{
            view =  MainActivityGrid.self.getLayoutInflater().inflate(R.layout.edit_medication_row, parent, false);
            vh = new ViewHolder();
            view.setTag(vh);

    		vh.spinnerMedType = (Spinner)view.findViewById(R.id.spinnerMedType);
    		vh.editTextMed = (AutoCompleteTextView)view.findViewById(R.id.txtMedName);
    		vh.editTextMed.setSelectAllOnFocus(true);
    		vh.editTextMedQuantity = (EditText)view.findViewById(R.id.txtMedQuantity);
    		vh.editTextMedQuantityUnit = (EditText)view.findViewById(R.id.txtMedQuantityUnit);
    		vh.spinnerMedQuant = (Spinner)view.findViewById(R.id.medicationQuantityTakingId);

    		vh.txtButtonMed = (Button)view.findViewById(R.id.txtButtonMed);

    		vh.txtButtonMed.setOnClickListener(vh.createTxtButtonMedClickListener());
    		
    		vh.medicineAmount = getItem(position);
    		/////////////////////////////////////
    		
    		vh.dataAdapterMedicinName = new ArrayAdapter<MedicineName>(super.getContext(), android.R.layout.simple_spinner_item, Medicine.getNameList() );
    		vh.dataAdapterMedicinName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		vh.editTextMed.setAdapter(vh.dataAdapterMedicinName);
    		
    		//vh.editTextMed.setOnFocusChangeListener(vh.createOnFocusChangedListener());
    		
    		vh.editTextMed.setOnItemSelectedListener(vh.creatEditTextMedOnItemSelectListener() );
    		
    		ArrayList<eMedicationQuantity> listMedQuant = new ArrayList<eMedicationQuantity>();
    		listMedQuant.add(eMedicationQuantity.Ganze);
    		listMedQuant.add(eMedicationQuantity.Halbe);
    		listMedQuant.add(eMedicationQuantity.Viertel);
    		vh.dataAdapterMedQuant = new ArrayAdapter<eMedicationQuantity>(super.getContext(), android.R.layout.simple_spinner_item, listMedQuant);
    		vh.dataAdapterMedQuant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		vh.spinnerMedQuant.setAdapter(vh.dataAdapterMedQuant);
    		vh.spinnerMedQuant.setOnItemSelectedListener(vh.createSpinnerMedQuantOnItemSelectListener());
    		
    		ArrayList<eMedicinType> listMedType = new ArrayList<eMedicinType>();
    		listMedType.add(eMedicinType.Tablette);
    		listMedType.add(eMedicinType.Spray);
    		listMedType.add(eMedicinType.Tropfen);
    		vh.dataAdapterMedType = new ArrayAdapter<eMedicinType>(super.getContext(), android.R.layout.simple_spinner_item, listMedType);
    		vh.dataAdapterMedType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		vh.spinnerMedType.setAdapter(vh.dataAdapterMedType);
    		vh.spinnerMedType.setOnItemSelectedListener(vh.createSpinnerMedTypOnItemSelectListener());

    		vh.editTextMedQuantity.addTextChangedListener( vh.createEditTextMedQuantityChangeListener() );
    		
    		vh.editTextMedQuantityUnit.addTextChangedListener( vh.createEditTextMedQuantUnitChangeListener() );
		}
		try{ 
			vh.medicineAmount = getItem(position); 
		} catch(Exception ex){}
		
		int selIndex = -1;
		if( vh.medicineAmount.getMedicine() != null )
			selIndex = vh.dataAdapterMedicinName.getPosition( vh.medicineAmount.getMedicine().getName() );
		else if( vh.dataAdapterMedicinName.getCount() > 0 )
			selIndex = vh.dataAdapterMedicinName.getCount()-1;
		if( selIndex >= 0 )
		{
			try{ vh.editTextMed.setSelection( selIndex ); } catch(Exception ex){}
			
			try {
			if( vh.editTextMed.getText().toString().isEmpty() )
				vh.editTextMed.setText( vh.dataAdapterMedicinName.getItem(selIndex).toString() );
			 } catch(Exception ex){}
		}

		vh.spinnerMedQuant.setSelection( vh.medicineAmount.getQuantity().ordinal() );
		if( vh.medicineAmount.getMedicine() != null )
			vh.spinnerMedType.setSelection( vh.medicineAmount.getMedicine().getType().ordinal() );
		else
			vh.spinnerMedType.setSelection( eMedicinType.Tablette.ordinal() );
		if( vh.medicineAmount.getMedicine() != null )
			vh.editTextMedQuantity.setText( "" + vh.medicineAmount.getMedicine().getQuantity().getQuantity() );
		else
			vh.editTextMedQuantity.setText( "0" );
		if( vh.medicineAmount.getMedicine() != null )
			vh.editTextMedQuantityUnit.setText( vh.medicineAmount.getMedicine().getQuantity().getQuantityUnit() );
		else
			vh.editTextMedQuantityUnit.setText( "mg" );
		
		return view;
	}
}
