package at.gepa.bloodpreasure.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyze;
import at.gepa.bloodpreasure.pref.BloodPreasurePreferenceActivity;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.lib.tools2.TimeTool;
import at.gepa.lib.tools2.Util;
import at.gepa.listener.IChangeListener;


public class BloodPreasureEditActivity_SinglePage extends Activity
implements IChangeListener
{
	private BloodPreasure dataValue;
	private EditText txTags;
	private boolean changed;

	public boolean isChanged() {
		return changed;
	}
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	@Override
	protected void onCreate(Bundle param) {
		super.onCreate(param);
		setContentView(R.layout.activity_blood_preasure_edit);
		
		dataValue = MainActivityGrid.self.getCurrentEditable();
		if( dataValue == null )
			dataValue = MainActivityGrid.self.newBloodPreasure();
		
		EditText txDatum = (EditText)findViewById(R.id.txDatum);
		txDatum.setText( dataValue.getDateString() );
		
		EditText txUhrzeit = (EditText)findViewById(R.id.txUhrzeit);
		txUhrzeit.setText( dataValue.getTimeString() );
		
		EditText txSyst = (EditText)findViewById(R.id.txSystolisch);
		TextWatcher tw = new TextWatcher() {

	          public void afterTextChanged(Editable s) {
	        	  analyzeValues();
	          }
	          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	          public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	  analyzeValues();
	          }
	       };
		txSyst.addTextChangedListener(tw);
		if( dataValue.getSystolisch() > 0 )
			txSyst.setText( dataValue.getSystolisch().toString() );
		txSyst.requestFocus();
		
		EditText txDiast = (EditText)findViewById(R.id.txDiastolisch);
		txDiast.addTextChangedListener(tw);
		if( dataValue.getDiastolisch() > 0 )
			txDiast.setText( dataValue.getDiastolisch().toString() );
		
		EditText txPuls = (EditText)findViewById(R.id.txPuls);
		if( dataValue.getPuls() > 0 )
			txPuls.setText( dataValue.getPuls().toString() );
		
		EditText txDescription = (EditText)findViewById(R.id.txDescription);
		txDescription.setText( dataValue.getDescription() );
		
		final BloodPreasureEditActivity_SinglePage bpa = this;
		txTags = (EditText)findViewById(R.id.txTags);

		TagListActivity.setChangeListener(this);
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(bpa, TagListActivity.class);
				
				Bundle b = new Bundle();
				b.putString(TagListActivity.KEY, txTags.getText().toString());
				i.putExtras(b);
				startActivityForResult(i, TagListActivity.ACTIVITY_REQUEST_CODE );
			}
		};
		txTags.setKeyListener(null);
		
		txTags.setText( dataValue.getTags() );
		View label_StartTagActivity = (View)findViewById(R.id.label_StartTagActivity);
		label_StartTagActivity.setOnClickListener(listener);
		txTags.setOnClickListener(listener);
		
		setChanged(false);
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if( requestCode == TagListActivity.ACTIVITY_REQUEST_CODE )
		{
			if (resultCode == RESULT_OK)
			{
				String seltags = data.getStringExtra(TagListActivity.KEY);
				txTags.setText(seltags);
				setChanged(true);
			}
		}
	}

	
	private void analyzeValues()
	{
		EditText txSyst = (EditText)findViewById(R.id.txSystolisch);
		EditText txDiast = (EditText)findViewById(R.id.txDiastolisch);
		int syst = Util.toInt( txSyst.getText().toString() );
		int diast = Util.toInt( txDiast.getText().toString() );
		
		BloodPreasureAnalyze ba = new BloodPreasureAnalyze(syst, diast);
		
		TextView tv = (TextView)findViewById(R.id.label_bloodpreasureAnalyze);
		tv.setText(ba.analyzeText());
		tv.setTextColor(ba.analyzeColor());
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_apply) {
			onSave( null );
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onSave(View v)
	{
		EditText txDatum = (EditText)findViewById(R.id.txDatum);
		EditText txUhrzeit = (EditText)findViewById(R.id.txUhrzeit);
		EditText txSyst = (EditText)findViewById(R.id.txSystolisch);
		EditText txDiast = (EditText)findViewById(R.id.txDiastolisch);
		EditText txPuls = (EditText)findViewById(R.id.txPuls);
		EditText txDescription = (EditText)findViewById(R.id.txDescription);
		
		try {
			dataValue.setDate(TimeTool.toDateTime( txDatum.getText().toString(), txUhrzeit.getText().toString()) );
			dataValue.setSystolisch( at.gepa.lib.tools2.Util.toInt( txSyst.getText().toString() ) );
			dataValue.setDiastolisch( at.gepa.lib.tools2.Util.toInt( txDiast.getText().toString() ) );
			dataValue.setPuls( at.gepa.lib.tools2.Util.toInt( txPuls.getText().toString() ) );
			dataValue.setDescription( txDescription.getText().toString() );
			dataValue.setTags( txTags.getText().toString() );

			MainActivityGrid.self.save( dataValue, false );
			
			if( isChanged() && tagListToSave != null )
				BloodPreasurePreferenceActivity.saveTags( tagListToSave );
				
		} catch (Exception e) {
			
			e.printStackTrace();
			Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
			toast.show();
			return;
		}
		super.finish();
	}
	


	private ArrayList<String> tagListToSave = null;
	@Override
	public void saveTags(ArrayList<String> convertToTagStringArray) {
		tagListToSave = convertToTagStringArray;
		setChanged(true);
	}
	@Override
	public ArrayList<String> getTags() 
	{
		if( tagListToSave == null )
			tagListToSave = BloodPreasurePreferenceActivity.getTags();
		return tagListToSave;
	}
	
	
}
