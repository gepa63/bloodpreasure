package at.gepa.bloodpreasure.analyze;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.ui.BloodPreasureInfoAdapter;
import at.gepa.model.BloodPreasureInfo;

public class BloodPreasureAnalyzeActivity extends Activity {

	public BloodPreasureAnalyzeActivity() {
	}

	@Override
	protected void onCreate(Bundle param) {
		super.onCreate(param);
		setContentView(R.layout.bloodpreasureinfo_activity);
		
		ListView listView = (ListView) findViewById(R.id.listBloodPreasureInfoGridId);
		
		BloodPreasureInfoAdapter adapter = new BloodPreasureInfoAdapter(MainActivityGrid.self, R.layout.bloodpreasureinfo_row);
		listView.setAdapter( adapter );
		
		for( BloodPreasureInfo bpi : BloodPreasureAnalyzeEngine.createList() )
			adapter.add( bpi );			
		
	}
	
}
