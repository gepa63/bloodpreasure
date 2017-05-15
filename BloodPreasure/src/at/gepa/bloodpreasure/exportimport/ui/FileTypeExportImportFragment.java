package at.gepa.bloodpreasure.exportimport.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.exportimport.ExportImportData;
import at.gepa.bloodpreasure.exportimport.IExportImportTypListener;
import at.gepa.bloodpreasure.pref.BloodPreasurePreferenceActivity;

public class FileTypeExportImportFragment extends ExportImportFragment {

	public FileTypeExportImportFragment() {
	}

	public FileTypeExportImportFragment(int arg0,
			ExportImportData exportImportData,
			IExportImportTypListener iExportImportTypListener) {
		super(arg0, exportImportData, iExportImportTypListener);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		super.onCreateView( inflater, container, savedInstanceState );
		rootView = (RelativeLayout)inflater.inflate(R.layout.export_fragment_1, container, false);

		rootView.setBackgroundResource( R.xml.background_editdlg );
		
		RadioButton rblf = (RadioButton)rootView.findViewById(R.id.export_import_localFileId);
		RadioButton rburl = (RadioButton)rootView.findViewById(R.id.export_import_UrlId);
		if( exportImportData.isLocalFile() )
			rblf.setChecked(true);
		else if( exportImportData.isUrl() )
			rburl.setChecked(true);
		
		RadioButton rbaf = (RadioButton)rootView.findViewById(R.id.export_import_AzureFunctionId);
		rbaf.setEnabled( !BloodPreasurePreferenceActivity.getAzureFunctionLink().isEmpty() );
		rbaf.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if( isChecked )
				{
					exportImportTypListener.beforeTypeChanged( exportImportData, page, ExportImportData.eFileType.LocalFile );
					exportImportData.setFileType( ExportImportData.eFileType.AzureFunction );
					exportImportTypListener.typeChanged( exportImportData, page );
				}
			}
		});
		
		rblf.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if( isChecked )
				{
					exportImportTypListener.beforeTypeChanged( exportImportData, page, ExportImportData.eFileType.Url );
					exportImportData.setFileType( ExportImportData.eFileType.LocalFile );
					exportImportTypListener.typeChanged( exportImportData, page );
				}
			}
		});

		rburl.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if( isChecked )
				{
					exportImportTypListener.beforeTypeChanged( exportImportData, page, ExportImportData.eFileType.LocalFile );
					exportImportData.setFileType( ExportImportData.eFileType.Url );
					exportImportTypListener.typeChanged( exportImportData, page );
				}
			}
		});
		return rootView;
	}
}
