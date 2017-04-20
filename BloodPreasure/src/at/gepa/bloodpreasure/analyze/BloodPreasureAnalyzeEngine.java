package at.gepa.bloodpreasure.analyze;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.model.BloodPreasureInfo;
import at.gepa.tools2.PDFExtension;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


//http://developers.itextpdf.com/examples/itext-action-second-edition/chapter-4
	
	
	
public class BloodPreasureAnalyzeEngine {
	
	public BloodPreasureAnalyzeEngine()
	{
	}
	
	private Point getScreenSize(){
	    Point size = new Point();
	    WindowManager w = MainActivityGrid.self.getWindowManager();

	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
	        w.getDefaultDisplay().getSize(size);
	        return size;
	    }else{
	        Display d = w.getDefaultDisplay();
	        //noinspection deprecation
	        return new Point( d.getWidth(), d.getHeight() );
	    }
	}	
	
	public String createPdfAnalyzeFile() throws Exception {

		String pdfName = "BloodPreasureAnalyze.pdf";
		
		File dir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS );
		if( !dir.exists() )
			dir.mkdirs();
		File outputFile = new File( dir , pdfName);
		if( outputFile.exists())
			outputFile.delete();

		outputFile.createNewFile();
		OutputStream out = new FileOutputStream(outputFile);
		
		Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        String title = "Blutdruck Analyse vom " + at.gepa.lib.tools.time.TimeTool.toDateString(Calendar.getInstance());
        document.addTitle(title);
        
        PDFExtension.addTitle( document, title);
        
        PdfPTable table = new PdfPTable(new float[] { 1, 2 });
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setHeaderRows(0);
        
        ArrayList<BloodPreasureInfo> list = createList();
        for( int i=0; i < list.size(); i++ )
        {
        	BloodPreasureInfo bpi = list.get(i);
        	table.addCell( bpi.getLabel() );
        	table.addCell( bpi.getText() );
        	
        	PdfPCell[] cells = table.getRow(i).getCells();
        	Font f = cells[1].getPhrase().getFont();
        	if( bpi.hasColor() )
        		f.setColor(new BaseColor( bpi.getColor() ));
        	if( bpi.hasTextSize() )
        	{
        		f.setSize( bpi.getTextSize() );
        		float newH = bpi.getTextSize() * 1.2f;// cells[1].getHeight() * 1.6f;
        		cells[0].setFixedHeight( newH );
        		cells[1].setFixedHeight( newH );
        	}
        }
        document.add(table);
        
        document.close();
		
		return outputFile.getAbsolutePath();
	}


	public static ArrayList<BloodPreasureInfo> createList()
	{
		ArrayList<BloodPreasureInfo> adapter = new ArrayList<BloodPreasureInfo>();
		adapter.add( new BloodPreasureInfo("Alter", "" + BloodPreasureAnalyze.getAge() + " Jahre" ) );
		adapter.add( new BloodPreasureInfo("Gewicht", "" + BloodPreasureAnalyze.getGewicht() + " Kg" ) );
		adapter.add( new BloodPreasureInfo("Größe", "" + BloodPreasureAnalyze.getGroesse() + " cm" ) );
		adapter.add( new BloodPreasureInfo("Taile", "" + BloodPreasureAnalyze.getTaile() + " cm" ) );
		adapter.add( new BloodPreasureInfo("Hüfte", "" + BloodPreasureAnalyze.getHuefte() + " cm" ) );
		
		adapter.add( new BloodPreasureInfo("", "") );
		adapter.add( new BloodPreasureInfo("Anzahl Messungen", "" +MainActivityGrid.self.bloodPreasureAdapter.getCount() ) );
		adapter.add( new BloodPreasureInfo("Erste Messung", "" + MainActivityGrid.self.bloodPreasureAdapter.getItem( MainActivityGrid.self.bloodPreasureAdapter.getCount()-1 ).toString() ) );
		adapter.add( new BloodPreasureInfo("Letzte Messung", "" + MainActivityGrid.self.bloodPreasureAdapter.getItem(0).toString() ) );

		BloodPreasureAnalyze bpa = new BloodPreasureAnalyze(MainActivityGrid.self.avgValueHolder.getAvgSyst(), MainActivityGrid.self.avgValueHolder.getAvgDiast() );
		
		adapter.add( new BloodPreasureInfo("Durchschn. SYS", "" + MainActivityGrid.self.avgValueHolder.getAvgSyst(), bpa.analyzeColor() ) );
		adapter.add( new BloodPreasureInfo("Durchschn. DIA", "" + MainActivityGrid.self.avgValueHolder.getAvgDiast(), bpa.analyzeColor() ) );
		adapter.add( new BloodPreasureInfo("Durchschn. PULS", "" + MainActivityGrid.self.avgValueHolder.getAvgPuls() ) );
		
		adapter.add( new BloodPreasureInfo("Analyse", bpa.analyzeText().toString(), bpa.analyzeColor() ) );
		
		adapter.add( new BloodPreasureInfo("Tendenz", MainActivityGrid.self.avgValueHolder.getTendency(), MainActivityGrid.self.avgValueHolder.getTendenzyColor() ) );
		
		adapter.add( new BloodPreasureInfo("", "") );
		adapter.add( new BloodPreasureInfo("BMI", String.format("%.2f", BloodPreasureAnalyze.getBMI()) ) );
		adapter.add( new BloodPreasureInfo("", BloodPreasureAnalyze.getBMIAnalyse(), 18f ) );
		
		adapter.add( new BloodPreasureInfo("Kalorien Grundverbrauch", String.format("~ %d kcal/Tag", BloodPreasureAnalyze.calcKalorienGrundVerbrauch() ) ) );
		
		adapter.add( new BloodPreasureInfo("", "") );
		adapter.add( new BloodPreasureInfo("Taile/Hüfte", String.format("%.2f", BloodPreasureAnalyze.getTHV() ) ) );
		adapter.add( new BloodPreasureInfo("KVI", String.format("%.2f", BloodPreasureAnalyze.getKVI() ) ) );
		adapter.add( new BloodPreasureInfo("", BloodPreasureAnalyze.getKVIAnalyzeText(), 18f ) );
		
		adapter.add( new BloodPreasureInfo("", "") );
		adapter.add( new BloodPreasureInfo("Taile/Größe", String.format("%.2f", BloodPreasureAnalyze.getTGV() ) ) );
		adapter.add( new BloodPreasureInfo("", BloodPreasureAnalyze.getTGVAnalyzeText(), 18f ) );
	
		return adapter;
	}

}
