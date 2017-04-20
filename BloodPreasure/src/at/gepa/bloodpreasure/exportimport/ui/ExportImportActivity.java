package at.gepa.bloodpreasure.exportimport.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.exportimport.ExportImportData;
import at.gepa.bloodpreasure.task.ICallbackTaskListener;
import at.gepa.net.IModel;


public class ExportImportActivity extends FragmentActivity
implements ICallbackTaskListener
{
	
	public final static String PARAM_KEY = "ExportImportData";
	private ExportImportData exportImportData;
	private ExportImportFragmentPagerAdapter adapterViewPager;
    public ProgressDialog mProgressDialog;
	
	public ExportImportActivity()
	{
		super();
	}
	
	@Override
	protected void onCreate(Bundle param) {
		super.onCreate(param);

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);
		
		setContentView( at.gepa.bloodpreasure.R.layout.activity_export_import );
		
		Bundle b = getIntent().getExtras();
		exportImportData = (ExportImportData)b.getSerializable(PARAM_KEY);
		exportImportData.setContext(this);
		
		ViewPager vpPager = (ViewPager) findViewById(at.gepa.bloodpreasure.R.id.vpPagerExportImportId);
		adapterViewPager = new ExportImportFragmentPagerAdapter(getSupportFragmentManager(), exportImportData);
		vpPager.setAdapter(adapterViewPager);
		
		setTitle( evalTitle() );
	}	
	
	
	private CharSequence evalTitle() {
		if( exportImportData.getOpenSaveMode() == ExportImportData.eMode.FileSave )
			return "Export";
		return "Import";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		final ICallbackTaskListener listener = this;//MainActivityGrid.self;
			
		MenuItem mi = menu.add(evalTitle());
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mi.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				adapterViewPager.updateFields(true);
				exportImportData.execute(listener);
				return true;
			}
		});
		return true;
	}

	@Override
	public ProgressDialog getProgressDialog() {
		return mProgressDialog;
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public void setModelDone(boolean b) {
		MainActivityGrid.self.setModelDone(b);
	}

	@Override
	public void doUIRefresh() {
		
	}

	@Override
	public IModel getDownloadModel() {
		return MainActivityGrid.self.getDownloadModel();
	}

	@Override
	public Object _getSystemService(String powerService) {
		return MainActivityGrid.self._getSystemService(powerService);
	}

	@Override
	public int getBloodPreasureCount() {
		return MainActivityGrid.self.getBloodPreasureCount();
	}

	@Override
	public void setListSelection(int i) {
	}

	@Override
	public void _createDownloadTask() {
		MainActivityGrid.createDownloadTask();
	}

	@Override
	public IModel getUploadModel() {
		return MainActivityGrid.self.getUploadModel();
	}
		
}
