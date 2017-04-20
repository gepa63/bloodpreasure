package at.gepa.bloodpreasure;

import java.util.ArrayList;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyze;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyzeActivity;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyzeEngine;
import at.gepa.bloodpreasure.exportimport.BackupData;
import at.gepa.bloodpreasure.exportimport.ExportImportData;
import at.gepa.bloodpreasure.exportimport.RestoreData;
import at.gepa.bloodpreasure.exportimport.TensovalImport;
import at.gepa.bloodpreasure.exportimport.ui.ExportImportActivity;
import at.gepa.bloodpreasure.pref.BloodPreasurePreferenceActivity;
import at.gepa.bloodpreasure.print.PrintFragmentActivity;
import at.gepa.bloodpreasure.task.DownloadTask;
import at.gepa.bloodpreasure.task.ICachedCallbackTaskListener;
import at.gepa.bloodpreasure.task.UploadTask;
import at.gepa.bloodpreasure.ui.BloodPreasureInfoActivity;
import at.gepa.bloodpreasure.ui.multipage.EditElementActivity;
import at.gepa.bloodpreasure.ui.multipage.EditFragment;
import at.gepa.files.CacheFile;
import at.gepa.files.LocalFileAccess;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.lib.model.BloodPreasureTags;
import at.gepa.lib.model.ITimeRoundListener;
import at.gepa.lib.model.TensovalModel;
import at.gepa.model.AvgValueHolder;
import at.gepa.model.BloodPreasureModel;
import at.gepa.model.TagListDownloadTask;
import at.gepa.net.DataAccess;
import at.gepa.net.IElement;
import at.gepa.net.IModel;



public class MainActivityGrid extends FragmentActivity 
implements 
//IDropboxSessionHandler, 
ITimeRoundListener, ICachedCallbackTaskListener
{
//	public static final boolean TENSOVAL_MODE = false;

	
	public static MainActivityGrid self;
	public BloodPreasureListAdapter bloodPreasureAdapter;
	private BloodPreasure currentEditable;
    public ProgressDialog mProgressDialog;
	public ListView list;
	public View header;
	private BloodPreasureFragmentPagerAdapter adapterViewPager;
	private ArrayList<IRefreshListener> dataRefreshListener;
	public AvgValueHolder avgValueHolder;
	private static Fragment[] fragmentList;

	private DataAccess dataAccess;
	public long lastModified;
	private ViewPager vpPager;
	public EditElementActivity editElementActivity;
	
	
	public MainActivityGrid()
	{
		editElementActivity = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		lastModified = 0;
		setContentView(R.layout.activity_main_activity);
		
//		for( int i=2; i < 255; i++ )
//		{
//			System.out.println( i + "=*" + (char)i + "*");
//		}
		
//		PasswordEncryptionDecryption e = new PasswordEncryptionDecryption("grumml");
//		String ep = "null", plainpwd = "null";
//		try {
//			ep = e.encrypt();
//			
//			PasswordEncryptionDecryption d = new PasswordEncryptionDecryption(ep, true);
//			plainpwd = d.decrypt();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		System.out.println(ep);
//		System.out.println(plainpwd);
		
		if( fragmentList == null )
			fragmentList = new Fragment[2];
		
		self = this;
		
		BloodPreasure.setRoundListener(this);
		BloodPreasurePreferenceActivity.initAnalyzeOnScreen();
		BloodPreasurePreferenceActivity.initFileAccessData();
		
//		if( TENSOVAL_MODE )
//			DataAccessFTP.SET_FTP_FILENAME("TENSOVAL.CSV");
			
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
			
		//getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment(0)).commit();
		vpPager = (ViewPager) findViewById(R.id.vpPager);

		adapterViewPager = new BloodPreasureFragmentPagerAdapter(getSupportFragmentManager());
		vpPager.setAdapter(adapterViewPager);
		vpPager.setCurrentItem(0);
		
		vpPager.setPageTransformer(false, new ViewPager.PageTransformer() { 
		    @Override
		    public void transformPage(View page, float position) {
//			        int pageWidth = GridFragment.getCurrentView().getWidth();
//			        int pageHeight = GridFragment.getCurrentView().getHeight();

		        if (position < -1) { // [-Infinity,-1)
		            // This page is way off-screen to the left.
		        	adapterViewPager.getItem(0).getView().setAlpha(0);
		        } else if(position <= 1){ // Page to the left, page centered, page to the right
		           // modify page view animations here for pages in view 
		        } else { // (1,+Infinity]
		            // This page is way off-screen to the right.
		        	adapterViewPager.getItem(0).getView().setAlpha(0);
		        }
		    }
		});
		
		avgValueHolder = new AvgValueHolder();
		avgValueHolder.setViews( findViewById(R.id.avgValues), findViewById(R.id.label_avg), findViewById(R.id.label_avg_day), findViewById(R.id.label_avg_night), findViewById(R.id.label_tendenz) );
		avgValueHolder.setImages( findViewById(R.id.tendenz_image), findViewById(R.id.img_avg_night), findViewById(R.id.img_avg_day) );
		
		showOrHideAnalyzeFields();
		
		EnableFunctionList.getInstance().setEnabled(false);
		
		BloodPreasurePreferenceActivity.checkSettings();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_grid, menu);
		
		EnableFunctionList.getInstance().add( menu.getItem(0) );
		EnableFunctionList.getInstance().add( menu.getItem(1) );
		
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.action_app_info) 
		{
			Intent intent = new Intent( self, BloodPreasureInfoActivity.class );
			try
			{
				startActivity(intent);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return true;
		}
		else if (id == R.id.action_settings) 
		{
			startPreferences();
			return true;
		}
		else if (id == R.id.action_app_analyze) 
		{
			Intent intent = new Intent( self, BloodPreasureAnalyzeActivity.class );
			try
			{
				startActivity(intent);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return true;
		}
		else if (id == R.id.action_add_bloodpreasure_value) 
		{
			startBloodPreasureEdit( newBloodPreasure() );
			return true;
		}
		else if( id == R.id.action_save_bloodpreasures )
		{
			try
			{
				save(null, true);
			}
			catch(Exception ex)
			{
				Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		else if( id == R.id.action_refresh )
		{
			try
			{
				GridFragment f = (GridFragment)adapterViewPager.getItem(0);
				f.refreshList();
				
				return true;
			}
			catch(Exception ex)
			{
				Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		else if( id == R.id.action_restore_data )
		{
			importBackupFromFile();
		}
		else if( id == R.id.action_backup_data )
		{
			exportBackupToFile();
			return true;
		}
		else if( id == R.id.action_import_tensovalid )
		{
			importTensovalFile();
			return true;
		}
//		else if( id == R.id.action_cache_as_datasource )
//		{
//	    <item
//        android:id="@+id/action_cache_as_datasource"
//        android:orderInCategory="260"
//        android:showAsAction="never"
//        android:title="@string/action_cache_as_datasource"/>
//		
//			saveCacheAsDatasource();
//			return true;
//		}
		else if( id == R.id.action_print )
		{
			if( at.gepa.androidlib.SystemInfo.isPROVersion( this ) )
			{
				Intent i = new Intent(this, PrintFragmentActivity.class);
				try
				{
					startActivity(i);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else
				at.gepa.tools2.BloodPreasureDialogMessageBox.ShowMessage_OnlyPRO(self);
			
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public BloodPreasure newBloodPreasure() {
		BloodPreasure element = new BloodPreasure();
		if( element.getGewicht() == 0f )
			element.setGewicht( BloodPreasureAnalyze.getGewicht() );
		if( !element.hasMedikation() )
			element.setMedikation( BloodPreasureAnalyze.getMedication() );
		element.setTags(BloodPreasureTags.mergeTagsMarkedAsUntilRevoke( element.getTags() ));
		
		//now, set defaults from avg
		if( avgValueHolder != null )
		{
			element.setSystolisch(avgValueHolder.getAvgSyst());
			element.setDiastolisch(avgValueHolder.getAvgDiast());
			element.setPuls(avgValueHolder.getAvgPuls());
		}
		element.setChanged(true);
		return element;
	}

	void startBloodPreasureEdit(BloodPreasure bp) {
		currentEditable = bp;		
		Intent i = new Intent(this, EditElementActivity.class);// BloodPreasureEditActivity_SinglePage.class);
		try
		{
			startActivity(i);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	
	public void save(BloodPreasure bp, boolean saveWithUpload )	throws Exception
	{
		if( bp != null )
		{
			bp.setChanged(true);
			int position = bloodPreasureAdapter.getPosition(bp); 
			if( position < 0 )
				bloodPreasureAdapter.insert( bp, 0 );
			else
			{
				BloodPreasure ov = bloodPreasureAdapter.getItem(position);
				ov.set( bp );
				bloodPreasureAdapter.notifyDataSetInvalidated();
			}
			refreshList();
		}		
		if( saveWithUpload )
		{
			createUploadTask();
		}
	}
	public void refreshList()
	{
		bloodPreasureAdapter.notifyDataSetChanged();
		list.invalidateViews();
		list.refreshDrawableState();
		if( avgValueHolder != null )
			this.avgValueHolder.calculate();
		notifyRefreshListener();
	}

	public BloodPreasure getCurrentEditable() {
		
		if( currentEditable == null )
		{
			return newBloodPreasure();
		}
		return currentEditable;
	}
	public void setCurrentEditable(BloodPreasure bp) {
		currentEditable = bp;
	}

    public void doUIRefresh()
    {
        self.runOnUiThread(new Runnable() {
            @Override
            public void run() {
	            self.refreshList();
            }
        });
    }
    

	public IModel getDownloadModel() {
		return new BloodPreasureModel(this);
	}

	public int size() {
		return bloodPreasureAdapter.getCount();
	}

	public IModel getUploadModel() 
	{
		return new BloodPreasureModel(this);
	}

	public void setModelDone(boolean b) {
		
		bloodPreasureAdapter.setChanged(b);
		regsiterUnknownTags( bloodPreasureAdapter );
		if( !b )
		{
	        self.runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	    			EnableFunctionList.getInstance().setEnabled(true);
	        		self.bloodPreasureAdapter.sort();
	            }
	        });
		}		
	}

	private void regsiterUnknownTags(BloodPreasureListAdapter bpa) {
		try
		{
		
			BloodPreasureTags tags = new BloodPreasureTags(bpa, BloodPreasurePreferenceActivity.getTags());
	
			BloodPreasureTags.loadTagsUntilRevokes(new TagListDownloadTask(getContext()), dataAccess.buildLink(BloodPreasureTags.FILENAME) );
			
			tags.prepare();
			
			if( tags.isNeed2Save() )
			{
				BloodPreasurePreferenceActivity.saveTags( tags.getList()  );
			}
		}
		catch(Throwable t)
		{
			
		}
	}


	public void clearModel() {
        self.runOnUiThread(new Runnable() {
            @Override
            public void run() {
	            self.bloodPreasureAdapter.clear();
            }
        });
	}

	public void add(final BloodPreasure bp) {
        self.runOnUiThread(new Runnable() {
            @Override
            public void run() {
	            self.bloodPreasureAdapter.add(bp);
            }
        });
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // ignore orientation/keyboard change
	  super.onConfigurationChanged(newConfig);
	}

	public void refreshInvalidated() {
        self.runOnUiThread(new Runnable() {
            @Override
            public void run() {
	            self.bloodPreasureAdapter.notifyDataSetInvalidated();
	            self.bloodPreasureAdapter.notifyDataSetChanged();
            }
        });
		
	}

	public void addRegsiterDataRefreshListener(IRefreshListener listener) 
	{
		if( dataRefreshListener == null )
			dataRefreshListener = new ArrayList<IRefreshListener>();
		if( !dataRefreshListener.contains(listener) )
			dataRefreshListener.add(listener);
	}	
	private void notifyRefreshListener() {
		if( dataRefreshListener != null )
		{
			for( IRefreshListener l : dataRefreshListener )
				l.dataRefresh();
		}
	}
	
	public void onClick( View v )
	{
		startBloodPreasureEdit( newBloodPreasure() );
	}

//	public void dumpValues() {
//        self.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//            	String out = "";
//	            for( int x = self.bloodPreasureAdapter.getCount()-1; x >= 0; x-- )
//	            {
//	            	BloodPreasure bp = self.bloodPreasureAdapter.getItem(x);
//	            	if( x < self.bloodPreasureAdapter.getCount()-1 )
//	            		out += "\t, ";
//	            	out += bp.getSystolisch();
//	            }
//            	String out2 = "";
//	            for( int x = 0; x < self.bloodPreasureAdapter.getCount(); x++ )
//	            {
//	            	if( x > 0 )
//	            		out2 += "\t, ";
//        			out2 += "" + (x+1);
//	            }
//	            System.out.println(out);
//	            System.out.println(out2);
//            }
//        });
//
//	}
	
	public void onBackPressed()
	{
		ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
		
		if( vpPager.getCurrentItem() == 0 )
			super.onBackPressed();
		else
			vpPager.setCurrentItem(0);
	}
	
	public void doDownloadFile()
	{
		MainActivityGrid.createDownloadTask();		
	}

	public boolean isAnalyzeVisible() {
		View avgValues = findViewById(R.id.avgValues);
		if( avgValues.getVisibility() == View.VISIBLE )
			return true;
		return false;
	}

	public void showOrHideAnalyzeFields() {
		avgValueHolder.setVisible(BloodPreasureAnalyze.SHOW_ANALYZE_ON_MAINSCREEN);
		RelativeLayout relLayoutButtonBarBottom = (RelativeLayout)findViewById(R.id.relLayoutButtonBarBottomId);
		
		EnableFunctionList.getInstance().add( relLayoutButtonBarBottom.findViewById(R.id.button) );
		
		int orientation = getResources().getConfiguration().orientation;
	    switch (orientation)
	    {
	        case Configuration.ORIENTATION_UNDEFINED: orientation = Configuration.ORIENTATION_PORTRAIT; break;
	        case Configuration.ORIENTATION_LANDSCAPE: break;
	        case Configuration.ORIENTATION_PORTRAIT:  break;
	        default: orientation = Configuration.ORIENTATION_PORTRAIT; break;
	    }
    	int left = 2;
		int top = 0;
		int right = 2;
		int bottom = 20;
	    if( Configuration.ORIENTATION_PORTRAIT == orientation )
	    {
	    	bottom = 2;
	    }
    	FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)relLayoutButtonBarBottom.getLayoutParams();
		lp.setMargins(left, top, right, bottom);
	}

	public void createUploadTask() {
		
		UploadTask Task = new UploadTask(MainActivityGrid.self);
		Task.execute(dataAccess);
	}

	public static void createDownloadTask() {
		if( self.dataAccess == null ) return;
		
		final DownloadTask downloadTask = new DownloadTask(MainActivityGrid.self);
		self.readCache( downloadTask.getList() );
		
		downloadTask.execute(self.dataAccess);
		
		if( MainActivityGrid.self.mProgressDialog != null )
			MainActivityGrid.self.mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		    @Override
		    public void onCancel(DialogInterface dialog) {
		        downloadTask.cancel(true);
		    }
		});		
		
	}

	public static DataAccess getDataAccess() {
		return self.dataAccess;
	}

	public void setDataAccessObject(DataAccess da) {
		if( dataAccess != null )
		{
			if( dataAccess.needReload(da) )
			{
				dataAccess = da;
//				if( wasFTPConfigured != DataAccessFTP.isFTPConfigured() )
//				{
//					String frage = null;
//					//entweder von lokal zum FTP Server
//					if( DataAccessFTP.isFTPConfigured() )
//					{
//						java.io.File f = new java.io.File(DataAccessFTP.GET_FTP_FILENAME());
//						if( f.exists() && f.length() > 0 )
//							frage = "Soll die lokale Datei auf den FTP Server hochgeladen werden?";
//						else
//							frage = null;
//					}
//					//oder vom FTP Server nach lokal
//					else if( wasFTPConfigured )
//					{
//						frage = "Soll die aktuelle Blutdruckliste lokal gespeichert werden?";
//					}
//					if( frage != null )
//					{
//				    	 AlertDialog.Builder builder = new AlertDialog.Builder(this);
//				    	    builder.setTitle(MainActivityGrid.self.getTitle());
//				    	    builder.setMessage( frage );
//				    	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
//				    	    {
//				    	        public void onClick(DialogInterface dialog, int id) {
//				    	        	if( DataAccessFTP.isFTPConfigured() )
//				    	        		MainActivityGrid.self.uploadAndDownloadFile();
//				    	        	else
//				    	        		MainActivityGrid.self.saveLocalAndReload();
//				    	        }
//				    	 });	 
//				    	 builder.create().show();
	//
//					}
//				}
//				else
					MainActivityGrid.self.doDownloadFile();
				
			}
		}
		this.dataAccess = da;
	}

//	@Override
//	public void handle(DropboxAPI<AndroidAuthSession> sourceClient) {
//		sourceClient.getSession().startOAuth2Authentication(this);
//		
//	}
	private void importTensovalFile() {

		TensovalImport bd = new TensovalImport(TensovalModel.DEFAULT_TENSOVAL_FILE, ExportImportData.eFileType.LocalFile, ExportImportData.eMode.FileOpen );
		doExportImport( bd );
	}

	public boolean contains(IElement bp) {
		return bloodPreasureAdapter.getPosition((BloodPreasure) bp) >= 0;
	}

	public void add(int i, IElement bp) {
		bloodPreasureAdapter.insert((BloodPreasure) bp, i);
	}

	public boolean checkLastModified(long lastModified) {
		return( this.lastModified >= lastModified || this.lastModified <= 0 || lastModified <= 0);
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public Date alignTime(Date time) 
	{
		if( BloodPreasureAnalyze.getRoundQuarters() )
		{
			time = at.gepa.lib.tools.time.TimeTool.alignToQuarter(time);
		}
		return time;
	}
	private void exportBackupToFile() {
		
		BackupData bd = new BackupData( BloodPreasure.DEFAULT_BACKUPFILE(),
				ExportImportData.eFileType.LocalFile, ExportImportData.eMode.FileSave );
		
		doExportImport( bd );
	}

	private void doExportImport(ExportImportData eximp ) 
	{
		BloodPreasurePreferenceActivity.getSettingsExportImportFileName(eximp);
		
		Intent intent = new Intent(this, ExportImportActivity.class);
		
		Bundle bundle = new Bundle();
		bundle.putSerializable(ExportImportActivity.PARAM_KEY, eximp);
		intent.putExtras(bundle);
		try
		{
			startActivity(intent);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void importBackupFromFile() {
		//at.gepa.tools2.SystemInfo.getBloodPreasureDirectory()
		RestoreData bd = new RestoreData(BloodPreasure.DEFAULT_BACKUPFILE(), ExportImportData.eFileType.LocalFile, ExportImportData.eMode.FileOpen );
		
		doExportImport( bd );
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
	public int getBloodPreasureCount() {
		return bloodPreasureAdapter.getCount();
	}

	@Override
	public void setListSelection(int i) {
		list.setSelection(i);
	}

	@Override
	public void _createDownloadTask() {
		MainActivityGrid.createDownloadTask();
	}

	@Override
	public Object _getSystemService(String powerService) {
		return self.getSystemService(powerService);
	}

	public String createPdfListFile(boolean details) throws Exception {
		GridFragment fg = null;
		synchronized (fragmentList) {
			fg = (GridFragment)fragmentList[0];			
		}
		
		return fg.createPdfListFile(details);
	}

	public String createPdfAnalyzeFile() throws Exception {
		BloodPreasureAnalyzeEngine e = new BloodPreasureAnalyzeEngine();
		
		return e.createPdfAnalyzeFile();
	}

	public String createPdfChartFile(int orientation) throws Exception {
		
		makeChartVisible();

		if( !isChartExists() ) return null;
		
		ChartFragment fg = null;
		synchronized (fragmentList) {
			fg = (ChartFragment)fragmentList[1];			
		}
		
		return fg.createPdfChartFile(orientation); //fg.createPdfChartFile_FromView();// 
	}

	public boolean isChartExists() {
		synchronized (fragmentList) {
			if( fragmentList[1] == null )
				return false;
		}
		return true;
	}

	public void restartPreferences() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	startPreferences();
            }
        });
		
	}
	private void startPreferences() {
		Intent i = new Intent(this, BloodPreasurePreferenceActivity.class);
		try
		{
			startActivity(i);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void reloadChart() {
		ChartFragment fg = null;
		synchronized (fragmentList) {
			if( fragmentList[1] != null )
			{
				fg = (ChartFragment)fragmentList[1];
			}
		}
		if( fg != null )
			fg.refresh();
	}

	public boolean isOrientationLandscape() {
		int currentOrientation = MainActivityGrid.self.getResources().getConfiguration().orientation;
		return currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	}

	public boolean isChartVisible() {
		synchronized (fragmentList) 
		{
			if( fragmentList[1] == null || vpPager.getCurrentItem() != 1 )
			{
				return false;
			}
		}
		return true;
	}

	public void makeChartVisible() {
		synchronized (fragmentList) 
		{
			if( fragmentList[1] == null )
			{
				vpPager.setCurrentItem(1);
			}
		}
	}

	public static Fragment setFragment(int position, Fragment f) {
		synchronized (fragmentList) 
		{
			MainActivityGrid.fragmentList[position] = f; 
		}
		return f;
	}

	public void setGridRefreshing(boolean b) {
		GridFragment fg = null;
		synchronized (fragmentList) 
		{
			try{ fg = (GridFragment)fragmentList[0]; } catch(Exception ex){}			
		}
        if( fg != null )
        	fg.setRefreshing(b);
	}

	public void fillInfos(EditFragment editFragment, int page) {
		synchronized (fragmentList) 
		{
			if( editElementActivity != null )
				editElementActivity.setFillInfos( editFragment, page);
		}
		
	}

	public View getView() {
		return findViewById(R.id.container);
	}
	public void activateOverlayElements( boolean show )
	{
		if( show )
		{
			findViewById(R.id.relLayoutButtonBarBottomId).setVisibility(View.VISIBLE);
		}
		else
		{
			findViewById(R.id.relLayoutButtonBarBottomId).setVisibility(View.GONE);
		}
	}

	@Override
	public void saveCache(IModel list) {
		if( CacheFile.isAccessingCache() ) return;
		CacheFile cf = new CacheFile(MainActivityGrid.self);
		cf.clear();
		cf.writeTextFile(list, null);
		
	}
	@Override
	public void readCache(IModel list) {
		if( CacheFile.isAccessingCache() ) return;
		CacheFile cf = new CacheFile(MainActivityGrid.self);
		if( cf.exists() )
			cf.readTextFile(list, null);
		
	}

	private void saveCacheAsDatasource() 
	{
		CacheFile cf = new CacheFile(MainActivityGrid.self);
		
		if( !cf.containsData() )
		{
			Toast.makeText(getContext(), "Aktivierung nicht möglich, da der Cache leer ist.", Toast.LENGTH_LONG).show();
			return;
		}
		
		try {
			getDataAccess().copyFrom( cf.getFile() );
			setLastModified( getDataAccess().getLastModified() );
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			
		}
		
	}

}
