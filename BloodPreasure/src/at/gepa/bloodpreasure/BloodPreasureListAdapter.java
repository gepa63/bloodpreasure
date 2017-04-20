package at.gepa.bloodpreasure;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyze;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.lib.model.BloodPreasureComperator;
import at.gepa.lib.model.IBloodPreasureAdapter;


public class BloodPreasureListAdapter extends ArrayAdapter<BloodPreasure> 
implements IBloodPreasureAdapter
{
	
	public BloodPreasureListAdapter(MainActivityGrid activity, int layout) 
	{
		super( activity, layout);
	}
	
	public void sort() {

	    super.sort(new BloodPreasureComperator());
	    notifyDataSetChanged();
	}	
	
	public static class ViewHolder{
        
        public TextView txtDatum;
        public TextView txtSyst;
        public TextView txtDiast;
        public TextView txtPuls;
        public TextView txtDescr;
		public TextView state;
		public TextView txtTags;
		public void showOrHide(String txt, TextView tv) {
			tv.setText( txt );
			boolean showIt = true;
			if( txt == null || txt.isEmpty() )
			{
				showIt = false;
			}
			tv.setVisibility( showIt ? View.VISIBLE : View.GONE );
		}
    }
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;//
		ViewHolder vh = null;
		
		if( convertView != null )
		{
			view = convertView;
			vh = (ViewHolder)view.getTag();
		}
		else
		{
            view =  MainActivityGrid.self.getLayoutInflater().inflate(R.layout.bloodpreasurevalue_row,parent,false);
            vh = new ViewHolder();
            view.setTag(vh);
            vh.txtDescr = (TextView)view.findViewById(R.id.cell_Description);
            
            vh.txtDatum = (TextView)view.findViewById(R.id.cell_Date);
			vh.txtSyst = (TextView)view.findViewById(R.id.cell_Systolisch);
			vh.txtDiast = (TextView)view.findViewById(R.id.cell_Diastolisch);
			vh.txtPuls = (TextView)view.findViewById(R.id.cell_Puls);
			vh.state = (TextView)view.findViewById(R.id.cell_State);
			vh.txtTags = (TextView)view.findViewById(R.id.cell_Tags);
			
			vh.state.setText(" ");
		}
		
		//Log.i( "at.gepa.adapter", "view is " + view.getClass().getSimpleName() );
		
		BloodPreasure bp = (BloodPreasure)getItem(position);

		BloodPreasureAnalyze ba = new BloodPreasureAnalyze(bp.getSystolisch(), bp.getDiastolisch(), Color.BLACK );
		
        vh.txtDatum.setTextColor(ba.analyzeColor());
		vh.txtSyst.setTextColor(ba.analyzeColor());
		vh.txtDiast.setTextColor(ba.analyzeColor());
		vh.txtPuls.setTextColor(ba.analyzeColor());
		vh.txtDescr.setTextColor(ba.analyzeColor());
		vh.txtTags.setTextColor(ba.analyzeColor());
		
		if( bp.isChanged() )
		{
			vh.state.setBackgroundColor( Color.parseColor("#FFFFBB33") );
			vh.state.setVisibility( TextView.VISIBLE );
		}
		else
		{
			vh.state.setVisibility( TextView.INVISIBLE );
		}
		vh.showOrHide( bp.getDescription(), vh.txtDescr );
		vh.showOrHide( bp.getTags(), vh.txtTags );
		
		vh.txtDatum.setText( (String)bp.get(BloodPreasure.COL_DATE) );
		vh.txtSyst.setText( bp.getSystolisch().toString() );
		vh.txtDiast.setText( bp.getDiastolisch().toString() );
		vh.txtPuls.setText( bp.getPuls().toString() );
		
		return view;
	}

	public void setChanged(boolean b) {
		for( int i = 0; i< getCount(); i++ )
		{
			getItem(i).setChanged(b);
		}
	}

	public boolean isChanged() {
		for( int i = 0; i< getCount(); i++ )
		{
			if( getItem(i).isChanged() )
				return true;
		}
		return false;
	}
	
//	@Override
//    public BloodPreasure getItem(int position) 
//    {
//		if( position <= 0 ) return null;
//        return super.getItem(position-1);
//    }
//	
}
