package at.gepa.tools2;

import java.util.ArrayList;

import at.gepa.bloodpreasure.ui.BloodPreasureInfoActivity;
import at.gepa.model.BloodPreasureInfo;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class PDFExtension {

	public static void addTitle(Document document, String title) throws DocumentException {
        Paragraph p = new Paragraph(title);
        p.getFont().setStyle(Font.BOLD | Font.UNDERLINE);
        p.getFont().setSize( 22f );
        p.setAlignment( Paragraph.ALIGN_CENTER );
        p.add( Chunk.NEWLINE );
        p.add( new Phrase(" ") );
        p.add( Chunk.NEWLINE );
        document.add(p);
	}

	public static void addListAsTable(Document document, ArrayList<BloodPreasureInfo> infoList) throws DocumentException {
        //add text-colors info:
        PdfPTable ttable = new PdfPTable( new float[]{1f, 1f} );
        ttable.setWidthPercentage(100f);
        for( BloodPreasureInfo bpi : BloodPreasureInfoActivity.getInfoList(null) )
        {
        	PdfPCell cell = new PdfPCell(new Phrase( bpi.getLabel() ));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            ttable.addCell(cell);
            
            cell = new PdfPCell(new Phrase( bpi.getText() ));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            ttable.addCell(cell);
            Font font = cell.getPhrase().getFont();
            font.setColor(new BaseColor( bpi.getColor()) );
            font.setSize(bpi.getTextSize());
        }
        document.add(ttable);
	}

}
