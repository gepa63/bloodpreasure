package at.gepa.bloodpreasure.pref;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.files.CacheFile;
import at.gepa.files.LocalFileAccess;
import at.gepa.lib.model.BloodPreasure;

public class FileChoosePreference extends Preference {

	private EditText prefFilename;
	private CharSequence lastValueTextWhenNull;
	private TextView textTitle;
	private LinearLayout verticalView;
	protected long mTextLostFocusTimestamp;

	public FileChoosePreference(Context context) {
		super(context);
		mTextLostFocusTimestamp = -1;
	}
	public FileChoosePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTextLostFocusTimestamp = -1;
	}
	public FileChoosePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mTextLostFocusTimestamp = -1;
	}

	private void reclaimFocus(View v, long timestamp) {
        if (timestamp == -1)
            return;
        if ((System.currentTimeMillis() - timestamp) < 250)
            v.requestFocus();
    }
	
	public View getView(View convertView, ViewGroup parent) {
        if (convertView == null)
        	convertView = verticalView;

        if (convertView == null) 
        {
        	convertView = new LinearLayout(getContext());
        	verticalView = (LinearLayout)convertView;
        	verticalView.setFocusable(true);
        	verticalView.setClickable(true);
        	verticalView.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if( hasFocus )
					{
						prefFilename.requestFocus();						
					}
				}
			});
        	
        	convertView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        	((LinearLayout) convertView).setOrientation(LinearLayout.VERTICAL);

            textTitle = new TextView(getContext());
            textTitle.setTag(convertView);
            textTitle.setText("Dateiname");
            textTitle.setTextSize(13f);
            ((LinearLayout) convertView).addView(textTitle,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        	
        }
        if( prefFilename == null )
        {
        	LinearLayout hconvertView = new LinearLayout(getContext());
        	((LinearLayout)hconvertView).setOrientation(LinearLayout.HORIZONTAL);
        	
            prefFilename = new EditText(getContext());
            
            prefFilename.setSelectAllOnFocus(true);
            prefFilename.setOnFocusChangeListener(new OnFocusChangeListener() {          

                public void onFocusChange(View v, boolean hasFocus) {
                	
                	if ((v == prefFilename) && !hasFocus)
                        mTextLostFocusTimestamp = System.currentTimeMillis();                	
                }
            });
            
            if( lastValueTextWhenNull == null )
            	prefFilename.setText("");
            else
            {
            	if( hasValueinText(lastValueTextWhenNull.toString()) )
    				setText(lastValueTextWhenNull.toString().split(": "));
    			else
    				prefFilename.setText(lastValueTextWhenNull);
            	
            	lastValueTextWhenNull = null;
            }
            ((LinearLayout) hconvertView).addView(prefFilename,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            Button btn = new Button(getContext());
            btn.setText("...");
            ((LinearLayout) hconvertView).addView(btn);
            btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startFileChooser();
				}
			});

            ((LinearLayout) convertView).addView(hconvertView,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            
            LinearLayout hconvertViewRow2 = addButton_CacheAsLocalFile();
            if( hconvertViewRow2 != null )
            	((LinearLayout) convertView).addView(hconvertViewRow2,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            LinearLayout hconvertViewRow3 = addButton_CacheAsFtpFile();
            if( hconvertViewRow3 != null )
            	((LinearLayout) convertView).addView(hconvertViewRow3,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        }
        
        reclaimFocus(prefFilename, mTextLostFocusTimestamp);

        return convertView;
    }
	
	private LinearLayout addButton_CacheAsFtpFile() {
    	LinearLayout hconvertViewRow2 = new LinearLayout(getContext());
    	((LinearLayout)hconvertViewRow2).setOrientation(LinearLayout.HORIZONTAL);
        Button btn = new Button(getContext());
        btn.setText("Aktuellen Cache zu Ftp-Datei hochladen");
        
		CacheFile cf = new CacheFile(MainActivityGrid.self);
		btn.setEnabled( cf.exists() );
        
        ((LinearLayout) hconvertViewRow2).addView(btn);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				CacheFile cf = new CacheFile(MainActivityGrid.self);
				if( cf.containsData() )
					try {
						MainActivityGrid.getDataAccess().copyFrom(cf.getFile());
					} catch (Exception e) {
						Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
					}
			}
		});
		return hconvertViewRow2;
	}
	private LinearLayout addButton_CacheAsLocalFile() {
    	LinearLayout hconvertViewRow2 = new LinearLayout(getContext());
    	((LinearLayout)hconvertViewRow2).setOrientation(LinearLayout.HORIZONTAL);
        Button btn = new Button(getContext());
        btn.setText("Aktuellen Cache zu Datei kopieren");
        
		CacheFile cf = new CacheFile(MainActivityGrid.self);
		btn.setEnabled( cf.exists() );
        
        ((LinearLayout) hconvertViewRow2).addView(btn);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String currentFile = prefFilename.getText().toString();
				
				java.io.File f = new java.io.File( currentFile );
				if( f.exists() )
				{
					java.io.File newPath = new java.io.File(currentFile+".backup");
					f.renameTo(newPath);
				}
				CacheFile cf = new CacheFile(MainActivityGrid.self);
				try {
					int lines = cf.copyTo(currentFile);
					Toast.makeText(getContext(), lines + " Cachezeilen erfolgreich nach '"+currentFile+"' kopiert", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
		return hconvertViewRow2;
	}
	private boolean hasValueinText(String s) {
		if( !s.toString().contains(": ") ) return false;
		String sa [] = s.split(": ");
		return sa.length >= 2;
	}
	public void startFileChooser(){
		
		at.gepa.androidlib.ui.SimpleFileDialog FileSaveDialog =  new at.gepa.androidlib.ui.SimpleFileDialog(getContext(), "FileSave", new at.gepa.androidlib.ui.SimpleFileDialog.SimpleFileDialogListener()
		{
			@Override
			public void onChosenDir(String chosenDir) 
			{
				try {
					prefFilename.setText(chosenDir);
				} catch (Exception e) {
					Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
				
			}
		}, true);
		String df = prefFilename.getText().toString();
		if( df.isEmpty() )
			df = BloodPreasure.DEFAULT_FILENAME();
		FileSaveDialog.setDefault_File_Name( df );
		FileSaveDialog.chooseFile_or_Dir();
	}
	
	public void setText(CharSequence c)
	{
		if( prefFilename != null )
		{
			prefFilename.setText(c);
			lastValueTextWhenNull = null;
		}
		else
			lastValueTextWhenNull = c;
	}
	
	@Override
	public void setTitle( CharSequence c)
	{
		if( textTitle != null )
		{
			if( hasValueInText(c.toString()) )
				setText(c.toString().split(": "));
		}
		else
			lastValueTextWhenNull = c;
	}
	
	private boolean hasValueInText(String s) {
		if( s.contains(": ") ) return false;
		String sa[] = s.split(": ");	
		return (sa.length >= 2);
	}
	private void setText(String[] sa) {
		textTitle.setText(sa[0].trim());
		prefFilename.setText( sa[1].trim() );
	}
	@Override
	public void setDefaultValue(Object defaultValue)
	{
		if( defaultValue == null )
			defaultValue = "";
		setText(defaultValue.toString());
		super.setDefaultValue(defaultValue);
	}
	public String getFileName() {
		if( prefFilename == null )
			return null;
		if( prefFilename.getText() == null )
			return null;
		return prefFilename.getText().toString();
	}
	
}
