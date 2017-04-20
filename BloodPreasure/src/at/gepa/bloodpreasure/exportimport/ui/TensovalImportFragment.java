package at.gepa.bloodpreasure.exportimport.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.exportimport.ExportImportData;
import at.gepa.bloodpreasure.exportimport.IExportImportTypListener;
import at.gepa.bloodpreasure.exportimport.TensovalImport;

public class TensovalImportFragment extends ExportImportFragment {

	private EditText import_txtTensovalPersonId;
	private RadioButton rb_TensovalImportId;
	private RadioButton rb_ImportOtherId;

	public TensovalImportFragment(int arg0, ExportImportData exportImportData, IExportImportTypListener iExportImportTypListener) {
		super(arg0, exportImportData, iExportImportTypListener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = (RelativeLayout)super.onCreateView(inflater, container, savedInstanceState);
		
		rootView = (RelativeLayout)inflater.inflate(R.layout.export_fragment_3, container, false);
		
		import_txtTensovalPersonId = (EditText)rootView.findViewById(R.id.import_txtTensovalPersonId);
		import_txtTensovalPersonId.setText( ""+getTensovalImportData().getTensovalPersonIndex() );
		import_txtTensovalPersonId.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				getTensovalImportData().setTensovalPersonIndex( s );
			}
		});
		
		rb_TensovalImportId = (RadioButton)rootView.findViewById(R.id.rb_TensovalImportId);
		rb_TensovalImportId.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				import_txtTensovalPersonId.setEnabled(isChecked);
			}
		});
		rb_ImportOtherId = (RadioButton)rootView.findViewById(R.id.rb_ImportOtherId);
		return rootView;
			
	}
	public void updateFields(boolean fromFieldToData) {
		super.updateFields(fromFieldToData);
		if( fromFieldToData )
		{
			if( import_txtTensovalPersonId != null )
				getTensovalImportData().setTensovalPersonIndex( import_txtTensovalPersonId.getText() );
		}
		else
			import_txtTensovalPersonId.setText( ""+getTensovalImportData().getTensovalPersonIndex() );
	}

	private TensovalImport getTensovalImportData() {
		TensovalImport ti = (TensovalImport) exportImportData;
		return ti;
	}
	
}
