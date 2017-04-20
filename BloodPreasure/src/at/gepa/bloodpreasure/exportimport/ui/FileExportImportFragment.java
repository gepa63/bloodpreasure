package at.gepa.bloodpreasure.exportimport.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.exportimport.ExportImportData;
import at.gepa.bloodpreasure.exportimport.ExportImportData.eFileType;
import at.gepa.bloodpreasure.exportimport.IExportImportTypListener;
import at.gepa.net.DataAccess;

public class FileExportImportFragment extends ExportImportFragment {
	protected EditText editFieldFilename;

	public FileExportImportFragment() {
	}

	public FileExportImportFragment(int arg0,
			ExportImportData exportImportData,
			IExportImportTypListener iExportImportTypListener) {
		super(arg0, exportImportData, iExportImportTypListener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		super.onCreateView( inflater, container, savedInstanceState );
		rootView = (RelativeLayout)reLayout();
		return rootView;
	}
	public View reLayout() 
	{
		if( rootView == null )
		{
			rootView = (RelativeLayout)inflater.inflate(R.layout.export_fragment_2, container, false);
			rootView.setBackgroundResource( R.xml.background_editdlg );
		}
		Button bt = (Button)rootView.findViewById(R.id.export_import_btChooseFilenameId);
		TextView label = (TextView)rootView.findViewById(R.id.export_import_labelFilenameId);
		label.setLabelFor(R.id.export_import_txtFilenameId);
		if( exportImportData.isLocalFile() )
		{
			bt.setVisibility(Button.VISIBLE);
			label.setText("Dateiname:");
		}
		else
		{
			DataAccess da = MainActivityGrid.getDataAccess();
			if( !da.isLocalFileActive() )
			{
				bt.setVisibility(Button.VISIBLE);
				bt.setText("Erstelle URL von Daten-Quelle");
			}
			else
				bt.setVisibility(Button.GONE);
				
			label.setText("URL:");
		}
		if( editFieldFilename == null )
		{
			editFieldFilename = (EditText)rootView.findViewById(R.id.export_import_txtFilenameId);
			
			bt.setOnClickListener(this);
			editFieldFilename.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					exportImportData.setFile(s.toString());
				}
			});
		}
		editFieldFilename.setText( exportImportData.getFile() );
		return rootView;
	}	
	@Override
	public void onClick(View v) {
		if( exportImportData.isLocalFile() )
		{
			at.gepa.androidlib.ui.SimpleFileDialog FileSaveDialog =  new at.gepa.androidlib.ui.SimpleFileDialog( getContext(), exportImportData.getOpenSaveMode().name(), 
							new at.gepa.androidlib.ui.SimpleFileDialog.SimpleFileDialogListener()
			{
				@Override
				public void onChosenDir(String chosenDir) 
				{
					editFieldFilename.setText(chosenDir);
					exportImportData.setLocalFile(chosenDir);
				}
			}, true);
			FileSaveDialog.setDefault_File_Name( exportImportData.getFile() );
			FileSaveDialog.chooseFile_or_Dir();
		}
		else
		{
			DataAccess da = MainActivityGrid.getDataAccess();
			if( !da.isLocalFileActive() )
			{
				String url = da.buildLink( exportImportData.getFile() );
				editFieldFilename.setText(url);
			}
		}
	}
	public void updateFields(boolean fromFieldToData) {
		if( fromFieldToData )
		{
			if( editFieldFilename != null )
				exportImportData.setFile(editFieldFilename.getText().toString());
		}
	}
	public void updateFields(eFileType t, boolean fromFieldToData) {
		if( fromFieldToData )
		{
			exportImportData.setFile(t, editFieldFilename.getText().toString());
		}
	}
}
