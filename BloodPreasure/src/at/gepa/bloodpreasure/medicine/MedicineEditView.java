package at.gepa.bloodpreasure.medicine;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import at.gepa.androidlib.ui.DialogMessageBox.EndEditListener;
import at.gepa.lib.model.medicine.Medicine;
import at.gepa.lib.model.medicine.Medicine.MedicineQuantity;
import at.gepa.lib.model.medicine.Medicine.eMedicinType;
import at.gepa.lib.model.medicine.MedicineAmount;
import at.gepa.lib.model.medicine.MedicineAmount.eMedicationQuantity;
import at.gepa.lib.model.medicine.MedicineName;

public class MedicineEditView extends LinearLayout implements EndEditListener {

	private MedicineAmount medicineAmount;

	public ArrayAdapter<eMedicationQuantity> dataAdapterMedQuant;
	public ArrayAdapter<eMedicinType> dataAdapterMedType;
	public AutoCompleteTextView editTextMed; 
	public EditText editTextMedQuantity;
	public EditText editTextMedQuantityUnit;
	public Spinner spinnerMedType;

	public ArrayAdapter<MedicineName> dataAdapterMedicinName;
	public Spinner spinnerMedQuant;

	public MedicineEditView(Context context, MedicineAmount ma) 
	{
		super(context);
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1 ); 
		setLayoutParams(lp);
		lp.setMargins(4, 4, 4, 4);
		setOrientation(LinearLayout.VERTICAL);
		
		this.medicineAmount = ma;
		
		addMedikamentText(context);
		
		addMedikamentTyp( context );
		
		addQuantity( context );
		
		addMedQuantity( context );
		
	}
	
	private void addMedQuantity(Context context) {
		LinearLayout llHoriz = buildLayout( context, "Einnahme:", 1004);
		
		spinnerMedQuant = new Spinner(context);
		spinnerMedQuant.setId(1004);
		llHoriz.addView( spinnerMedQuant, llHoriz.getChildCount() );
		spinnerMedQuant.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1 ));
		
		addView( llHoriz, getChildCount() );

		ArrayList<eMedicationQuantity> listMedQuant = new ArrayList<eMedicationQuantity>();
		listMedQuant.add(eMedicationQuantity.Ganze);
		listMedQuant.add(eMedicationQuantity.Halbe);
		listMedQuant.add(eMedicationQuantity.Viertel);
		dataAdapterMedQuant = new ArrayAdapter<eMedicationQuantity>(super.getContext(), android.R.layout.simple_spinner_item, listMedQuant);
		dataAdapterMedQuant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerMedQuant.setAdapter(dataAdapterMedQuant);
		spinnerMedQuant.setOnItemSelectedListener(createSpinnerMedQuantOnItemSelectListener());
		
		spinnerMedQuant.setSelection( medicineAmount.getQuantity().ordinal() );
	}

	private LinearLayout buildLayout(Context context, String label, int id) {
		LinearLayout llHoriz = new LinearLayout(context);
		llHoriz.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1 ));
		llHoriz.setOrientation(LinearLayout.HORIZONTAL);
		
		TextView tvlabel = new TextView( context );
		tvlabel.setWidth(220);
		tvlabel.setText(label);
		llHoriz.addView( tvlabel, llHoriz.getChildCount() );
		tvlabel.setLabelFor(id);
		return llHoriz;
	}

	private void addQuantity(Context context) {
		LinearLayout llHoriz = buildLayout( context, "Größe:", 1003);
		
		editTextMedQuantity = new EditText(context);
		editTextMedQuantity.setId(1003);
		llHoriz.addView( editTextMedQuantity, llHoriz.getChildCount() );
		editTextMedQuantityUnit = new EditText(context);
		llHoriz.addView( editTextMedQuantityUnit, llHoriz.getChildCount() );
		
		editTextMedQuantity.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.5f ));
		
		
		addView( llHoriz, getChildCount());
		
		editTextMedQuantity.addTextChangedListener( createEditTextMedQuantityChangeListener() );
		if( medicineAmount.getMedicine() != null )
			editTextMedQuantity.setText( "" + medicineAmount.getMedicine().getQuantity().getQuantity() );
		else
			editTextMedQuantity.setText( "0" );
		if( medicineAmount.getMedicine() != null )
			editTextMedQuantityUnit.setText( medicineAmount.getMedicine().getQuantity().getQuantityUnit() );
		else
			editTextMedQuantityUnit.setText( "mg" );
	
		editTextMedQuantityUnit.addTextChangedListener( createEditTextMedQuantUnitChangeListener() );
		editTextMedQuantityUnit.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.5f ));
	}

	private void addMedikamentTyp(Context context) {
		LinearLayout llHoriz = buildLayout( context, "Typ:", 1002);
		
		spinnerMedType = new Spinner(context);
		spinnerMedType.setId(1002);
		llHoriz.addView( spinnerMedType, llHoriz.getChildCount() );
		spinnerMedType.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f ));
		
		addView( llHoriz, getChildCount());

		ArrayList<eMedicinType> listMedType = new ArrayList<eMedicinType>();
		listMedType.add(eMedicinType.Tablette);
		listMedType.add(eMedicinType.Spray);
		listMedType.add(eMedicinType.Tropfen);
		dataAdapterMedType = new ArrayAdapter<eMedicinType>(super.getContext(), android.R.layout.simple_spinner_item, listMedType);
		dataAdapterMedType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerMedType.setAdapter(dataAdapterMedType);
		spinnerMedType.setOnItemSelectedListener(createSpinnerMedTypOnItemSelectListener());
		
		if( medicineAmount.getMedicine() != null )
			spinnerMedType.setSelection( medicineAmount.getMedicine().getType().ordinal() );
		else
			spinnerMedType.setSelection( eMedicinType.Tablette.ordinal() );
		
	}

	private void addMedikamentText(Context context) {
		LinearLayout llHoriz = buildLayout( context, "Name:", 1001);
		
		editTextMed = new AutoCompleteTextView(context);
		editTextMed.setId(1001);
		editTextMed.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f ));
		
		llHoriz.addView( editTextMed, llHoriz.getChildCount() );
		
		addView( llHoriz, getChildCount());
		
		editTextMed.setSelectAllOnFocus(true);
		dataAdapterMedicinName = new ArrayAdapter<MedicineName>(super.getContext(), android.R.layout.simple_spinner_item, Medicine.getNameList() );
		dataAdapterMedicinName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		editTextMed.setAdapter(dataAdapterMedicinName);
		
		editTextMed.setOnItemSelectedListener(creatEditTextMedOnItemSelectListener() );
		int selIndex = -1;
		if( medicineAmount.getMedicine() != null )
		{
			selIndex = dataAdapterMedicinName.getPosition( medicineAmount.getMedicine().getName() );
			if( selIndex >= 0 )
			{
				try{ editTextMed.setSelection( selIndex ); } catch(Exception ex){}
				
				try {
				if( editTextMed.getText().toString().isEmpty() )
					editTextMed.setText( dataAdapterMedicinName.getItem(selIndex).toString() );
				 } catch(Exception ex){}
			}
		}
		if( selIndex < 0 )
			editTextMed.setText( "" );
	}

	public MedicineName getCurrentMedicineName() 
	{
		MedicineName medicineName = MedicineName.createInstance(editTextMed.getText().toString());
//		int pos = editTextMed.getListSelection();
//		if( pos >= 0 )
//			medicineName = (MedicineName)dataAdapterMedicinName.getItem(pos);
//		else
//			medicineName = MedicineName.createInstance(editTextMed.getText().toString());
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
	


	@Override
	public void textChanged(String[] t) {
		if( medicineAmount.getMedicine() == null )
			medicineAmount.setMedicine( getNewMedicine(spinnerMedType.getSelectedItemPosition()) );
		else
			medicineAmount.getMedicine().setName(getCurrentMedicineName());
	}

}
