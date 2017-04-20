package at.gepa.bloodpreasure.exportimport;

import java.io.Serializable;

import at.gepa.bloodpreasure.exportimport.ExportImportData.eFileType;

public interface IExportImportTypListener
extends Serializable
{

	void typeChanged(ExportImportData exportImportData, int page);

	void beforeTypeChanged(ExportImportData exportImportData, int page, eFileType t);

}
