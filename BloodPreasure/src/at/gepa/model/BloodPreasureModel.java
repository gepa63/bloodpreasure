package at.gepa.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyze;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.net.FileStreamAccess;
import at.gepa.net.IElement;
import at.gepa.net.IModel;
import at.gepa.net.IReadWriteHeaderListener;
import at.gepa.net.IWriteable;

//Wraper Class, encapsulate adapter list in MainActivityGrid
public class BloodPreasureModel implements IModel {
	public static final String TENSOVAL_HEADER = "DATUM,SYSTOLE,DIASTOLE,PULS,Beschreibung,Tags,Gewicht";
	
	private MainActivityGrid self;
	
	public BloodPreasureModel( MainActivityGrid m )
	{
		self = m;
		this.self.lastModified = 0;
	}

	@Override
	public int size() {
		return self.size();
	}
	
	@Override
	public void checkPrevious(IElement _prev, IElement _bp) {
		BloodPreasure prev = (BloodPreasure)_prev;
		BloodPreasure bp = (BloodPreasure)_bp;
		if( at.gepa.lib.tools.time.TimeTool.check(prev.getDateTime(), bp.getDateTime()) )
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(prev.getDateTime());
			if( cal.get(Calendar.HOUR_OF_DAY) < 11 )
			{
				cal.add(Calendar.HOUR_OF_DAY, 12);
				MainActivityGrid.self.refreshInvalidated();
			}
			prev.setDate( cal.getTime() );
		}
	}
	
	@Override
	public IWriteable get(int i) {
		return self.bloodPreasureAdapter.getItem(i);
	}
	
	@Override
	public IElement createInstance(String[] split) {
		BloodPreasure bp = new BloodPreasure(split);
		if( bp.getGewicht() == 0f )
			bp.setGewicht( BloodPreasureAnalyze.getGewicht() );
		return bp;
	}
	
	@Override
	public void clearModel() {
		self.clearModel();
	}
	
	@Override
	public void add(IElement bp) {
		self.add( (BloodPreasure)bp );
	}

	@Override
	public void writeHeader(OutputStreamWriter writer, String header) throws IOException {
//		if( MainActivityGrid.TENSOVAL_MODE )
//		{
//			writer.write("TDC-CSV,1.0"+FileStreamAccess.crlf);
//			writer.write("CS50098_D	Rev. 34"+FileStreamAccess.crlf);
//			writer.write("USB-HW,Rev. 1"+FileStreamAccess.crlf);
//			SimpleDateFormat sd = new SimpleDateFormat( "yy,MM,dd,hh,mm,ss", Locale.GERMAN);
//			writer.write( sd.format(Calendar.getInstance().getTime()) +FileStreamAccess.crlf);
//			writer.write("MemArea,Year,Month,Day,Hour,Minute,IHB,SYS,DIA,PUL");
//		}
//		else
//		{
		writer.write(header);
//		}
	}

	@Override
	public IReadWriteHeaderListener getHeaderListener() 
	{
		return BloodPreasureModel.createHeaderListener();
	}

	public static IReadWriteHeaderListener createHeaderListener()
	{
		return new IReadWriteHeaderListener()
		{

			@Override
			public void readHeader(BufferedReader reader) throws IOException {
				reader.readLine(); //first line is header
			}

			@Override
			public void writeHeader(OutputStreamWriter writer, IModel list) throws IOException {
//				if( MainActivityGrid.TENSOVAL_MODE )
//					list.writeHeader( writer, BloodPreasure.createHeaderLine() );
//				else
				list.writeHeader( writer, TENSOVAL_HEADER );
		        writer.write( FileStreamAccess.crlf );
			}
			
		};
		
	}

	@Override
	public void done() {
		//sonst wird MainActivityGrid.setModelDone(..) 2x aufgerufen 
//		self.setModelDone(false);
		self.saveCache(this);
	}

	@Override
	public boolean contains(IElement bp) {
		return self.contains(bp);
	}

	@Override
	public void add(int i, IElement bp) {
		self.add( i, (BloodPreasure)bp );
	}

	@Override
	public boolean checkLastModified(long lastModified) {
		return self.checkLastModified(lastModified);
	}

	@Override
	public void setLastModified(long lastModified) {
		self.setLastModified(lastModified);
	}

	@Override
	public String setStream(InputStream input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLineToProceed(String line, String fieldDelim) {
		return !line.isEmpty() && (fieldDelim == null || line.contains(fieldDelim));
	}
}
