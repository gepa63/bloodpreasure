package at.gepa.hellochart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PathEffect;
import android.graphics.Typeface;
import at.gepa.bloodpreasure.ChartFragment;
import at.gepa.bloodpreasure.IRefreshListener;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.lib.model.BloodPreasure;


public class BloodPreasureLineHelloChartView extends LineChartView implements IRefreshListener {

	public BloodPreasureLineHelloChartView(Context context) {
		super(context);
	}

	public void dataRefreshFrom(Serializable serializable) {
	
		this.fillData();
	}

	public ArrayList<Serializable> createDataList() {
		ArrayList<Serializable> globallist = new ArrayList<Serializable>();
		
		return globallist;
	}

	public void fillData() {
		if( getLineChartData() != null )
		{
			setLineChartData(null);
		}
		
		List<List<PointValue>> listOfList = new ArrayList<List<PointValue>>();
		for( int i=0; i < ChartFragment.PRINT_CHART_CONFIG; i++ )
			listOfList.add(new ArrayList<PointValue>());
//		List<PointValue> sysvalues = new ArrayList<PointValue>();
//		List<PointValue> diastvalues = new ArrayList<PointValue>();
//		List<PointValue> pulsvalues = new ArrayList<PointValue>();
	    List<AxisValue> xvalues = new ArrayList<AxisValue>();

	    int index = 0;
	    Date fi = ChartFragment.getFilterInstance();
	    for( int i= MainActivityGrid.self.bloodPreasureAdapter.getCount()-1; i >= 0; i-- )
	    {
	    	BloodPreasure bp = MainActivityGrid.self.bloodPreasureAdapter.getItem(i);
	    	
	    	if( bp.getDateTime().after( fi ) )
	    	{	    	
		    	AxisValue av = new AxisValue(index);
		    	String s = bp.getDateString() + " " + bp.getTimeString();
		    	av.setLabel( s );
		    	//Log.i(getClass().getSimpleName(), s);
		    	
				//av.setLabel( TimeTool.toShortDateTimeString( bp.getDate() ));
		    	
		    	xvalues.add( av );
		    	
		    	if( ChartFragment.isSystolischUsed() )
		    		listOfList.get(ChartFragment.PRINT_CHART_INDEX_SYS).add(new PointValue(index, bp.getSystolisch() ));
		    	if( ChartFragment.isDiastolischUsed() )
		    		listOfList.get(ChartFragment.PRINT_CHART_INDEX_DIA).add(new PointValue(index, bp.getDiastolisch() ));
		    	if( ChartFragment.isPulsUsed() )
		    		listOfList.get(ChartFragment.PRINT_CHART_INDEX_PULS).add(new PointValue(index, bp.getPuls() ));
		    	if( ChartFragment.isGewichtUsed() )
		    		listOfList.get(ChartFragment.PRINT_CHART_INDEX_GEWICHT).add(new PointValue(index, bp.getGewicht() ));
		    	if( ChartFragment.isZuckerUsed() )
		    		listOfList.get(ChartFragment.PRINT_CHART_INDEX_ZUCKER).add( new PointValue(index, bp.getZucker()) );
		    	if( ChartFragment.isTemperturUsed())
		    		listOfList.get(ChartFragment.PRINT_CHART_INDEX_TEMP).add( new PointValue(index, bp.getTemp()) );
		    	index++;
	    	}
	    }
	    //damit der Letzte angezeigt wird, scheint so, dass das nicht der Fall ist
	    {
	    	AxisValue av = new AxisValue(index);
	    	av.setLabel( at.gepa.lib.tools.time.TimeTool.toDateTimeString(Calendar.getInstance().getTime()) );
		    xvalues.add( av );
	    }
	    
	    List<Line> lines = new ArrayList<Line>();
	    
	    //In most cased you can call data model methods in builder-pattern-like manner.
	    for( int i=0; i < ChartFragment.PRINT_CHART_CONFIG; i++ )
	    {
	    	if( listOfList.get(i).size() > 0 )
	    	{
		    	Line line = new Line(listOfList.get(i));
			    configLine( line, i);
			    lines.add(line);
	    	}
	    }
	    LineChartData data = new LineChartData();
	    data.setLines(lines);
	    
//	    Line sysline = new Line(sysvalues);
//	    Line dialine = new Line(diastvalues);
//	    Line pulsline = new Line(pulsvalues);
//	    configLine( sysline, 0);
//	    configLine( dialine, 1);
//	    configLine( pulsline, 2);
	    
//	    List<Line> lines = new ArrayList<Line>();
//	    lines.add(sysline);
//	    lines.add(dialine);
//	    lines.add(pulsline);

		Axis axisX = new Axis(xvalues);
		axisX.setHasLines(true);
		axisX.setInside(false);
		axisX.setHasSeparationLine(true);
		axisX.setHasTiltedLabels(true);
		axisX.setTextSize(9);
		axisX.setTextColor(Color.BLACK);
		
		axisX.setMaxLabelChars(16);
		
		Typeface tf = Typeface.defaultFromStyle(Typeface.BOLD);
		axisX.setTypeface( tf );
		
		data.setAxisXBottom(axisX);

	    setLineChartData(data);
	    
	    dozoom(7f);
	}

	
	public void dozoom(float zoomFactor) {
		Viewport maxViewport = getMaximumViewport(); 
		float dx = 0f;
		float dy = 0;
		if( !maxViewport.contains(dx, dy) )
			dx = maxViewport.right-2;
		if( !maxViewport.contains(dx, dy) )
			dy = maxViewport.centerY();
		//float zoomFactor = 7f;
		setZoomLevel( dx, dy, zoomFactor );
	}

	private void configLine(Line line, int index) 
	{
		int color = getLineColor(index);
		boolean cubic = getCubic(index);
		
		line.setColor(color).setCubic(cubic);

		configLineLables( line, true, 3, false, -1 );
	}

	private boolean getCubic(int index) {
		boolean b = true;
		switch(index)
		{
		case 0: //Systolsich
			b = true;
			break;
		case 1: //Diastolisch
			b = true;
			break;
		case 2: //Puls
			b = false;
			break;
		case 3: //Gewicht
			b = false;
			break;
		case 4: //Zucker
			b = false;
			break;
		case 5: //Temp
			b = false;
			break;
		}
		return b;
	}

	public static int getLineColor(int index) {
		int color = Color.BLACK;
		switch(index)
		{
		case 0: //Systolsich
			color = Color.BLUE;
			break;
		case 1: //Diastolisch
			color = Color.YELLOW;
			break;
		case 2: //Puls
			color = Color.LTGRAY;
			break;
		case 3: //Gewicht
			color = Color.GREEN;
			break;
		case 4: //Zucker
			color = Color.CYAN;
			break;
		case 5: //Temp
			color = Color.MAGENTA;
			break;
		}
		return color;
	}

	private void configLineLables(Line line, boolean hasLables, int radius, boolean filled, int width) {
		line.setHasLabels(hasLables);
		line.setHasPoints(hasLables);
		line.setFilled(filled);
		//line.setHasLabelsOnlyForSelected(true);
		line.setHasLines(true);
		line.setPointRadius(radius);
		line.setPathEffect(new PathEffect() );
		if( width > 0 )
			line.setStrokeWidth(width);
	}

	@Override
	public void dataRefresh() {
		fillData();
	}

	public void configPrint(boolean do4Print) {
		for( Line line : getLineChartData().getLines() )
		{
			if( do4Print )
				configLineLables( line, false, 0, false, 1 );
			else
				configLineLables( line, true, 3, false, -1 );
		}
	}

	public void refresh() {
		fillData();
	}
}
