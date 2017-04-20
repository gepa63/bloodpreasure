package at.gepa.model;

import java.util.ArrayList;
import java.util.Date;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import at.gepa.bloodpreasure.ChartFragment;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyze;
import at.gepa.bloodpreasure.analyze.BloodPreasureTendenz;
import at.gepa.lib.model.BloodPreasure;


public class AvgValueHolder {

	private View layoutAvg;
	private TextView avgValue;
	private TextView avgValueDay;
	private TextView avgValueNight;
	private TextView tendenz;
	private BloodPreasureTendenz tendenzy;
	private ImageView tendenz_image;
	
	private int avgSyst;
	public TextView getAvgValue() {
		return avgValue;
	}

	public void setAvgValue(TextView avgValue) {
		this.avgValue = avgValue;
	}

	public int getAvgSyst() {
		return avgSyst;
	}

	public void setAvgSyst(int avgSyst) {
		this.avgSyst = avgSyst;
	}

	public int getAvgDiast() {
		return avgDiast;
	}

	public void setAvgDiast(int avgDiast) {
		this.avgDiast = avgDiast;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	private int avgDiast;
	private int avgSystNight;
	private int avgSystDay;
	private int avgDiastNight;
	private int avgDiastDay;
	private int cnt;
	private int cntNight;
	private int cntDay;
	private int avgPuls;
	private View img_avg_night;
	private View img_avg_day;

	public int getAvgPuls() {
		return avgPuls;
	}

	public void setViews(View layoutAvg, View avgValue, View avgValueDay, View avgValueNight, View tendenz) 
	{
		this.layoutAvg = layoutAvg;
		this.avgValue = (TextView)avgValue;
		this.avgValueDay = (TextView)avgValueDay;
		this.avgValueNight = (TextView)avgValueNight;
		this.tendenz = (TextView)tendenz;
		resetTendenz();
	}
	
	public void calculate()
	{
		resetTendenz();
		
		avgPuls = 0;
		avgSyst = 0;
		avgDiast = 0;
		avgSystNight = 0;
		avgSystDay = 0;
		avgDiastNight = 0;
		avgDiastDay = 0;
		cnt = MainActivityGrid.self.bloodPreasureAdapter.getCount();
		int div = 0;
		cntNight = 0;
		cntDay = 0;
		
		Date fi = ChartFragment.getFilterInstance();
		
		boolean is1970 = false;
		if( at.gepa.lib.tools.time.TimeTool.toDateString(fi) == null || at.gepa.lib.tools.time.TimeTool.toDateString(fi).equals("01.01.1970" ) )
		{
			is1970 = true;
		}
		ArrayList<BloodPreasure> tendenzBase = new ArrayList<BloodPreasure>();
		for( int i=0; i < cnt; i++ )
		{
			BloodPreasure bp = MainActivityGrid.self.bloodPreasureAdapter.getItem(i);
			Date bpd = bp.getDateTime();
			//System.out.println( TimeTool.toDateTimeString(bpd) + " is after " + TimeTool.toDateTimeString(fi) + " = " + bpd.after( fi ) );
	    	if( !bpd.after( fi ) && !is1970 ) continue;
	    	
    		tendenzBase.add(bp);
    		avgPuls += bp.getPuls();
    		avgSyst += bp.getSystolisch();
    		avgDiast += bp.getDiastolisch();
    		
    		if( at.gepa.lib.tools.time.TimeTool.isNight(bp.getDateTime() ) )
    		{
    			avgDiastNight += bp.getDiastolisch();
    			avgSystNight += bp.getSystolisch();
    			cntNight++;
    		}
    		else
    		{
    			avgDiastDay += bp.getDiastolisch();
    			avgSystDay += bp.getSystolisch();
    			cntDay++;
    		}
    		div++;
		}
		if( div == 0)
			div++;
		if( cntNight == 0 )
			cntNight = 1;
		if( cntDay == 0 )
			cntDay = 1;
		
		avgPuls /= div;
		avgSyst /= div;
		avgDiast /= div;
		avgDiastDay /= cntDay;
		avgSystDay  /= cntDay;
		avgDiastNight  /= cntNight;
		avgSystNight /= cntNight;
		
		if( avgSyst == 0 || avgDiast == 0)
		{
			layoutAvg.setVisibility(View.GONE);
			return;
		}
		
		avgValue.setText(String.format("%d / %d", avgSyst, avgDiast));
		BloodPreasureAnalyze bpa = new BloodPreasureAnalyze(avgSyst, avgDiast);
		avgValue.setTextColor(bpa.analyzeColor());
		
		avgValueDay.setText(String.format(" %d / %d", avgSystDay, avgDiastDay));
		bpa = new BloodPreasureAnalyze(avgSystDay, avgDiastDay);
		avgValueDay.setTextColor(bpa.analyzeColor());
		
		avgValueNight.setText(String.format(" %d / %d", avgSystNight, avgDiastNight));
		bpa = new BloodPreasureAnalyze(avgSystNight, avgDiastNight);
		avgValueNight.setTextColor(bpa.analyzeColor());
		
		layoutAvg.setVisibility(View.VISIBLE);
		
		tendenzy.calculate(tendenzBase);
		tendenz.setText( tendenzy.getValue() );
		tendenz_image.setImageResource( tendenzy.getImage() );
		tendenz.setTextColor( tendenzy.analyzeColor() );
	}

	private void resetTendenz() {
		tendenzy = new BloodPreasureTendenz();
	}

	public void setVisible(boolean b) {
		int visibility = (b ? View.VISIBLE : View.GONE);
		layoutAvg.setVisibility(visibility);
		avgValue.setVisibility(visibility);
		avgValueDay.setVisibility(visibility);
		avgValueNight.setVisibility(visibility);
		tendenz.setVisibility(visibility);
		tendenz_image.setVisibility(visibility);
		img_avg_day.setVisibility(visibility);
		img_avg_night.setVisibility(visibility);
	}

	public void setImages(View tendenz_image, View img_avg_night, View img_avg_day) 
	{
		this.tendenz_image = (ImageView)tendenz_image;
		this.img_avg_night = img_avg_night;
		this.img_avg_day = img_avg_day;
	}

	public String getTendency() {
		return tendenzy.getValue();
	}

	public int getTendenzyColor() {
		return tendenzy.analyzeColor();
	}

}
