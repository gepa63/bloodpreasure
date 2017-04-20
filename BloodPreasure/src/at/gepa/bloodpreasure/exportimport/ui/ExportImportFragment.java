package at.gepa.bloodpreasure.exportimport.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import at.gepa.bloodpreasure.exportimport.ExportImportData;
import at.gepa.bloodpreasure.exportimport.ExportImportData.eFileType;
import at.gepa.bloodpreasure.exportimport.IExportImportTypListener;

public abstract class ExportImportFragment extends Fragment implements OnClickListener {

	protected int page;
	protected ExportImportData exportImportData;
	protected IExportImportTypListener exportImportTypListener;
	protected LayoutInflater inflater;
	protected ViewGroup container;
	protected RelativeLayout rootView;

	public ExportImportFragment() {
		this(-1, null, null);
	}
	public ExportImportFragment(int arg0, ExportImportData exportImportData, IExportImportTypListener iExportImportTypListener) {
		this.page = arg0;
		this.exportImportData = exportImportData;
		this.exportImportTypListener = iExportImportTypListener;
		rootView = null;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		
		if( outState != null )
		{
			outState.putInt("page", this.page);
			try{ outState.putSerializable("data", this.exportImportData); } catch(Exception ex){}
			try{ outState.putSerializable("listener", exportImportTypListener); } catch(Exception ex){}
		}
		
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if( savedInstanceState != null )
		{
			this.page = savedInstanceState.getInt("page");
			this.exportImportData = (ExportImportData)savedInstanceState.getSerializable("data");
			this.exportImportTypListener = (IExportImportTypListener) savedInstanceState.getSerializable("listener");
			if( exportImportTypListener == null )
			{
				;
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		this.inflater = inflater;
		this.container = container;
		return rootView;
	}
	public View reLayout() {
		return rootView;
	}
	@Override
	public void onClick(View v) {
	}	
	
	public void updateFields(boolean fromFieldToData) {
	}
	public void updateFields(eFileType t, boolean fromFieldToData) {
	}
}
