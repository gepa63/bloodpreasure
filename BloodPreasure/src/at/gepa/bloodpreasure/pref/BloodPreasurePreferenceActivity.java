package at.gepa.bloodpreasure.pref;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Looper;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import at.azure.DataAccessAndroid;
import at.azure.DataAccessAzureAndroidController;
import at.gepa.androidlib.security.PasswordEncryptionDecryption;
import at.gepa.bloodpreasure.ChartFragment;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyze;
import at.gepa.bloodpreasure.exportimport.ExportImportData;
import at.gepa.bloodpreasure.exportimport.ExportImportData.eFileType;
import at.gepa.bloodpreasure.medicine.MedicationPreference;
import at.gepa.bloodpreasure.print.PrintFragmentPagerAdapter.PrintConfig;
import at.gepa.files.LocalFileAccess;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.lib.tools.Util;
import at.gepa.net.DataAccess;
import at.gepa.net.DataAccessController;
import at.gepa.net.IElement;



public class BloodPreasurePreferenceActivity extends PreferenceActivity
implements OnSharedPreferenceChangeListener
{
	public static final String KEY_DATEFILTER = "dod";
	public static final String KEY_LINK = "prefLink";
	public static final String KEY_SUBFOLDER = "prefSubFolder";
	public static final String KEY_FTPPORT = "prefFTP_Port";
	public static final String KEY_FTPFILENAME = "prefFTPFilename";
	
	public static final String KEY_CLOUD_KEY = "prefCloud_Key";
	public static final String KEY_CLOUD_SEC = "prefCloud_Sec";
	public static final String KEY_CLOUD_BUCKET = "prefCloud_Bucket";
	
	public static final String KEY_FILENAME = "prefFilename";
	public static final String KEY_USER = "prefUser";
	public static final String KEY_PWD = "prefPwd";
	
	public static final String KEY_AZURE_ACCOUNT = "prefAzureAccount";
	public static final String KEY_AZURE_KEY = "prefAzureKey";
	public static final String KEY_AZURE_CONTAINER = "prefAzureContainer";
	
	
	public static final String KEY_BIRTHDAY = "pref_Birthday";
	
	public static final String KEY_ROUND_QUARTERS = "pref_AlignQuarters";
	public static final String KEY_MEDICATION = "prefMedicationDlg";
	public static final String KEY_TAGS = "Tags";
	public static final String KEY_SHOW_ANALYZE = "showAnalyze";
	public static final String KEY_TENSOVAL_PERSON = "pref_TensovalPerson";
	
	public static final String KEY_GEWICHT = "pref_Gewicht";
	public static final String KEY_GROESSE = "pref_Groesse";
	public static final String KEY_GESCHLECHT = "pref_Geschlecht";
	public static final String KEY_TAILE = "pref_Taile";
	public static final String KEY_HUEFTE = "pref_Huefte";
	
	public static final String KEY_PRINT_CHART_SYSTOLISCH = "pref_PrintSystolisch";
	public static final String KEY_PRINT_CHART_DIASTOLISCH = "pref_PrintDiastolisch";
	public static final String KEY_PRINT_CHART_PULS = "pref_PrintPluls";
	public static final String KEY_PRINT_CHART_GEWICHT = "pref_PrintGewicht";
	public static final String KEY_PRINT_CHART_ZUCKER = "pref_PrintZucker";
	public static final String KEY_PRINT_CHART_TEMP = "pref_PrintTemp";
	
	public static final String KEY_USE_FTPSERVER = "prefUseFTPServer";
	public static final String KEY_USE_LOCALFILE = "prefUseLocalFile";
	public static final String KEY_USE_AZUREFILE = "prefUseAzureFile";
	
	private static final String KEY_PRINTCONFIG_ANALYZE = "pref_PrintConfig_Analyze";
	private static final String KEY_PRINTCONFIG_CHART = "pref_PrintConfig_Chart";
	private static final String KEY_PRINTCONFIG_LIST = "pref_PrintConfig_List";
	private static final String KEY_PRINTCONFIG_LISTDETAIL = "pref_PrintConfig_ListDetails";
	private static final String KEY_MARKTAGSAS_UNTILREVOKE = "pref_MarkTagsAsUntilRevoke";
	
	public static final String TAG_DELIMITER = ";";

	public static class MyPreferenceFragment extends PreferenceFragment
	{
	    private CheckBoxPreference prefUseLocalFile;
	    private CheckBoxPreference prefUseFTPServer;
	    private CheckBoxPreference prefUseAzure;
	    private PreferenceScreen btPrefFTPScreen;
	    private PreferenceScreen btPrefAzureFileScreen;
	    private PreferenceScreen btPrefLocalFileScreen;

		private String [] setTextPrefs;
		@Override
	    public void onCreate(final Bundle savedInstanceState)
	    {
	        super.onCreate(savedInstanceState);
	        
	        setTextPrefs = new String []{ KEY_FILENAME, KEY_FTPFILENAME, KEY_SUBFOLDER,
	        		KEY_AZURE_ACCOUNT, KEY_AZURE_KEY, KEY_AZURE_CONTAINER,
	        		KEY_FTPPORT, KEY_USER, KEY_GEWICHT, KEY_TAILE, KEY_HUEFTE, KEY_GROESSE, KEY_MEDICATION, KEY_PWD, KEY_LINK, KEY_GESCHLECHT, KEY_TENSOVAL_PERSON};
	        
	        try 
	        {
	        	addPreferencesFromResource(R.xml.preference);
	        }
	        catch(Exception ex)
	        {
	        	ex.printStackTrace();
	        }

	        PreferenceManager pm = getPreferenceManager();
	        
	        updateTextFields( pm, KEY_BIRTHDAY, BloodPreasureAnalyze.getBirthdayString());
	        updateTextFields( pm, KEY_DATEFILTER, ChartFragment.getFilterDateString() );
	        
	        updateChartPrintFields(pm);
	        
	        DataAccess da = MainActivityGrid.getDataAccess();
        	if( da == null || da.isLocalFileActive() )
        	{
        		updateTextFields( pm, KEY_FILENAME, MainActivityGrid.getDataAccess() ==null ? BloodPreasure.DEFAULT_FILENAME() : da.getFileName() );
        	}
        	
	        if( da != null && da.isFTPActive() )
	        {
	        	updateTextFields( pm, KEY_FTPFILENAME, da.getFtpFileName() );
	        }
	        if( da != null && da.isAzureActive() )
	        {
	        	updateTextFields( pm, KEY_FTPFILENAME, da.getFileName() );
	        	updateTextFields( pm, KEY_AZURE_ACCOUNT, da.getAzureAccount() );
	        	updateTextFields( pm, KEY_AZURE_KEY, da.getAzureKey() );
	        	updateTextFields( pm, KEY_AZURE_CONTAINER, da.getAzureContainer() );
	        }
	        
	        doScreen(getPreferenceScreen());
	        
	    }

		private void updateChartPrintFields(PreferenceManager pm) {
	        Editor edit = pm.getSharedPreferences().edit();
	        edit.putBoolean(KEY_PRINT_CHART_SYSTOLISCH, ChartFragment.isSystolischUsed() );
	        edit.putBoolean(KEY_PRINT_CHART_DIASTOLISCH, ChartFragment.isDiastolischUsed() );
	        edit.putBoolean(KEY_PRINT_CHART_PULS, ChartFragment.isPulsUsed() );
	        edit.putBoolean(KEY_PRINT_CHART_GEWICHT, ChartFragment.isGewichtUsed() );
	        edit.putBoolean(KEY_PRINT_CHART_ZUCKER, ChartFragment.isZuckerUsed() );
	        edit.putBoolean(KEY_PRINT_CHART_TEMP, ChartFragment.isTemperturUsed() );
	        edit.apply();
	        edit.commit();
		}

		private PreferenceManager updateTextFields(PreferenceManager pm, String key, String txt) {
			if( pm == null )
				pm = getPreferenceManager();
			Preference dp = pm.findPreference(key);
			if( dp != null && dp instanceof DatePreference )
			{
				((DatePreference)dp).setDate( txt, at.gepa.lib.tools.time.TimeTool.TEMPLATE_DATE );
				return pm;
			}
			if( dp != null && dp instanceof MedicationPreference )
			{
				((MedicationPreference)dp).setMedication( txt );
				return pm;
			}
			
			
			dp = pm.findPreference(key);
			if( dp != null && dp instanceof DatePreference )
			{
				((DatePreference)dp).setDate( txt, at.gepa.lib.tools.time.TimeTool.TEMPLATE_DATE );
				return pm;
			}
			if( dp != null && dp instanceof MedicationPreference )
			{
				((MedicationPreference)dp).setMedication( txt );
				return pm;
			}
			
			dp = pm.findPreference(key);
			if( dp != null && dp instanceof FileChoosePreference )
			{
				((FileChoosePreference)dp).setText( txt );
				return pm;
			}
			
			if( dp != null && dp instanceof EditTextPreference )
			{
				((EditTextPreference)dp).setText(txt);
				return pm;
			}
			if( dp != null && dp instanceof CheckBoxPreference )
			{
				((CheckBoxPreference)dp).setChecked( Boolean.parseBoolean(txt) );
				return pm;
			}
			
			if( dp != null && dp instanceof ListPreference )
			{
				ListPreference lp = (ListPreference)dp;
				if( txt.equalsIgnoreCase("männlich" ) )
				{
					lp.setValueIndex(0);
				}
				return pm;
			}
			if( dp != null && dp instanceof EditTextPreference )
			{
				EditTextPreference ep = (EditTextPreference)dp;
				ep.setText(txt);
			}
			
			return pm;
		}

		private void doScreen(PreferenceScreen screen) {
			if( screen.getKey() != null && screen.getKey().equals("btPrefFTPScreen") )
				btPrefFTPScreen = screen;
			else if( screen.getKey() != null && screen.getKey().equals("btPrefLocalFileScreen") )
				btPrefLocalFileScreen = screen;
			else if( screen.getKey() != null && screen.getKey().equals("btPrefAzureFileScreen") )
				btPrefAzureFileScreen = screen;
			
			for(int x = 0; x < screen.getPreferenceCount(); x++)
			{
				Object o = screen.getPreference(x);
				if( o instanceof PreferenceScreen )
				{
					doScreen( (PreferenceScreen)o );
				}
				else if( o instanceof PreferenceCategory )
				{
					PreferenceCategory lol = (PreferenceCategory) o;
					//We do not use Cloude Interface, so remove it for now
					boolean b = ( lol.getKey() != null && (lol.getKey().equals("prefCloudCategory") || lol.getKey().equals("prefLocalDatabaseCategory") ) );
					if( b )
						screen.removePreference(lol);
					else
						doCategory( lol, b );
				}
		    }			
		}
		public void setFTPServerIsActive(boolean flag)
		{
			prefUseLocalFile.setChecked(!flag);
			prefUseFTPServer.setChecked(flag);
			if( prefUseAzure != null )
				prefUseAzure.setChecked(false);
			updateStorageTitle();
		}
		public void setAzureServerIsActive(boolean flag)
		{
			prefUseFTPServer.setChecked(!flag);
			prefUseLocalFile.setChecked(!flag);
			if( prefUseAzure != null )
				prefUseAzure.setChecked(flag);
			updateStorageTitle();
		}

		private void doCategory(PreferenceCategory lol, boolean doDisableall) {
	        for(int y = 0; y < lol.getPreferenceCount(); y++)
	        {
	            Preference pref = lol.getPreference(y);
	            if( doDisableall )
	            {
	            	pref.setEnabled(!doDisableall);
	            	
	            }
	            findAndReplaceTitle(pref, null);
	            if( pref.getKey().equals(KEY_USE_FTPSERVER) )
	            {
	            	prefUseFTPServer = (CheckBoxPreference) pref;
		            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

		                @Override
		                public boolean onPreferenceClick(Preference preference) {
		                	setFTPServerIsActive(prefUseFTPServer.isChecked());
		                    return false;
		                }

		            });
		            updateStorageTitle();
	            }
	            else if( pref.getKey().equals(KEY_USE_LOCALFILE) )
	            {
	            	this.prefUseLocalFile = (CheckBoxPreference) pref;
		            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

		                @Override
		                public boolean onPreferenceClick(Preference preference) {
		                	setFTPServerIsActive(!prefUseLocalFile.isChecked());
		                    return false;
		                }

		            });
		            updateStorageTitle();
	            }
	            else if( pref.getKey().equals(KEY_USE_AZUREFILE) )
	            {
	            	this.prefUseAzure = (CheckBoxPreference) pref;
		            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

		                @Override
		                public boolean onPreferenceClick(Preference preference) {
		                	setAzureServerIsActive( prefUseAzure.isChecked() );
		                    return false;
		                }

		            });
		            updateStorageTitle();
	            }
	            
	        }
		}
		public void updateStorageTitle() {
	        String ftpt = "FTP Einstellungen";
	        if( prefUseFTPServer != null && prefUseFTPServer.isChecked() )
	        	ftpt += ": Ja";
	        if( btPrefFTPScreen != null )
	        	btPrefFTPScreen.setTitle( ftpt );
	        String localFileTitle = "Lokale Datei Einstellungen";
	        if( prefUseLocalFile.isChecked() )
	        	localFileTitle += ": Ja";
	        btPrefLocalFileScreen.setTitle(localFileTitle);
	        
	        String azureTitle = "Azure Einstellungen";
	        if( prefUseAzure != null && prefUseAzure.isChecked() )
	        	azureTitle += ": Ja";
	        if( btPrefAzureFileScreen != null )
	        	btPrefAzureFileScreen.setTitle(azureTitle);
		}
		

		private void updatePrefTitle(SharedPreferences mySharedPreferences, Preference pref, String key) {
    		String value = mySharedPreferences.getString(key, "");
    		String title = pref.getTitle().toString();
    		if( title.contains(":") )
    		{
    			String sa[] = title.split(":");
    			title = sa[0];
    		}
    		if( !value.isEmpty() )
    		{
    			if( key.equals(KEY_MEDICATION))
    				value = BloodPreasureAnalyze.getMedicationObject().getMedicationEvalText();
    			else if( key.equals(KEY_PWD))
    				value = "******";
    		}
			pref.setTitle( title + ": " + value);
		}
		public void findAndReplaceTitle(SharedPreferences mySharedPreferences, String key) 
		{
			PreferenceManager pm = getPreferenceManager();
			if( pm == null )
			{
				System.out.println("getPreferenceManager returns null for key " + key);
				return;
			}
			Preference p = pm.findPreference(key);
			findAndReplaceTitle( p, mySharedPreferences);
		}		
		public void findAndReplaceTitle(Preference pref, SharedPreferences mySharedPreferences) 
		{
    		if( pref == null ) return;
    		if( setTextPrefs == null ) return;
    		
    		if( mySharedPreferences == null )
    			mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
    		
	        for( int st = 0; st < setTextPrefs.length; st++ )
	        {
	        	if( pref.getKey() != null && pref.getKey().equals(setTextPrefs[st]))
	        	{
	    			updatePrefTitle( mySharedPreferences, pref, setTextPrefs[st]);
	        		break;
	        	}
	        }
		
		}

		public int importKeyValuePairs(KeyValuePairPrefs kvp) {
			int cnt=0;

			PreferenceManager pm = getPreferenceManager();
			for( IElement e : kvp.getValues() )
			{
				KeyValuePairPrefs.KeyValue kv = (KeyValuePairPrefs.KeyValue)e;

				String value = kv.getValue();
				if( kv.getKey().equals(KEY_PWD) )
				{
					String v = kv.getValue(); 
					if( v.startsWith("§") )
					{
						PasswordEncryptionDecryption d = new PasswordEncryptionDecryption( v.substring(1), true);
						try {
							v = d.decrypt();
						} catch (Exception e1) {
						}
					}
					value = v;
				}
				else
				{
					Object o = kv.getValue();
					if( o == null ) o = "";
					if( o instanceof Boolean)
						value = ((Boolean)o).toString();
					else if( o instanceof Integer)
						value = ((Integer)o).toString();
					else if( o instanceof Float)
						value = ((Float)o).toString();
					else
						value = o.toString();

				}
				
				updateTextFields( pm, kv.getKey(), value );
				
				if( kv.getKey().equals(KEY_TAGS) )
				{
					ArrayList<String> tagListToSave = Util.convertToArray( kv.getValue() );
					BloodPreasurePreferenceActivity.saveTags( tagListToSave  );
				}
				if( pm.findPreference( kv.getKey() ) != null )
					cnt++;
					
			}
			setFTPServerIsActive( ((CheckBoxPreference)pm.findPreference(KEY_USE_FTPSERVER)).isChecked() );
			CheckBoxPreference cbpref =(CheckBoxPreference)pm.findPreference(KEY_USE_AZUREFILE);
			if( cbpref != null)
				setAzureServerIsActive( cbpref.isChecked() );
			
			return cnt;
		}

		
		private int export(PreferenceScreen screen, KeyValuePairPrefs kvp) {
			int cnt = 0;
			for(int x = 0; x < screen.getPreferenceCount(); x++)
			{
				Object o = screen.getPreference(x);
				if( o instanceof PreferenceScreen )
				{
					cnt += export( (PreferenceScreen)o, kvp );
				}
				else if( o instanceof PreferenceCategory )
				{
					PreferenceCategory lol = (PreferenceCategory) o;
					//We do not use Cloude Interface, so remove it for now
					cnt += export( lol, kvp );
				}
		    }
			return cnt;
		}
		
		private int export(PreferenceCategory lol, KeyValuePairPrefs kvp) {
			int cnt = 0;
	        for(int y = 0; y < lol.getPreferenceCount(); y++)
	        {
	            Preference pref = lol.getPreference(y);
	            if( pref == null ) continue;
				if( pref instanceof DatePreference )
				{
					Calendar date = ((DatePreference)pref).getDate();//.get .setDate( txt, TimeTool.TEMPLATE_DATE );
					kvp.add( pref.getKey(), at.gepa.lib.tools.time.TimeTool.toDateString(date));
					cnt++;
				}
				else if( pref instanceof MedicationPreference )
				{
					kvp.add( pref.getKey(), ((MedicationPreference)pref).getMedication().toString() );
					cnt++;
				}
				else if( pref instanceof FileChoosePreference )
				{
					kvp.add( pref.getKey(), ((FileChoosePreference)pref).getFileName() );
					cnt++;
				}
				else if( pref instanceof CheckBoxPreference )
				{
					kvp.add( pref.getKey(), ""+((CheckBoxPreference)pref).isChecked() );
					cnt++;
				}
				else if( pref instanceof ListPreference )
				{
					ListPreference lp = (ListPreference)pref;
					kvp.add( pref.getKey(), (lp.getValue() == null ? "" : lp.getValue()) );
					cnt++;
				}
				else if( pref instanceof EditTextPreference )
				{
					EditTextPreference ep = (EditTextPreference)pref;
					kvp.add( pref.getKey(), ep.getText() );
					cnt++;
				}
	        }
	        return cnt;
		}

		public void exportKeyValuePairs(KeyValuePairPrefs kvp) {
			export( getPreferenceScreen(), kvp );
		}
	}

	
	//private Bundle savedInstanceState;
	private MyPreferenceFragment preferenceFragment;
	private boolean needReload;
	private int port;
	private String filename;
	private String ftpLink;
	private String pwd;
	private String username;
	private String subFolder;
//	private String cloudKey;
//	private String cloudSecKey;
//	private String cloudBucket;

	private boolean needChartReload;
	private String azureAccount;
	private String azureKey;
	private String azureContainer;
	
	@Override 
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		needReload = false;
		needChartReload = false;
		
		this.preferenceFragment = new MyPreferenceFragment();
		
		getFragmentManager().beginTransaction().replace(android.R.id.content, preferenceFragment).commit();
		
		try {
			loadDefaulPref();
		}
		catch(Throwable ex){
			ex.printStackTrace();
		}
	}
  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.pref_menu, menu);
		
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if( id == R.id.action_settings_imp )
		{
			importPrefsFromFile();
		}
		else if( id == R.id.action_settings_save )
		{
			exportPrefsToFile();
			return true;
		}
		else if( id == R.id.action_settings_cancel )
		{
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void exportPrefsToFile() {
		final KeyValuePairPrefs kvp = new KeyValuePairPrefs();
		exportKeyValuePairs(kvp);
		
		at.gepa.androidlib.ui.SimpleFileDialog FileSaveDialog =  new at.gepa.androidlib.ui.SimpleFileDialog(this, "FileSave", new at.gepa.androidlib.ui.SimpleFileDialog.SimpleFileDialogListener()
		{
			@Override
			public void onChosenDir(String chosenDir) 
			{
				// The code in this function will be executed when the dialog OK button is pushed
				//Toast.makeText(QuickPhoneCallerConfig.this, "Chosen FileSaveDialog File: " + chosenDir, Toast.LENGTH_LONG).show();
				File f = new File(chosenDir);
				if( f.isDirectory() )
					f = new File(f, BloodPreasure.DEFAULT_SETTINGS_BACKUPFILE());
				else if( f.exists() )
					f.delete();
				try {
					LocalFileAccess lfa = new LocalFileAccess(f.getAbsolutePath(), false, MainActivityGrid.self);
					if( lfa.writeTextFile(kvp.createModel(), null, kvp.createHeaderFactory()) )
						Toast.makeText(MainActivityGrid.self, "Fertig!", Toast.LENGTH_LONG).show();
					else
						throw lfa.getLastError(); 
				} catch (Exception e) {
					Toast.makeText(MainActivityGrid.self, e.getMessage(), Toast.LENGTH_LONG).show();
				}
				
			}
		}, true);
		
		//You can change the default filename using the public variable "Default_File_Name"
		FileSaveDialog.setDefault_File_Name( BloodPreasure.DEFAULT_SETTINGS_BACKUPFILE() );
		FileSaveDialog.chooseFile_or_Dir();
	}
	
	private void importPrefsFromFile() {
		at.gepa.androidlib.ui.SimpleFileDialog FileSaveDialog =  new at.gepa.androidlib.ui.SimpleFileDialog(this, "FileOpen", new at.gepa.androidlib.ui.SimpleFileDialog.SimpleFileDialogListener()
		{
			@Override
			public void onChosenDir(String chosenDir) 
			{
				File f = new File(chosenDir);
				if( f.isDirectory() )
					f = new File(f, BloodPreasure.DEFAULT_SETTINGS_BACKUPFILE());
				try {
					LocalFileAccess lfa = new LocalFileAccess(f.getAbsolutePath(), false, MainActivityGrid.self);
					
					final KeyValuePairPrefs kvp = new KeyValuePairPrefs();
					
					if( lfa.readTextFile( kvp.createModel(), null, kvp.createHeaderFactory()) )
					{
						int cnt = preferenceFragment.importKeyValuePairs(kvp);
						String msg = "";
						switch( cnt )
						{
						case 0:
							msg = "Keine Werte importiert!";
							break;
						case 1:
							msg = "Einen Wert importiert!";
							break;
						default:
							msg = cnt + " Werte importiert!";
							break;
						}
						Toast.makeText(MainActivityGrid.self, "Fertig - " + msg, Toast.LENGTH_LONG).show();
						
//						Thread thread = new Thread(new Runnable(){
//
//							@Override
//							public void run() {
//								finish();
//								
//								Thread thread2 = new Thread(new Runnable(){
//
//									@Override
//									public void run() {
//										MainActivityGrid.self.restartPreferences();
//									}});
//								thread2.start();
//								
//							}});
//						thread.start();
					}
					else
						throw lfa.getLastError(); 
					
					MainActivityGrid.createDownloadTask();
				} catch (Exception e) {
					Toast.makeText(MainActivityGrid.self, e.getMessage(), Toast.LENGTH_LONG).show();
				}
				
			}
		}, true);
		FileSaveDialog.setDefault_File_Name( BloodPreasure.DEFAULT_SETTINGS_BACKUPFILE() );
		FileSaveDialog.chooseFile_or_Dir();
		
	}
	
	
	
	private void loadDefaulPref()
	{
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mySharedPreferences.registerOnSharedPreferenceChangeListener(this);
		
		Editor edit = mySharedPreferences.edit();
		
		if( MainActivityGrid.getDataAccess() != null )
		{
			boolean isFtpActive = MainActivityGrid.getDataAccess().isFTPActive();
			boolean isLocalFile = MainActivityGrid.getDataAccess().isLocalFileActive();
			boolean isAzureActive = MainActivityGrid.getDataAccess().isAzureActive();
			
			setIfNotExists( mySharedPreferences, edit, KEY_LINK, MainActivityGrid.getDataAccess().getFtpLink() );
			setIfNotExists( mySharedPreferences, edit, KEY_SUBFOLDER, MainActivityGrid.getDataAccess().getSubFolder() );
			
			setIfNotExists( mySharedPreferences, edit, KEY_FTPFILENAME, MainActivityGrid.getDataAccess().getFtpFileName() );
			setIfNotExists( mySharedPreferences, edit, KEY_FILENAME, MainActivityGrid.getDataAccess().getBaseFileName() );

			setIfNotExists( mySharedPreferences, edit, KEY_AZURE_ACCOUNT, MainActivityGrid.getDataAccess().getAzureAccount() );
			setIfNotExists( mySharedPreferences, edit, KEY_AZURE_KEY, MainActivityGrid.getDataAccess().getAzureKey() );
			setIfNotExists( mySharedPreferences, edit, KEY_AZURE_CONTAINER, MainActivityGrid.getDataAccess().getAzureContainer() );
			
			if( !MainActivityGrid.getDataAccess().getBaseFileName().equals(MainActivityGrid.getDataAccess().getFtpFileName()) )
			{
				if( isFtpActive && isAzureActive == false )
				{
					isFtpActive = false;
					isLocalFile = true;
				}
			}
			
			setIfNotExists( mySharedPreferences, edit, KEY_PWD, MainActivityGrid.getDataAccess().getPassword() );
			setIfNotExists( mySharedPreferences, edit, KEY_USER, MainActivityGrid.getDataAccess().getUserName() );
			
			setIfNotExists( mySharedPreferences, edit, KEY_CLOUD_KEY, MainActivityGrid.getDataAccess().getCloudKey() );
			setIfNotExists( mySharedPreferences, edit, KEY_CLOUD_SEC, MainActivityGrid.getDataAccess().getCloudSecKey() );
			setIfNotExists( mySharedPreferences, edit, KEY_CLOUD_BUCKET, MainActivityGrid.getDataAccess().getCloudBucket() );
			setIfNotExists( mySharedPreferences, edit, KEY_FTPPORT, "" + MainActivityGrid.getDataAccess().getFtpPort() );
			
			edit.putBoolean(KEY_USE_FTPSERVER, isFtpActive );
			edit.putBoolean(KEY_USE_LOCALFILE, isLocalFile );
			edit.putBoolean(KEY_USE_AZUREFILE, isAzureActive );
			
		}
		else
		{
			edit.putBoolean(KEY_USE_LOCALFILE, true );
			edit.putBoolean(KEY_USE_AZUREFILE, false );
			setIfNotExists( mySharedPreferences, edit, KEY_AZURE_ACCOUNT, DataAccessAzureAndroidController.DEFAULT_ACCOUNT );
			setIfNotExists( mySharedPreferences, edit, KEY_AZURE_KEY, "" );
			setIfNotExists( mySharedPreferences, edit, KEY_AZURE_CONTAINER, DataAccessAzureAndroidController.DEFAULT_CONTAINER );
		}
		
		setIfNotExists( mySharedPreferences, edit, KEY_GEWICHT, "" + BloodPreasureAnalyze.getGewicht() );
		setIfNotExists( mySharedPreferences, edit, KEY_GROESSE, "" + BloodPreasureAnalyze.getGroesse() );
		setIfNotExists( mySharedPreferences, edit, KEY_HUEFTE, "" + BloodPreasureAnalyze.getHuefte() );
		setIfNotExists( mySharedPreferences, edit, KEY_TAILE, "" + BloodPreasureAnalyze.getTaile() );
		setIfNotExists( mySharedPreferences, edit, KEY_MEDICATION, BloodPreasureAnalyze.getMedication());
		setIfNotExists( mySharedPreferences, edit, KEY_GESCHLECHT, BloodPreasureAnalyze.getGeschlecht());
		setIfNotExists( mySharedPreferences, edit, KEY_DATEFILTER, ChartFragment.getFilterDateStringUS() );
		setIfNotExists( mySharedPreferences, edit, KEY_SHOW_ANALYZE, true );
		setIfNotExists( mySharedPreferences, edit, KEY_ROUND_QUARTERS, true );
		setIfNotExists( mySharedPreferences, edit, KEY_TENSOVAL_PERSON, "1" );
		
		edit.apply();
		edit.commit();
	}
	
	private void setIfNotExists(SharedPreferences mySharedPreferences, Editor edit, String key, boolean defValue) {
		if( !mySharedPreferences.contains(key) )
			edit.putBoolean(key, defValue);
	}

	private void setIfNotExists(SharedPreferences mySharedPreferences, Editor edit, String key, String defdata) 
	{
		String value = mySharedPreferences.getString(key, "");
		if( value.isEmpty() )
		{
			edit.putString(key, defdata);
		}
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean useLocalFile = mySharedPreferences.getBoolean(KEY_USE_LOCALFILE, true);
		if( useLocalFile )
		{
			FileChoosePreference pfn = (FileChoosePreference)preferenceFragment.getPreferenceManager().findPreference(KEY_FILENAME);
			Editor edit = mySharedPreferences.edit();
			edit.putString(KEY_FILENAME, pfn.getFileName());
			edit.apply();
			edit.commit();
		}
		MainActivityGrid.self.clearDataAccessObject();
		BloodPreasurePreferenceActivity.initFileAccessData();
		MainActivityGrid.self.reloadChart();
	}
	public void _onBackPressed()
	{
		boolean configured = isConfigured(PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self)); 
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean useLocalFile = mySharedPreferences.getBoolean(KEY_USE_LOCALFILE, true);
		boolean isAzureActive =mySharedPreferences.getBoolean(KEY_USE_AZUREFILE, false);
		
		FileChoosePreference pfn = (FileChoosePreference)preferenceFragment.getPreferenceManager().findPreference(KEY_FILENAME);
		String fn = pfn.getFileName();
		String tfn = mySharedPreferences.getString(KEY_FILENAME, "");
		if( !tfn.equals(fn) )
		{
			Editor edit = mySharedPreferences.edit();
			edit.putString(KEY_FILENAME, fn);
			edit.apply();
			edit.commit();
		}
		if( useLocalFile || isAzureActive)
			filename = fn;
		
		if( needReload || !configured)
		{
			boolean useFTPServer = mySharedPreferences.getBoolean(KEY_USE_FTPSERVER, false);

			if( useFTPServer )
			{
				if( filename.indexOf('/') == 0 )
					filename = filename.substring( filename.lastIndexOf('/')+1 );
				Editor editor = mySharedPreferences.edit();
				editor.putString(KEY_FTPFILENAME, filename );
				editor.commit();
			}
			
			String medicationPacked = BloodPreasureAnalyze.getMedication();
			if( !mySharedPreferences.getString(KEY_MEDICATION, medicationPacked).equals( medicationPacked ) )
			{
				Editor editor = mySharedPreferences.edit();
				editor.putString(KEY_MEDICATION, medicationPacked );
				editor.commit();
			}
			
			if( filename == null )
			{
				if( useFTPServer )
					onSharedPreferenceChanged(mySharedPreferences, KEY_FTPFILENAME);
				else
					onSharedPreferenceChanged(mySharedPreferences, KEY_FILENAME);
			}
			if( username == null )
				onSharedPreferenceChanged(mySharedPreferences, KEY_USER); 
			if( pwd == null )
				onSharedPreferenceChanged(mySharedPreferences, KEY_PWD); 
			if( ftpLink == null )
				onSharedPreferenceChanged(mySharedPreferences, KEY_LINK);
			if( port == 0 )
				onSharedPreferenceChanged(mySharedPreferences, KEY_FTPPORT);
			if( subFolder == null )
				onSharedPreferenceChanged(mySharedPreferences, KEY_SUBFOLDER);
			if( azureAccount == null )
				onSharedPreferenceChanged(mySharedPreferences, KEY_AZURE_ACCOUNT);
			if( azureKey == null )
				onSharedPreferenceChanged(mySharedPreferences, KEY_AZURE_KEY);
			if( azureContainer == null )
				onSharedPreferenceChanged(mySharedPreferences, KEY_AZURE_CONTAINER);
			
			if( !configured )
			{
				Editor editor = mySharedPreferences.edit();
				if( useFTPServer )
				{
					editor.putString(KEY_FTPFILENAME, filename );
				}
				else
				{
					editor.putString(KEY_FILENAME, filename );
					subFolder = "";
				}
				editor.putString(KEY_SUBFOLDER, subFolder );
				editor.commit();
			}
			if( !useLocalFile && !useFTPServer && !isAzureActive)
			{
				Editor editor = mySharedPreferences.edit();
				if( azureKey != null && !azureKey.isEmpty() )
				{
					isAzureActive = true;
					editor.putBoolean(KEY_USE_AZUREFILE, isAzureActive);
				}
				else
				{
					useLocalFile = true;
					editor.putBoolean(KEY_USE_LOCALFILE, useLocalFile);
					editor.putString(KEY_AZURE_KEY, "");
				}
				editor.commit();
			}
			if( useLocalFile && useFTPServer )
			{
				useFTPServer = false;
				Editor editor = mySharedPreferences.edit();
				editor.putBoolean(KEY_USE_FTPSERVER, useFTPServer);
				editor.putString(KEY_AZURE_KEY, "");
				editor.commit();
			}
			
			DataAccess da = null;
			if( useLocalFile )
			{
				da = DataAccess.createInstance( this.filename );
				try {
					da.createIfNotExistsFile();
				}
				catch(Exception ex)
				{
					Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
					return;
				}
			}
			else if( isAzureActive )
			{
				da = DataAccessAndroid.createInstance( this.filename, this.azureAccount, this.azureKey, this.azureContainer );
				try {
					da.createIfNotExistsFile();
				}
				catch(Exception ex)
				{
					Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
					return;
				}
			}
			else
			{
				String sf = "";
				if( useFTPServer )
					sf = this.subFolder;
				da = DataAccess.createInstance( this.filename, 
						this.username, 
						this.pwd, 
						this.ftpLink, 
						sf, //subpath, 
						this.port );
			}
			try {
				da.validate();
				boolean doreload = ( MainActivityGrid.getDataAccess() == null );
				MainActivityGrid.self.setDataAccessObject( da );
				if( doreload )
					MainActivityGrid.self.doDownloadFile();
			} catch (Exception e) {
				
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				return;
			}
			
		}
		
		if( needChartReload )
		{
			MainActivityGrid.self.reloadChart();
		}
		super.onBackPressed();
	}
  
	public void clearSharedPreferences() {
		SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}
    
  
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
	{
		if( key.equals(KEY_DATEFILTER))
		{
			String sDate = sharedPreferences.getString(key, ChartFragment.DEFAULT_FILTER);
			ChartFragment.setDateFilter(sDate);
		}
		if( key.equals(KEY_BIRTHDAY) )
		{
			if( sharedPreferences instanceof DatePreference)
				BloodPreasureAnalyze.setBirthday( ((DatePreference)sharedPreferences).getDate() );
			else
				BloodPreasureAnalyze.setBirthday( sharedPreferences.getString(KEY_BIRTHDAY, BloodPreasureAnalyze.getBirthdayString() ) );
		}
		if( key.equals(KEY_GEWICHT) )
		{
			BloodPreasureAnalyze.setGewicht( Float.parseFloat(sharedPreferences.getString(KEY_GEWICHT, ""+BloodPreasureAnalyze.getGewicht())) );
		}
		if( key.equals(KEY_GROESSE) )
		{
			BloodPreasureAnalyze.setGewicht( Integer.parseInt(sharedPreferences.getString(KEY_GROESSE, ""+BloodPreasureAnalyze.getGroesse())) );
		}
		if( key.equals(KEY_HUEFTE) )
		{
			BloodPreasureAnalyze.setGewicht( Integer.parseInt(sharedPreferences.getString(KEY_HUEFTE, ""+BloodPreasureAnalyze.getHuefte() )) );
		}
		if( key.equals(KEY_TAILE) )
		{
			BloodPreasureAnalyze.setGewicht( Integer.parseInt(sharedPreferences.getString(KEY_TAILE, ""+BloodPreasureAnalyze.getTaile() )) );
		}
		
		if( key.equals(KEY_MEDICATION) )
		{
			BloodPreasureAnalyze.setMedication( sharedPreferences.getString(KEY_MEDICATION, BloodPreasureAnalyze.getMedication()) );
		}
		if( key.equals(KEY_GESCHLECHT) )
		{
			BloodPreasureAnalyze.setGeschlecht( sharedPreferences.getString(KEY_GESCHLECHT, BloodPreasureAnalyze.getGeschlecht()) );
		}
		if( key.equals(KEY_ROUND_QUARTERS))
		{
			BloodPreasureAnalyze.setRoundQuarters( sharedPreferences.getBoolean(KEY_ROUND_QUARTERS, BloodPreasureAnalyze.getRoundQuarters() ) );
		}
		
		if( key.equals(KEY_FTPPORT) )
		{
			needReload = true;
			
			this.port = Integer.parseInt(sharedPreferences.getString(KEY_FTPPORT, ""+
					(MainActivityGrid.getDataAccess() == null ? 21 : MainActivityGrid.getDataAccess().getFtpPort()) ));
		}
		if( key.equals(KEY_USE_LOCALFILE) || key.equals(KEY_USE_FTPSERVER) )
		{
			needReload = true;
		}
		if( key.equals(KEY_FILENAME) )
		{
			if( sharedPreferences.getBoolean(KEY_USE_LOCALFILE, false) || sharedPreferences.getBoolean(KEY_USE_AZUREFILE, false) )
			{
				needReload = true;
				this.filename = sharedPreferences.getString(KEY_FILENAME,
					MainActivityGrid.getDataAccess() == null ? BloodPreasure.DEFAULT_FILENAME() :
					MainActivityGrid.getDataAccess().getFileName() );
			}
		}
		if( key.equals(KEY_FTPFILENAME) )
		{
			if( sharedPreferences.getBoolean(KEY_USE_FTPSERVER, false) )
			{
				needReload = true;
				this.filename = sharedPreferences.getString(KEY_FTPFILENAME,
					MainActivityGrid.getDataAccess() == null ? BloodPreasure.DEFAULT_FTP_FILENAME() :
					MainActivityGrid.getDataAccess().getFtpFileName() );
			}
		}
		if( key.equals(KEY_SUBFOLDER))
		{
			needReload = true;
			this.subFolder = sharedPreferences.getString(KEY_SUBFOLDER,
				MainActivityGrid.getDataAccess() == null ? BloodPreasure.DEFAULT_SUBFOLDER() :
				MainActivityGrid.getDataAccess().getSubFolder() );
			
		}
		if( key.equals(KEY_AZURE_ACCOUNT))
		{
			needReload = true;
			this.azureAccount = sharedPreferences.getString(KEY_AZURE_ACCOUNT,
				MainActivityGrid.getDataAccess() == null ? DataAccessAzureAndroidController.DEFAULT_ACCOUNT :
				MainActivityGrid.getDataAccess().getAzureAccount() );
			
		}
		if( key.equals(KEY_AZURE_KEY))
		{
			needReload = true;
			this.azureKey = sharedPreferences.getString(KEY_AZURE_KEY,
				MainActivityGrid.getDataAccess() == null ? "" : MainActivityGrid.getDataAccess().getAzureAccount() );
		}
		if( key.equals(KEY_AZURE_CONTAINER))
		{
			needReload = true;
			this.azureContainer = sharedPreferences.getString(KEY_AZURE_CONTAINER,
				MainActivityGrid.getDataAccess() == null ? DataAccessAzureAndroidController.DEFAULT_CONTAINER : MainActivityGrid.getDataAccess().getAzureAccount() );
		}
		
		if( key.startsWith("pref_Print") )
		{
			needChartReload = true;
		}
		

//		if( key.equals(KEY_CLOUD_KEY) )
//		{
//			needReload = true;
//			this.cloudKey = sharedPreferences.getString(KEY_CLOUD_KEY, MainActivityGrid.getDataAccess().getCloudKey() );
//		}
//		if( key.equals(KEY_CLOUD_SEC) )
//		{
//			needReload = true;
//			this.cloudSecKey = sharedPreferences.getString(KEY_CLOUD_SEC, MainActivityGrid.getDataAccess().getCloudSecKey() );
//		}
//		if( key.equals(KEY_CLOUD_BUCKET) )
//		{
//			needReload = true;
//			this.cloudBucket = sharedPreferences.getString(KEY_CLOUD_BUCKET, MainActivityGrid.getDataAccess().getCloudBucket() );
//		}
		
		if( key.equals(KEY_LINK) )
		{
			needReload = true;
			ftpLink = sharedPreferences.getString(KEY_LINK,
					MainActivityGrid.getDataAccess() == null ? "" : MainActivityGrid.getDataAccess().getFtpLink() );
		}
		if( key.equals(KEY_PWD) )
		{
			needReload = true;
			this.pwd = sharedPreferences.getString(KEY_PWD,
					MainActivityGrid.getDataAccess() == null ? "" : MainActivityGrid.getDataAccess().getPassword() );
		}
		if( key.equals(KEY_USER) )
		{
			needReload = true;
			this.username = sharedPreferences.getString(KEY_USER,
					MainActivityGrid.getDataAccess() == null ? "" : MainActivityGrid.getDataAccess().getUserName() );
		}
		if( key.equals(KEY_SHOW_ANALYZE) )
		{
			boolean oldval = BloodPreasureAnalyze.SHOW_ANALYZE_ON_MAINSCREEN;
			BloodPreasureAnalyze.setShowAnalyze( sharedPreferences.getBoolean(KEY_SHOW_ANALYZE, true) );
			if( oldval != BloodPreasureAnalyze.SHOW_ANALYZE_ON_MAINSCREEN || MainActivityGrid.self.isAnalyzeVisible() != BloodPreasureAnalyze.SHOW_ANALYZE_ON_MAINSCREEN  )
				MainActivityGrid.self.showOrHideAnalyzeFields();
		}
		preferenceFragment.findAndReplaceTitle( sharedPreferences, key );
	}
	
	public static void checkSettings() {
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				Looper.prepare();
				SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
				
				if( !isConfigured(mySharedPreferences) || MainActivityGrid.getDataAccess() == null || !MainActivityGrid.getDataAccess().isConfigured() ) 
				{
					Intent i = new Intent(MainActivityGrid.self, BloodPreasurePreferenceActivity.class);
					MainActivityGrid.self.startActivityForResult(i, 99);
				}
			}});
		t.start();
	}
	
	protected static boolean isConfigured(SharedPreferences mySharedPreferences) {
		return mySharedPreferences.contains(KEY_FILENAME) || mySharedPreferences.contains(KEY_FTPFILENAME);
	}

	public static ArrayList<String> getTags()
	{
		ArrayList<String> tags = new ArrayList<String>();
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		String tagsText = mySharedPreferences.getString(KEY_TAGS, "").trim();
		
		if( tagsText == null || tagsText.isEmpty() || tagsText.indexOf(TAG_DELIMITER) < 0) 
		{
			tags.add("Links");
			tags.add("Rechts");
			tags.add("Fieber");
			tags.add("Kopfschmerzen");
		}
		else
			tags = new ArrayList<String>(Arrays.asList(tagsText.split(TAG_DELIMITER)));
		return tags;
	}

	public static void saveTags(ArrayList<String> list) {
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		Editor editor = mySharedPreferences.edit();
		String sl = convertToString( list ) ;
		editor.putString(KEY_TAGS, sl);
		editor.commit();
		
	}
	
	private static String convertToString(ArrayList<String> list) {

        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (String s : list)
        {
            sb.append(delim);
            sb.append(s);
            delim = TAG_DELIMITER;
        }
        return sb.toString();
    }
	private ArrayList<String> convertToArray(String value) {
		ArrayList<String> l = new ArrayList<String>();
		String sa [] = value.split(TAG_DELIMITER);
		for( String s : sa )
		{
			if( s != null && !s.isEmpty() )
			{
				l.add(s);
			}
		}
		return l;
	}

	public static void initFileAccessData() {
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		
		if( isConfigured(mySharedPreferences)  ) 
		{
			String df = mySharedPreferences.getString(KEY_DATEFILTER, "");
			if( !df.isEmpty() )
				ChartFragment.setDateFilter(df);
			
//			DataAccess d = DataAccess.createInstance(mySharedPreferences.getString(KEY_FILENAME, MainActivityGrid.DEFAULT_FILENAME), 
//					mySharedPreferences.getString(KEY_USER, "" ), 
//					mySharedPreferences.getString(KEY_PWD, "" ), 
//					"", //mySharedPreferences.getString(KEY_LINK, "" ), 
//					"", //subpath, 
//					0, //Integer.parseInt(mySharedPreferences.getString(KEY_FTPPORT, "" )), 
//					"6r1wiyd6b12joqe",//mySharedPreferences.getString(KEY_CLOUD_KEY, "" ), 
//					"kelhrhbxs88xt56", //mySharedPreferences.getString(KEY_CLOUD_SEC, "" ), 
//					""//mySharedPreferences.getString(KEY_CLOUD_BUCKET, "" )
//					); 
//			d.setHandler(MainActivityGrid.self);
			
			boolean useFTPServer = mySharedPreferences.getBoolean(KEY_USE_FTPSERVER, false);
			boolean useAzureFile = mySharedPreferences.getBoolean(KEY_USE_AZUREFILE, false);
			boolean useLocalFile = mySharedPreferences.getBoolean(KEY_USE_LOCALFILE, false);
			
			String fname = mySharedPreferences.getString(KEY_FILENAME, BloodPreasure.DEFAULT_FILENAME() );
			String subFolder = "";
			DataAccess d = null;
			if( useAzureFile )
			{
				d = DataAccessAndroid.createInstance( fname, 
						mySharedPreferences.getString(KEY_AZURE_ACCOUNT, DataAccessAzureAndroidController.DEFAULT_ACCOUNT),
						mySharedPreferences.getString(KEY_AZURE_KEY, "" ),
						mySharedPreferences.getString(KEY_AZURE_CONTAINER, DataAccessAzureAndroidController.DEFAULT_CONTAINER ) );
			}
			else
			{
				if( useFTPServer )
				{
					fname = mySharedPreferences.getString(KEY_FTPFILENAME, BloodPreasure.DEFAULT_FTP_FILENAME());
					subFolder = mySharedPreferences.getString(KEY_SUBFOLDER, BloodPreasure.DEFAULT_SUBFOLDER() );
	
					d = DataAccess.createInstance( fname, 
						mySharedPreferences.getString(KEY_USER, "" ), 
						mySharedPreferences.getString(KEY_PWD, "" ), 
						mySharedPreferences.getString(KEY_LINK, "" ), 
						subFolder, //subpath, 
						Integer.parseInt(mySharedPreferences.getString(KEY_FTPPORT, "" )), 
						mySharedPreferences.getString(KEY_CLOUD_KEY, "" ), 
						mySharedPreferences.getString(KEY_CLOUD_SEC, "" ), 
						mySharedPreferences.getString(KEY_CLOUD_BUCKET, "" )
						); 
				}
				else //if( useLocalFile )
				{
					d = new DataAccess(new DataAccessController(fname), DataAccess.eAccessType.LocalFile);
				}
				
			}
			MainActivityGrid.self.setDataAccessObject( d );
			BloodPreasurePreferenceActivity.setBloodPreasureAnalyzeData(mySharedPreferences);
			
		}
	}

	
//	public static Calendar getBirthday() {
//		Calendar cal = Calendar.getInstance();
//		DatePreference p = (DatePreference)new MyPreferenceFragment().getPreferenceManager().findPreference(KEY_BIRTHDAY);
//		if( p == null )
//		{
//			BloodPreasureAnalyze.setBirthday( (String)null );
//			cal = BloodPreasureAnalyze.getBirthday();
//		}
//		else
//			cal = p.getDate();
//		return cal;
//	}
//	private static void setBirthday(String value) {
//		DatePreference p = (DatePreference)new MyPreferenceFragment().getPreferenceManager().findPreference(KEY_BIRTHDAY);
//		p.setDate(value, TimeTool.TEMPLATE_DATE);
//		SharedPreferences.Editor editor = p.getEditor();
//		editor.putString(KEY_BIRTHDAY, value);
//	}

	public static void initAnalyzeOnScreen()
	{
		setBloodPreasureAnalyzeData(null);
		//SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		
	}
	public static void setBloodPreasureAnalyzeData( SharedPreferences mySharedPreferences) {
		if( mySharedPreferences == null )
			mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		
		try
		{
			BloodPreasureAnalyze.SHOW_ANALYZE_ON_MAINSCREEN = mySharedPreferences.getBoolean(KEY_SHOW_ANALYZE, BloodPreasureAnalyze.SHOW_ANALYZE_ON_MAINSCREEN);
		}
		catch(Exception ex)
		{
			Editor edit = mySharedPreferences.edit();
			edit.putBoolean(KEY_SHOW_ANALYZE, true);
			edit.apply();
			edit.commit();
			BloodPreasureAnalyze.SHOW_ANALYZE_ON_MAINSCREEN = true;
		}
		BloodPreasureAnalyze.setRoundQuarters( mySharedPreferences.getBoolean(KEY_ROUND_QUARTERS, BloodPreasureAnalyze.getRoundQuarters()) );
		
		BloodPreasureAnalyze.setBirthday( mySharedPreferences.getString(KEY_BIRTHDAY, BloodPreasureAnalyze.getBirthdayString() ) );
		
		BloodPreasureAnalyze.setGewicht(Float.parseFloat(mySharedPreferences.getString(KEY_GEWICHT, ""+BloodPreasureAnalyze.getGewicht() )) );
		BloodPreasureAnalyze.setGroesse(Integer.parseInt(mySharedPreferences.getString(KEY_GROESSE, ""+BloodPreasureAnalyze.getGroesse() )) );
		BloodPreasureAnalyze.setTaile(Integer.parseInt(mySharedPreferences.getString(KEY_TAILE, ""+BloodPreasureAnalyze.getTaile() )) );
		BloodPreasureAnalyze.setHuefte(Integer.parseInt(mySharedPreferences.getString(KEY_HUEFTE, ""+BloodPreasureAnalyze.getHuefte() )) );
		
		BloodPreasureAnalyze.setMedication( mySharedPreferences.getString(KEY_MEDICATION, BloodPreasureAnalyze.getMedication()) );
		BloodPreasureAnalyze.setGeschlecht( mySharedPreferences.getString(KEY_GESCHLECHT, BloodPreasureAnalyze.getGeschlecht()) );
		
		ChartFragment.setSystolischUsed( mySharedPreferences.getBoolean( KEY_PRINT_CHART_SYSTOLISCH, ChartFragment.isSystolischUsed()) );
		ChartFragment.setDiastolischUsed( mySharedPreferences.getBoolean( KEY_PRINT_CHART_DIASTOLISCH, ChartFragment.isDiastolischUsed()) );
		ChartFragment.setPulsUsed( mySharedPreferences.getBoolean( KEY_PRINT_CHART_PULS, ChartFragment.isPulsUsed()) );
		ChartFragment.setGewichtUsed( mySharedPreferences.getBoolean( KEY_PRINT_CHART_GEWICHT, ChartFragment.isGewichtUsed()) );
		ChartFragment.setZuckerUsed( mySharedPreferences.getBoolean( KEY_PRINT_CHART_ZUCKER, ChartFragment.isZuckerUsed()) );
		ChartFragment.setTempUsed( mySharedPreferences.getBoolean( KEY_PRINT_CHART_TEMP, ChartFragment.isTemperturUsed()) );
	}

	public void exportKeyValuePairs(KeyValuePairPrefs kvp) {
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		if( isConfigured(mySharedPreferences)  ) 
		{
			this.preferenceFragment.exportKeyValuePairs(kvp);
			
		}
	}

	public void importKeyValuePairs(KeyValuePairPrefs kvp) 
	{
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		Editor edit = mySharedPreferences.edit();
		
        PreferenceManager pm = null;

		for( IElement e : kvp.getValues() )
		{
			KeyValuePairPrefs.KeyValue kv = (KeyValuePairPrefs.KeyValue)e;
			if( kv.getKey().equals(KEY_SHOW_ANALYZE) )
				edit.putBoolean(KEY_SHOW_ANALYZE, Boolean.getBoolean(kv.getValue()));
			else if( kv.getKey().equals(KEY_ROUND_QUARTERS) )
				edit.putBoolean(KEY_ROUND_QUARTERS, Boolean.getBoolean(kv.getValue()));
			else if( kv.getKey().equals(KEY_TAGS) )
			{
				ArrayList<String> tagListToSave = convertToArray(kv.getValue());
				BloodPreasurePreferenceActivity.saveTags( tagListToSave  );
			}
			else if( kv.getKey().equals(KEY_PWD) )
			{
				String v = kv.getValue(); 
				if( v.startsWith("§") )
				{
					PasswordEncryptionDecryption d = new PasswordEncryptionDecryption( v.substring(1), true);
					try {
						v = d.decrypt();
					} catch (Exception e1) {
					}
				}
				edit.putString(kv.getKey(), v);
			}
			else
			{
				Object o = kv.getValue();
				if( o instanceof String)
					edit.putString(kv.getKey(), kv.getValue());
				else if( o instanceof Boolean)
					edit.putBoolean(kv.getKey(), (Boolean)o);
				else if( o instanceof Integer)
					edit.putInt( kv.getKey(), (Integer)o);
				else if( o instanceof Float)
					edit.putFloat( kv.getKey(), (Float)o);
			}
			
			if( !kv.getKey().equals(KEY_TAGS) )
				pm = preferenceFragment.updateTextFields( pm, kv.getKey(), kv.getValue().toString() );
		}
		edit.apply();
		edit.commit();
		
		preferenceFragment.doScreen(preferenceFragment.getPreferenceScreen());
		
		BloodPreasurePreferenceActivity.initAnalyzeOnScreen();
		BloodPreasurePreferenceActivity.initFileAccessData();
	}

	public static int getTensovalPersonIndex() {
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		String s = mySharedPreferences.getString(KEY_TENSOVAL_PERSON, "1");
		
		return Integer.parseInt(s);
	}

	public static void saveSettingsExportImportFileName( ExportImportData data, String suffixKey, String defaultName ) 
	{
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		Editor edit = mySharedPreferences.edit();
		String lf = data.getLocalFile();

		if( lf != null && !lf.isEmpty() )
			edit.putString(suffixKey+eFileType.LocalFile.name(), lf );

		lf = data.getFile(eFileType.Url);
		if( lf != null && !lf.isEmpty() )
			edit.putString(suffixKey+eFileType.Url.name(), lf );
		
		edit.apply();
		edit.commit();
	}

	public static void getSettingsExportImportFileName(ExportImportData eximp) {
		// 		eximp.setLocalFile();
		//BloodPreasurePreferenceActivity.getSettingsExportImportFileName( BloodPreasure.DEFAULT_BACKUPFILE, BackupData.class.getSimpleName() ), 
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		
		String s = mySharedPreferences.getString(eximp.getClass().getSimpleName() + eFileType.LocalFile.name(), null);
		if( s != null && !s.isEmpty() )
			eximp.setLocalFile(s);
		s = mySharedPreferences.getString(eximp.getClass().getSimpleName() + eFileType.Url.name(), null);
		if( s != null && !s.isEmpty() )
			eximp.setUrlFile(s);
		
	}

	public static void getPrintConfig(PrintConfig printConfig) {
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		
		printConfig.setPrintBloodPreasureAnalyze( mySharedPreferences.getBoolean(KEY_PRINTCONFIG_ANALYZE, printConfig.isPrintBloodPreasureAnalyze() ));
		printConfig.setPrintBloodPreasureChart( mySharedPreferences.getBoolean(KEY_PRINTCONFIG_CHART, printConfig.isPrintBloodPreasureChart() ));
		printConfig.setPrintBloodPreasureList( mySharedPreferences.getBoolean(KEY_PRINTCONFIG_LIST, printConfig.isPrintBloodPreasureList() ));
		printConfig.setPrintBloodPreasureListDetals( mySharedPreferences.getBoolean(KEY_PRINTCONFIG_LISTDETAIL, printConfig.isPrintBloodPreasureListDetails() ));
		
//		boolean b = printConfig.isPrintBloodPreasureAnalyze();
//		b = printConfig.isPrintBloodPreasureChart();
//		b = printConfig.isPrintBloodPreasureList();
//		b = printConfig.isPrintBloodPreasureListDetails();
		
	}
	public static void setPrintConfig(PrintConfig printConfig) {
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		
//		boolean b = printConfig.isPrintBloodPreasureAnalyze();
//		b = printConfig.isPrintBloodPreasureChart();
//		b = printConfig.isPrintBloodPreasureList();
//		b = printConfig.isPrintBloodPreasureListDetails();
		
		Editor editor = mySharedPreferences.edit();
		editor.putBoolean( KEY_PRINTCONFIG_ANALYZE, printConfig.isPrintBloodPreasureAnalyze() );
		editor.putBoolean(  KEY_PRINTCONFIG_CHART, printConfig.isPrintBloodPreasureChart() );
		editor.putBoolean( KEY_PRINTCONFIG_LIST, printConfig.isPrintBloodPreasureList() );
		editor.putBoolean( KEY_PRINTCONFIG_LISTDETAIL, printConfig.isPrintBloodPreasureListDetails() );
		
		editor.apply();
		editor.commit();
	}

	public static void setMarkTagsAsUntilRevoke(boolean markTagsAsUntilRevoke) {
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		Editor editor = mySharedPreferences.edit();
		editor.putBoolean( KEY_MARKTAGSAS_UNTILREVOKE, markTagsAsUntilRevoke );
		editor.apply();
		editor.commit();
	}

	public static boolean isMarkTagsAsUntilRevoke() {
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
		boolean ret = mySharedPreferences.getBoolean(KEY_MARKTAGSAS_UNTILREVOKE, false ); 
		return ret;
	}
	
}
