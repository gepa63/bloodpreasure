package at.gepa.bloodpreasure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyze;
import at.gepa.bloodpreasure.ui.BloodPreasureInfoActivity;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.tools2.DoubleClickListener;
import at.gepa.tools2.PDFExtension;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;


public class GridFragment extends Fragment {

	public static final String STATE_DATA = "BloodPreasure";
	public static final int CMD_EDIT		= 0;
	public static final int CMD_DEL		= 1;
	public static final int CMD_REFRESH	= 2;
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private View rootView;
	
	
	public static GridFragment newInstance() {
		return new GridFragment();
	}

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate( R.layout.fragment_main_activity_grid, container, false);
		
		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.SwipeRefreshLayoutId);
		
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
		     @Override
		     public void onRefresh() {
		         refreshList();
		     }
		});
		
		MainActivityGrid.self.list = (ListView) rootView.findViewById(R.id.BloodPreasureGrid);
        
		MainActivityGrid.self.list.setLongClickable(true);
		MainActivityGrid.self.list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
	        	MainActivityGrid.self.setCurrentEditable( MainActivityGrid.self.bloodPreasureAdapter.getItem(position) );
				return false;
			}
		});
		MainActivityGrid.self.list.setOnItemClickListener(new DoubleClickListener() {

	        @Override
	        public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
	        	MainActivityGrid.self.setCurrentEditable( MainActivityGrid.self.bloodPreasureAdapter.getItem(position) );
	        	MainActivityGrid.self.list.performClick();
	        }

	        @Override
	        public void onDoubleClick(AdapterView<?> parent, View view, int position, long id) 
	        {
	        	MainActivityGrid.self.setCurrentEditable( MainActivityGrid.self.bloodPreasureAdapter.getItem(position) );
	        	MainActivityGrid.self.startBloodPreasureEdit( (BloodPreasure) MainActivityGrid.self.bloodPreasureAdapter.getItem(position) );
	        }
	    });
		
        registerForContextMenu(MainActivityGrid.self.list);
        
        MainActivityGrid.self.bloodPreasureAdapter = new BloodPreasureListAdapter(MainActivityGrid.self, R.layout.bloodpreasurevalue_row );
        MainActivityGrid.self.list.setAdapter(MainActivityGrid.self.bloodPreasureAdapter);

        if( savedInstanceState == null )
        {
        	MainActivityGrid.createDownloadTask();
        }
        else
        {
    		MainActivityGrid.self.bloodPreasureAdapter.clear();
    	    ArrayList<Serializable> dataList = (ArrayList<Serializable>)savedInstanceState.getSerializable(STATE_DATA);
        	for( Serializable p : dataList )
        	{
        		if( p instanceof BloodPreasure )
        		{
        			BloodPreasure bp = (BloodPreasure)p;
        			MainActivityGrid.self.bloodPreasureAdapter.add(bp);
        		}
        	}
        	MainActivityGrid.self.refreshList();
        }
		return rootView;
	}
	

	@Override
	public void onSaveInstanceState (Bundle savedInstanceState) {
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);

	    
	    ArrayList<Serializable> list = new ArrayList<Serializable>();
	    int anzahlElemente = MainActivityGrid.self.bloodPreasureAdapter.getCount();

	    for (int i=0; i < anzahlElemente; i++) {
	        list.add( MainActivityGrid.self.bloodPreasureAdapter.getItem(i) );
	    }
	    savedInstanceState.putSerializable(STATE_DATA, list);
	}	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
	    super.onCreateContextMenu(menu, v, menuInfo);

	    MenuItem item = menu.add(0, CMD_EDIT, 0, R.string.menu_edit).setIcon(R.drawable.ic_launcher);
		EnableFunctionList.getInstance().add( item );

		item = menu.add(0, CMD_DEL, 10, R.string.menu_del);
		EnableFunctionList.getInstance().add( item );
		
	    menu.add(1, CMD_REFRESH, 20, R.string.menu_refresh).setIcon(R.drawable.download);
	    
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {

	    if (item.getItemId() == CMD_DEL) 
	    {
	    	final BloodPreasure bp = MainActivityGrid.self.getCurrentEditable();
	    	if( bp == null )
	    	{
	    		Toast.makeText(MainActivityGrid.self, "Kein Eintrag selektiert!", Toast.LENGTH_LONG).show();
	    		return false;
	    	}
	    	 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	    builder.setTitle(MainActivityGrid.self.getTitle());
	    	    builder.setMessage( String.format("Diese Messung wirklich löschen: %s %d/%d ?", bp.get(BloodPreasure.COL_DATE), bp.getSystolisch(), bp.getDiastolisch() ) );
	    	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
	    	    {
	    	        public void onClick(DialogInterface dialog, int id) {
	    	        	MainActivityGrid.self.bloodPreasureAdapter.remove(bp);
	    	        	MainActivityGrid.self.setCurrentEditable( null );
	    	        	MainActivityGrid.self.bloodPreasureAdapter.notifyDataSetChanged();
	    	        }
	    	 });	 
	    	 builder.create().show();
	    } 
	    else if (item.getItemId() == CMD_EDIT) 
	    {
	    	final BloodPreasure bp = MainActivityGrid.self.getCurrentEditable();
	    	if( bp == null )
	    	{
	    		Toast.makeText(MainActivityGrid.self, "Kein Eintrag selektiert!", Toast.LENGTH_LONG).show();
	    		return false;
	    	}
	    	MainActivityGrid.self.startBloodPreasureEdit( bp );
	    } 
	    else if (item.getItemId() == CMD_REFRESH) 
	    {
	    	refreshList();
	    } 
	    else 
	    {
	        return false;
	    }
	    return true;

	}

	public void refreshList() {
    	if( MainActivityGrid.self.bloodPreasureAdapter.isChanged() )
    	{
	    	 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	    builder.setTitle(MainActivityGrid.self.getTitle());
	    	    builder.setMessage( "Alle Änderungen verwerfen?" );
	    	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
	    	    {
	    	        public void onClick(DialogInterface dialog, int id) {
	    	        	MainActivityGrid.createDownloadTask();
	    	        }
	    	 });	 
	    	 builder.create().show();
    	}
    	else
    	{
    		MainActivityGrid.createDownloadTask();
    	}
	}

	public void setRefreshing(boolean b) {
		if(mSwipeRefreshLayout !=null)
			mSwipeRefreshLayout.setRefreshing(false);		
	}

	public String createPdfListFile(boolean details) throws Exception {
		
		String pdfName = "BloodPreasureList.pdf";
		if( details )
			pdfName = "BloodPreasureListDetail.pdf";
		
		File dir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS );
		if( !dir.exists() )
			dir.mkdirs();
		File outputFile = new File( dir , pdfName);
		if( outputFile.exists() )
			outputFile.delete();
	   outputFile.createNewFile();
		
	   try {
		   createPDFDocument( outputFile, details );
//		   if( details )
//			  createPDFDocument( outputFile, details );
//		   else
//			   createPDFDocumentFromView( outputFile );
	   } catch (Exception e) {
		   e.printStackTrace();
		   throw e;
	   }		

	   MainActivityGrid.self.list.setSelection( 0 );
	   MainActivityGrid.self.list.scrollTo(0, 0);
	   //MainActivityGrid.self.list.scrollTo(0, 0);
	   return outputFile.getAbsolutePath();
	}

	class HeaderFooter extends PdfPageEventHelper {
	    /** Alternating phrase for the header. */
	    Phrase[] header = new Phrase[2];
	    /** Current page number (will be reset for every chapter). */
	    int pagenumber;

	    
	    public HeaderFooter(String title)
	    {
	    	header[0] = new Phrase(title);
	    }
	    /**
	     * Initialize one of the headers.
	     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
	     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
	     */
	    public void onOpenDocument(PdfWriter writer, Document document) {
	    }

	    /**
	     * Initialize one of the headers, based on the chapter title;
	     * reset the page number.
	     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onChapter(
	     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document, float,
	     *      com.itextpdf.text.Paragraph)
	     */
	    public void onChapter(PdfWriter writer, Document document,
	            float paragraphPosition, Paragraph title) {
	        header[1] = new Phrase(title.getContent());
	        pagenumber = 1;
	    }

	    /**
	     * Increase the page number.
	     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(
	     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
	     */
	    public void onStartPage(PdfWriter writer, Document document) {
	        pagenumber++;
	    }

	    /**
	     * Adds the header and the footer.
	     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
	     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
	     */
	    public void onEndPage(PdfWriter writer, Document document) {
	        Rectangle rect = writer.getBoxSize("XXX");
	        switch(writer.getPageNumber() % 2) {
	        case 0:
	            ColumnText.showTextAligned(writer.getDirectContent(),
	                    Element.ALIGN_RIGHT, header[0],
	                    rect.getRight(), rect.getTop(), 0);
	            break;
	        case 1:
	            ColumnText.showTextAligned(writer.getDirectContent(),
	                    Element.ALIGN_LEFT, header[1],
	                    rect.getLeft(), rect.getTop(), 0);
	            break;
	        }
	        ColumnText.showTextAligned(writer.getDirectContent(),
	                Element.ALIGN_CENTER, new Phrase(String.format("page %d", pagenumber)),
	                (rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18, 0);
	    }
	}	
	private void createPDFDocument(File outputFile, boolean withDetails) throws Exception 
	{
		OutputStream out = new FileOutputStream(outputFile);
		
		Document document = new Document(); //PageSize.A4.rotate()
		//document.setPageSize( document.getPageSize().rotate() );
        PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
        
        document.open();
        
        String title = "Blutdruckdetails vom " + at.gepa.lib.tools.time.TimeTool.toDateString(Calendar.getInstance());
        
        document.addTitle(title);
        document.addCreationDate();
        document.addAuthor(BloodPreasureInfoActivity.getAuthor());
        
        PDFExtension.addTitle( document, title);
        
        PdfPTable table = new PdfPTable(BloodPreasure.createPDFHeaderWeights(withDetails));
        table.setWidthPercentage(100f);
        if( withDetails )
        {
        	table.setSpacingBefore(0f);
            table.setSpacingAfter(0f);        	
        }
        
        boolean bwithPrefix = true;
        String header[] = BloodPreasure.createPDFHeader(bwithPrefix, withDetails);
        Font f = new Font();
        f.setColor(BaseColor.BLACK);
        f.setStyle(Font.BOLD);
        for( String ht : header )
        {
            PdfPCell cell = new PdfPCell(new Phrase(ht, f));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        	table.addCell(cell);
        }
        table.setHeaderRows(1);
        
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        if( table.getDefaultCell().getPhrase() == null )
        	table.getDefaultCell().setPhrase(new Phrase(""));
        Font defFont = table.getDefaultCell().getPhrase().getFont();
        if( defFont != null )
        	defFont.setSize( defFont.getSize() * 1.5f );

        float specBorderWidth = 0.0f;
        for( int i=0; i < MainActivityGrid.self.bloodPreasureAdapter.getCount(); i++ )
        {
        	BloodPreasure bp = MainActivityGrid.self.bloodPreasureAdapter.getItem(i);
        	BloodPreasureAnalyze bpa = new BloodPreasureAnalyze(bp.getSystolisch(), bp.getDiastolisch());
        	BaseColor tcolor = new BaseColor(bpa.analyzeColor());
        	
            String b = bp.getDescription();
            String t = bp.getTags();
            boolean hasDescr = ( b != null && !b.trim().isEmpty() );
            boolean hasTags = ( t != null && !t.trim().isEmpty() );
        
            PdfPCell cell = new PdfPCell(new Phrase( bp.getPrettyText(BloodPreasure.COL_IDX_DATUM) ));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.getPhrase().getFont().setColor(tcolor);
            if( hasDescr || hasTags ) cell.setBorderWidthBottom(specBorderWidth);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase( bp.getPrettyText(BloodPreasure.COL_IDX_SYSTOLISCH, true, false) ));
            cell.getPhrase().getFont().setColor(tcolor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase( bp.getPrettyText(BloodPreasure.COL_IDX_DIASTOLISCH, true, false) ));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.getPhrase().getFont().setColor(tcolor);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase( bp.getPrettyText(BloodPreasure.COL_IDX_PULS, true, false) ));
            cell.getPhrase().getFont().setColor(tcolor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            if( withDetails )
            {
	            cell = new PdfPCell(new Phrase( bp.getPrettyText(BloodPreasure.COL_IDX_GEWICHT, true, false) ));
	            cell.getPhrase().getFont().setColor(tcolor);
	            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(cell);
	            cell = new PdfPCell(new Phrase( bp.getPrettyText(BloodPreasure.COL_IDX_ZUCKER, true, false) ));
	            cell.getPhrase().getFont().setColor(tcolor);
	            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(cell);
	            cell = new PdfPCell(new Phrase( bp.getPrettyText(BloodPreasure.COL_IDX_TEMP, true, false) ));
	            cell.getPhrase().getFont().setColor(tcolor);
	            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(cell);
            }        	
            if(hasDescr)
            {
                cell = new PdfPCell(new Phrase( b ));
                cell.setColspan(header.length);
                cell.setBorderWidthTop(specBorderWidth);
                table.addCell(cell);
            }
            if( hasTags )
            {
                cell = new PdfPCell(new Phrase( t ));
                cell.setColspan(header.length);
                cell.setBorderWidthTop(specBorderWidth);
                table.addCell(cell);
            }
        }
        document.add(table);

        document.newPage();
        
        PDFExtension.addTitle( document, "Legende");
        PDFExtension.addListAsTable( document, BloodPreasureInfoActivity.getInfoList(null) );
        
        document.close();
        
	}

	private void createPDFDocumentFromView(File outputFile) throws Exception {
		PdfDocument document = new PdfDocument();
		int h = rootView.getHeight();// - 20;
		int pageNumber = 1;

		MainActivityGrid.self.list.setSelection( 0 );
		MainActivityGrid.self.list.scrollTo(0, 0);
		
		int lastVis = 		MainActivityGrid.self.list.getLastVisiblePosition();
		int maxRow = MainActivityGrid.self.list.getCount();
		int rowsPerPage = lastVis; 
		
//		int heightPerRow = MainActivityGrid.self.list.getHeight() / rowsPerPage;
//		int headerHeight = 20;
//		View v = rootView.findViewById(R.id.inclheaderId);
//		if( v != null )
//			headerHeight = v.getHeight();
//		headerHeight++;
		
		boolean isLastPage = false;
		
		//MainActivityGrid.self.list.scrollTo(0, 0);
		
		int currentYPositionInPage = 0;
		do
		{		
			PageInfo pageInfo = new PageInfo.Builder(rootView.getWidth(), h, pageNumber).create();
			
			Page page = document.startPage(pageInfo);
			
			rootView.draw(page.getCanvas());
			
			document.finishPage(page);
			
			if( isLastPage ) break;
			
			if( maxRow - lastVis < rowsPerPage )
			{
				isLastPage = true;
			}

			currentYPositionInPage = 0;
			int firstVis = 0;
			while( firstVis < rowsPerPage )
			{
				View v = MainActivityGrid.self.list.getChildAt(firstVis);
				if( v == null )
				{
					break;
				}
				currentYPositionInPage += v.getHeight();
				firstVis++;
			}
			MainActivityGrid.self.list.setSelection( MainActivityGrid.self.list.getLastVisiblePosition() + 1);
			MainActivityGrid.self.list.scrollListBy( currentYPositionInPage );
			lastVis = 		MainActivityGrid.self.list.getLastVisiblePosition();
		}while( true);
		
	   OutputStream out = new FileOutputStream(outputFile);
	   document.writeTo(out);
	   document.close();
	   out.close();
		
	}
}
