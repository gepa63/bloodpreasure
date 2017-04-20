package at.gepa.bloodpreasure.exportimport;

import at.gepa.bloodpreasure.task.ICallbackTaskListener;

public class RestoreData extends ExportImportData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6859647172873673883L;

	public RestoreData(String defaultBackupfile, eFileType localfile, eMode filesave) 
	{
		super(defaultBackupfile, localfile, filesave);
	}
	public RestoreData(String [] files, eFileType localfile, eMode filesave) 
	{
		super(files, localfile, filesave);
	}

	@Override
	public void execute(ICallbackTaskListener listener) {
		super.execute(listener);
		super.load();

	}
	public String getEndMessage() {
		return "Wiederherstellung erfolgreich abgeschlossen!";
	}

}
