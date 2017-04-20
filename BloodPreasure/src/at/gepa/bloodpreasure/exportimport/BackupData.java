package at.gepa.bloodpreasure.exportimport;

import at.gepa.bloodpreasure.task.ICallbackTaskListener;

public class BackupData extends ExportImportData
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6859647172873673883L;

	public BackupData(String defaultBackupfile, eFileType localfile, eMode filesave) 
	{
		super(defaultBackupfile, localfile, filesave);
	}
	public BackupData(String [] files, eFileType localfile, eMode filesave) 
	{
		super(files, localfile, filesave);
	}

	@Override
	public void execute(ICallbackTaskListener listener) {
		super.execute(listener);
		super.save();

	}
	public String getEndMessage() {
		return "Sicherung erfolgreich abgeschlossen!";
	}

}
