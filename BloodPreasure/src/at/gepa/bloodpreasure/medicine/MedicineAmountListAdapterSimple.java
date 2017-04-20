package at.gepa.bloodpreasure.medicine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import at.gepa.androidlib.ui.DialogMessageBox;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.pref.EditMedicationActivity;
import at.gepa.lib.model.medicine.MedicineAmount;


public class MedicineAmountListAdapterSimple 
extends ArrayAdapter<MedicineAmount> 
{
	public static interface MedicineAmountRemoveListener
	{
		public void onRemove( MedicineAmount ma );
	}

	private static MedicineAmountRemoveListener removeListener;
	public static void setMedicineAmountRemoveListener( MedicineAmountRemoveListener l )
	{
		removeListener = l;
	}
	private static EditMedicationActivity context;
	private static Bitmap iconEdit;
	private static Bitmap iconRemove;
	public static void setParentContext(EditMedicationActivity c)
	{
		context = c;
	}
	public MedicineAmountListAdapterSimple( Context activity, int layout )
	{
		super(activity, layout);
	}
	
	
	public static class ViewHolder{
		private TextView textView;
		private ImageButton btMedEdit;
		private ImageButton btMedDel;
		public MedicineAmount medicineAmount;
		public OnClickListener createButtonOnClickListener() 
		{
			return 	new OnClickListener() {
				@Override
				public void onClick(View v) {
				
					MedicineEditView ev = new MedicineEditView(context, medicineAmount);
					DialogMessageBox.simpleEdit("Titel", ev, context, ev);
				}
		};
		}
		public OnClickListener createRemoveButtonOnClickListener() {
			return 	new OnClickListener() {
				@Override
				public void onClick(View v) {
					if( removeListener != null )
						removeListener.onRemove(medicineAmount);
				}
			};
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
            view =  MainActivityGrid.self.getLayoutInflater().inflate(R.layout.edit_medication_row_simple, parent, false);
            vh = new ViewHolder();
            view.setTag(vh);
            
            vh.textView = (TextView)view.findViewById(R.id.labelMedikamentFull);
            vh.btMedEdit = (ImageButton)view.findViewById(R.id.txtButtonEditMed);
            
            int w = 32;//vh.textView.getWidth();
            int h = 32;//vh.textView.getHeight();
            if( w == 0) w = 24; if( h == 0) h = 24;
            if( MedicineAmountListAdapterSimple.iconEdit == null ) 
            {
            	Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.edit);
            	w = (int)(2 * imageBitmap.getWidth()/3f);
            	h = (int)(2 * imageBitmap.getHeight()/3f);
            	MedicineAmountListAdapterSimple.iconEdit = Bitmap.createScaledBitmap(imageBitmap, w, h, false);
            }
            vh.btMedEdit.setBackground(null);
            vh.btMedEdit.setImageBitmap( MedicineAmountListAdapterSimple.iconEdit );
            vh.btMedEdit.setOnClickListener(vh.createButtonOnClickListener());
            
            vh.btMedDel = (ImageButton)view.findViewById(R.id.txtButtonDelMed);
            vh.btMedDel.setOnClickListener( vh.createRemoveButtonOnClickListener() );
            if( MedicineAmountListAdapterSimple.iconRemove == null )
            {
            	Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.remove);
            	MedicineAmountListAdapterSimple.iconRemove = Bitmap.createScaledBitmap(imageBitmap, w, h, false);
            }
            vh.btMedDel.setBackground(null);
            vh.btMedDel.setImageBitmap( MedicineAmountListAdapterSimple.iconRemove );
		}
		
		vh.medicineAmount = getItem(position);
		vh.textView.setText( vh.medicineAmount.toString() );
		
		return view;
	}
	public void startNew(View view) {
		ViewHolder vh = (ViewHolder)view.getTag();
		if( vh != null )
		{
			vh.btMedEdit.callOnClick();
		}
	}
}
