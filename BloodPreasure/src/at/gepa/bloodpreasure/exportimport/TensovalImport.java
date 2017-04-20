package at.gepa.bloodpreasure.exportimport;

import android.text.Editable;
import android.widget.Toast;
import at.gepa.androidlib.SystemInfo;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.exportimport.ui.ExportImportFragment;
import at.gepa.bloodpreasure.exportimport.ui.TensovalImportFragment;
import at.gepa.bloodpreasure.pref.BloodPreasurePreferenceActivity;
import at.gepa.bloodpreasure.task.ICallbackTaskListener;
import at.gepa.bloodpreasure.task.TensovalDownloadTask;
import at.gepa.lib.model.TensovalModel;

public class TensovalImport extends ExportImportData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6859647172873673883L;

	private int tensovalPersonIndex;
	public int getTensovalPersonIndex() {
		return tensovalPersonIndex;
	}

	@Override
	public void execute(ICallbackTaskListener listener) {
		super.execute(listener);
		
		
		try {
			if( SystemInfo.isPROVersion( MainActivityGrid.self ) )
			{
				TensovalModel tmodel = TensovalModel.createInstance(getFile(), tensovalPersonIndex);
				TensovalDownloadTask task = new TensovalDownloadTask(super.listener.getDownloadModel());
				task.execute(tmodel);
				BloodPreasurePreferenceActivity.saveSettingsExportImportFileName(this, getClass().getSimpleName(), "" );
			}
			else
				at.gepa.tools2.BloodPreasureDialogMessageBox.ShowMessage_OnlyPRO(getContext() );
			
		} catch (Exception e) {
			Toast.makeText(MainActivityGrid.self, e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	public String getEndMessage() {
		return "Tensoval Import erfolgreich abgeschlossen!";
	}
	
	@Override
	public ExportImportFragment createInstance(int page, IExportImportTypListener listener) 
	{
		if( page < 2 )
			return super.createInstance(page, listener); 
		return new TensovalImportFragment(page, this, listener);
	}
	public void setTensovalPersonIndex(int tensovalPersonIndex) {
		this.tensovalPersonIndex = tensovalPersonIndex;
	}

	public TensovalImport(String defaultBackupfile, eFileType localfile, eMode filesave) 
	{
		super(defaultBackupfile, localfile, filesave);
		tensovalPersonIndex = BloodPreasurePreferenceActivity.getTensovalPersonIndex();
	}

	public void setTensovalPersonIndex(Editable text) {
		String s = text.toString();
		try
		{
			tensovalPersonIndex = Integer.parseInt(s);
		}
		catch(Exception ex){}
	}
	public int getCount() {
		return super.getCount()+1;
	}

}
