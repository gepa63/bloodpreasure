package at.gepa.bloodpreasure.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import at.gepa.androidlib.SystemInfo;
import at.gepa.bloodpreasure.ChartFragment;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyze;
import at.gepa.hellochart.BloodPreasureLineHelloChartView;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.model.BloodPreasureInfo;

public class BloodPreasureInfoActivity extends Activity {

	public BloodPreasureInfoActivity() {
		
	}
	
	public static String getContact()
	{
		return "h r . g r u m m l @ c h e l l o . a t";
	}
	public static String getContact2()
	{
		return getContact().replaceAll(" ", "");
	}
	
	@Override
	protected void onCreate(Bundle param) {
		super.onCreate(param);
		setContentView(R.layout.bloodpreasureinfo_activity);
		
		ListView listView = (ListView) findViewById(R.id.listBloodPreasureInfoGridId);
		
		BloodPreasureInfoAdapter adapter = new BloodPreasureInfoAdapter(MainActivityGrid.self, R.layout.bloodpreasureinfo_row);
		listView.setAdapter( adapter );
		
		adapter.add( createBloodPreasureInfo_Version( ) );
		adapter.add( createBloodPreasureInfo_Autor( ) );
		adapter.add( createBloodPreasureInfo_Contact( ) );
		adapter.add( new BloodPreasureInfo("","") );

		for( BloodPreasureInfo cc : BloodPreasureInfoActivity.getChartColorList() )
			adapter.add( cc );
		
		adapter.add( new BloodPreasureInfo("","") );
		
		for( BloodPreasureInfo bpi : BloodPreasureInfoActivity.getInfoList(null) )
			adapter.add( bpi );
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Intent intent = ShareCompat.IntentBuilder
                        .from(MainActivityGrid.self)
                        .setEmailTo(new String[]{getContact2()})
                        .setType("application/txt")
                        .setSubject("Zur App Blutdruck ...")
                        .setText("Ich möchte Ihnen folgendes mitteilen:\n\nVG")
                        .setChooserTitle("E-Mail senden mit ...")
                        .createChooserIntent()
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

				MainActivityGrid.self.startActivity(intent);					
			}
		});
	}

	private BloodPreasureInfo createBloodPreasureInfo_Contact() {
		return new BloodPreasureInfo("Kontakt", getContact(), true, 18f);
	}

	public static String getAuthor()
	{
		return "Gerhard Payer";
	}
	private BloodPreasureInfo createBloodPreasureInfo_Autor() {
		return new BloodPreasureInfo("Autor", getAuthor() + " (c)");
	}


	private BloodPreasureInfo createBloodPreasureInfo_Version() {
		return new BloodPreasureInfo("Version", SystemInfo.getVersion(this) );
	}	

	public static ArrayList<BloodPreasureInfo> getInfoList(String value)
	{
		ArrayList<BloodPreasureInfo> adapter = new ArrayList<BloodPreasureInfo>();
		if( value == null ) value = "Text-Analyse-Farbe";
		adapter.add( new BloodPreasureInfo(BloodPreasureAnalyze.TEXT_BLOODPREASURE_LOW, value, BloodPreasureAnalyze.COLOR_LOW_PREASURE, false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasureAnalyze.TEXT_BLOODPREASURE_OPTIMAL, value, BloodPreasureAnalyze.COLOR_OPTIMAL, false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasureAnalyze.TEXT_BLOODPREASURE_NORMAL, value, BloodPreasureAnalyze.COLOR_NORMAL, false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasureAnalyze.TEXT_BLOODPREASURE_PRAEH, value, BloodPreasureAnalyze.COLOR_PREHYPERTORNIE, false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasureAnalyze.TEXT_BLOODPREASURE_LIGHTH, value, BloodPreasureAnalyze.COLOR_HYPERTORNIE_LEICHT, false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasureAnalyze.TEXT_BLOODPREASURE_MIDDLEH, value, BloodPreasureAnalyze.COLOR_HYPERTORNIE_MITTEL, false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasureAnalyze.TEXT_BLOODPREASURE_HEAVYH, value, BloodPreasureAnalyze.COLOR_HYPERTORNIE_SCHWER, false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasureAnalyze.TEXT_BLOODPREASURE_ISOLIERT, value, BloodPreasureAnalyze.COLOR_ISOLIERTE_HYPERTORNIE, false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasureAnalyze.TEXT_BLOODPREASURE_FEHLER, value, BloodPreasureAnalyze.COLOR_ERROR, false, 16f) );
		
		return adapter;
	}
	public static ArrayList<BloodPreasureInfo> getChartColorList() {
		ArrayList<BloodPreasureInfo> adapter = new ArrayList<BloodPreasureInfo>();
		String value = "Chart-Farbe";
		adapter.add( new BloodPreasureInfo(BloodPreasure.COL_SYS, value, BloodPreasureLineHelloChartView.getLineColor( ChartFragment.PRINT_CHART_INDEX_SYS), false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasure.COL_DIA, value, BloodPreasureLineHelloChartView.getLineColor( ChartFragment.PRINT_CHART_INDEX_DIA), false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasure.COL_PULS, value, BloodPreasureLineHelloChartView.getLineColor( ChartFragment.PRINT_CHART_INDEX_PULS), false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasure.COL_GEWICHT, value, BloodPreasureLineHelloChartView.getLineColor( ChartFragment.PRINT_CHART_INDEX_GEWICHT), false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasure.COL_ZUCKER, value, BloodPreasureLineHelloChartView.getLineColor( ChartFragment.PRINT_CHART_INDEX_ZUCKER), false, 16f) );
		adapter.add( new BloodPreasureInfo(BloodPreasure.COL_TEMP, value, BloodPreasureLineHelloChartView.getLineColor( ChartFragment.PRINT_CHART_INDEX_TEMP), false, 16f) );
		return adapter;
	}
	
}
