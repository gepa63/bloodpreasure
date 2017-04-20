package at.gepa.bloodpreasure;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.gepa.bloodpreasure.pref.BloodPreasurePreferenceActivity;
import at.gepa.bloodpreasure.ui.BloodPreasureInfoActivity;
import at.gepa.hellochart.BloodPreasureLineHelloChartView;
import at.gepa.tools2.PDFExtension;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;


public class ChartFragment extends Fragment {
	
	public static final String DEFAULT_FILTER = "01.08.2015";
	
	
	public static final int PRINT_CHART_INDEX_SYS = 0;
	public static final int PRINT_CHART_INDEX_DIA = 1;
	public static final int PRINT_CHART_INDEX_PULS = 2;
	public static final int PRINT_CHART_INDEX_GEWICHT = 3;
	public static final int PRINT_CHART_INDEX_ZUCKER = 4;
	public static final int PRINT_CHART_INDEX_TEMP = 5;
	public static final boolean [] PRINT_CHART = new boolean[]{true, true, true, false, false, false};
	public static final int PRINT_CHART_CONFIG = 6;
	
	public static boolean isSystolischUsed() {
		return PRINT_CHART[PRINT_CHART_INDEX_SYS];
	}
	public static boolean isDiastolischUsed() {
		return PRINT_CHART[PRINT_CHART_INDEX_DIA];
	}
	public static boolean isPulsUsed() {
		return PRINT_CHART[PRINT_CHART_INDEX_PULS];
	}
	public static boolean isGewichtUsed() {
		return PRINT_CHART[PRINT_CHART_INDEX_GEWICHT];
	}
	public static boolean isZuckerUsed() {
		return PRINT_CHART[PRINT_CHART_INDEX_ZUCKER];
	}
	public static boolean isTemperturUsed() {
		return PRINT_CHART[PRINT_CHART_INDEX_TEMP];
	}
	public static void setSystolischUsed(boolean boolean1) {
		PRINT_CHART[PRINT_CHART_INDEX_SYS] = boolean1;
	}
	public static void setDiastolischUsed(boolean boolean1) {
		PRINT_CHART[PRINT_CHART_INDEX_DIA] = boolean1;
	}
	public static void setPulsUsed(boolean boolean1) {
		PRINT_CHART[PRINT_CHART_INDEX_PULS] = boolean1;
	}
	public static void setGewichtUsed(boolean boolean1) {
		PRINT_CHART[PRINT_CHART_INDEX_GEWICHT] = boolean1;
	}
	public static void setZuckerUsed(boolean boolean1) {
		PRINT_CHART[PRINT_CHART_INDEX_ZUCKER] = boolean1;
	}
	public static void setTempUsed(boolean boolean1) {
		PRINT_CHART[PRINT_CHART_INDEX_TEMP] = boolean1;
	}
	public void refresh() {
		chartView.refresh();		
	}
	
	
	public static final Date buildDefaultFilterDate()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 0, 1);
		return cal.getTime();
	}
	
	private static final String STATE_DATA = "CHART_DATA";
	private static Date CURRENT_DATE_FILTER = null;
	
	public static Date getFilterInstance()
	{
		initFilter();
		if( CURRENT_DATE_FILTER == null )
			CURRENT_DATE_FILTER = ChartFragment.buildDefaultFilterDate();
		return CURRENT_DATE_FILTER;
	}
	public static void initFilter()
	{
		if( CURRENT_DATE_FILTER == null )
		{
			SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityGrid.self);
			ChartFragment.setDateFilter(mySharedPreferences.getString(BloodPreasurePreferenceActivity.KEY_DATEFILTER, DEFAULT_FILTER));
		}

	}
	
	public static ChartFragment newInstance() {
		return new ChartFragment();
	}


	private BloodPreasureLineHelloChartView chartView;
	private SwipeRefreshLayout swipeRefreshLayoutChartId;

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate( R.layout.fragment_main_activity_chart, container, false);
		
		this.chartView = new BloodPreasureLineHelloChartView(MainActivityGrid.self);
		
		//RelativeLayout rl = (RelativeLayout)rootView.findViewById(R.id.chartLayout);
		//rl.addView( chartView );
		
		swipeRefreshLayoutChartId = (android.support.v4.widget.SwipeRefreshLayout)rootView.findViewById(R.id.SwipeRefreshLayoutChartId);
		swipeRefreshLayoutChartId.addView(chartView);
		swipeRefreshLayoutChartId.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
		     @Override
		     public void onRefresh() {
		         refresh();
		         swipeRefreshLayoutChartId.setRefreshing(false);
		     }
		});
		

		chartView.setScrollEnabled(true);
		chartView.setZoomEnabled(true);
		chartView.setZoomType(ZoomType.HORIZONTAL);
		
		chartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
		
		
		
        MainActivityGrid.self.addRegsiterDataRefreshListener(chartView);

		if( savedInstanceState != null )
		{
			chartView.dataRefreshFrom( savedInstanceState.getSerializable(ChartFragment.STATE_DATA) );
		}
		
		//chartView.zoom(1.4f,1.4f,new PointF(0,0));
        
		return rootView;
	}
	@Override
	public void onSaveInstanceState (Bundle savedInstanceState) {
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);

	    ArrayList<Serializable> list = chartView.createDataList();

	    savedInstanceState.putSerializable(ChartFragment.STATE_DATA, list);
	}	
	

	public static void setDateFilter(String sDate) {
		if( sDate.contains(".") )
		{
			if( sDate.indexOf(".") > 2 )
				CURRENT_DATE_FILTER = at.gepa.lib.tools.time.TimeTool.toDate(sDate, at.gepa.lib.tools.time.TimeTool.TEMPLATE_DATE_EN);
			else
				CURRENT_DATE_FILTER = at.gepa.lib.tools.time.TimeTool.toDate(sDate, at.gepa.lib.tools.time.TimeTool.TEMPLATE_DATE);
		}
		else if( sDate.contains("-") )
		{
			if( sDate.indexOf("-") > 2 )
				CURRENT_DATE_FILTER = at.gepa.lib.tools.time.TimeTool.toDate(sDate, at.gepa.lib.tools.time.TimeTool.TEMPLATE_DATE_US);
			else
				CURRENT_DATE_FILTER = at.gepa.lib.tools.time.TimeTool.toDate(sDate, at.gepa.lib.tools.time.TimeTool.TEMPLATE_DATE_US2);
		}
	}
	public static String getFilterDateStringUS() {
		return at.gepa.lib.tools.time.TimeTool.toDateStringUS( ChartFragment.getFilterInstance() );
	}
	public static String getFilterDateString() {
		return at.gepa.lib.tools.time.TimeTool.toDateString( ChartFragment.getFilterInstance() );
	}

	public String createPdfChartFile(int orientation) throws IOException {
		if( !MainActivityGrid.self.isChartVisible() ) return null;
		
		android.support.v4.widget.SwipeRefreshLayout view = (android.support.v4.widget.SwipeRefreshLayout)chartView.getParent();
		String pdfName = "BloodPreasureChart.pdf";
		File dir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS );
		if( !dir.exists() )
			dir.mkdirs();
		File outputFile = new File( dir, pdfName);
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		
		int w = (int)view.getWidth();
		int h = (int)view.getHeight();

		MainActivityGrid.self.activateOverlayElements(false);
		View parent = MainActivityGrid.self.getView();
		parent.setDrawingCacheEnabled(true);
		parent.setDrawingCacheBackgroundColor(Color.WHITE);
		parent.buildDrawingCache();
        
        Bitmap cachedBitmap = parent.getDrawingCache();
	
        Bitmap bm = cachedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        
        w = bm.getWidth();
        h = bm.getHeight();
	    
		PdfDocument document = new PdfDocument();
		PageInfo pageInfo = new PageInfo.Builder( w, h, 1).create();
		Page page = document.startPage(pageInfo);
		//view.draw(page.getCanvas());
		parent.draw( page.getCanvas() );
		document.finishPage(page);
		MainActivityGrid.self.activateOverlayElements(true);
		document.writeTo(outputStream);
		document.close();
		outputStream.close();
		return outputFile.getAbsolutePath();
	}
	
	public String createPdfChartFile_2() throws Exception
	{
		String pdfName = "BloodPreasureChart.pdf";
		File dir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS );
		if( !dir.exists() )
			dir.mkdirs();
		File outputFile = new File( dir, pdfName);
		FileOutputStream outputStream = new FileOutputStream(outputFile);

		Document document = new Document();//PageSize.A4.rotate());
		PdfWriter.getInstance(document, outputStream);
		document.open();
		
        chartView.setDrawingCacheEnabled(true);
        chartView.setDrawingCacheBackgroundColor(Color.WHITE);
        chartView.buildDrawingCache(false);
        
        Bitmap cachedBitmap = chartView.getDrawingCache();
		
        Bitmap bm = cachedBitmap.copy(Bitmap.Config.ARGB_8888/*.RGB_565*/, true);
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	bm.compress(Bitmap.CompressFormat.PNG, 100 , stream);
    	byte[] bytes = stream.toByteArray();
    	if( bytes != null && bytes.length > 0 )
    	{
    		Image img = Image.getInstance( bytes );
    		//img.setAbsolutePosition(0, 0);
    		//img.setScaleToFitHeight(false);
    		document.add(img);
    	}		
		
		return outputFile.getAbsolutePath();
	}
	
	
	
	
	
	public String createPdfChartFile_withscroll(int orientation) throws Exception {
		Document document = null;
		File outputFile = null;
		try {
			
			String pdfName = "BloodPreasureChart.pdf";
			File dir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS );
			if( !dir.exists() )
				dir.mkdirs();
			outputFile = new File( dir, pdfName);
			if( outputFile.exists() )
				outputFile.delete();
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			
			document = new Document();
			try { PdfWriter.getInstance(document, outputStream); } catch( Exception ex ){}
			document.open();
			
			Rectangle pageSize = document.getPageSize(); 
	    	float width = pageSize.getWidth();
	    	float height = pageSize.getHeight();
	    	float pageWidthNetto = width - 10;
	    	float pageHeigthNetto = height - 20;
	    	
			
//			ChartComputator cc = chartView.getChartComputator();
//			ChartScroller cs = new ChartScroller(MainActivityGrid.self.getContext());
//			//ChartZoomer cz = new ChartZoomer(MainActivityGrid.self.getContext(), ZoomType.HORIZONTAL);
//			//boolean b = cz.scale(cc, 0f, 0f, 20f);
//			ScrollResult arg3 = new ScrollResult();
	
			PDFExtension.addTitle(document, "Blutdruckwerte bis " + at.gepa.lib.tools.time.TimeTool.toDateString(Calendar.getInstance()));
			
//			int maxPages = 3;
//			int pages = 1;
//			while( cs.scroll(cc, -width, 0, arg3) )
//			{
//				pages++;
//				if( pages >= maxPages )
//					break;
//			}
//
			int pages = 1;
			for( int page =1; page <= pages; page++ )
			{
		        chartView.setDrawingCacheEnabled(true);
		        chartView.setDrawingCacheBackgroundColor(Color.WHITE);
		        chartView.buildDrawingCache();
		        
		        Bitmap cachedBitmap = chartView.getDrawingCache();
			
		        Bitmap bm = cachedBitmap.copy(Bitmap.Config.ARGB_8888, true);
		        
		        writeToFile( "bloodpreasure_chart.png", page, bm);
        	
        		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        		bm.compress(Bitmap.CompressFormat.PNG, 100 , stream);
        		
	        	byte[] bytes = stream.toByteArray();
	        	if( bytes != null && bytes.length > 0 )
	        	{
	        		Image img = Image.getInstance( bytes );
	        		img.setAlignment(Image.MIDDLE);
	        		img.setScaleToFitHeight(true);
					img.scaleToFit(pageWidthNetto, pageHeigthNetto );
	        		document.add(img);
	        	}
	    		stream.close();
	    		//cs.scroll(cc, width, 0, arg3);
        	}

			document.newPage();
			PDFExtension.addTitle(document, "Legende");
			PDFExtension.addListAsTable(document, BloodPreasureInfoActivity.getChartColorList() );
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		finally
		{
			if( document != null )
				document.close();
		}
		if( outputFile == null ) return null;
		return outputFile.getAbsolutePath();
	}
	private void writeToFile(String fname, int page, Bitmap bm) {
		String path = Environment.getExternalStorageDirectory().toString();
		OutputStream fOut = null;
		File file = new File(path, String.format("%d_%dx%d_%s", page, bm.getWidth(), bm.getHeight(), fname) ); // the File to save to
		if( file.exists() )
			file.delete();
		try {
			fOut = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
			fOut.flush();
			fOut.close(); // do not forget to close the stream
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
