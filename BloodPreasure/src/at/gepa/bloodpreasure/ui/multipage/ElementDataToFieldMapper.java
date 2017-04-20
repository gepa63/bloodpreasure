package at.gepa.bloodpreasure.ui.multipage;

import java.lang.reflect.Array;
import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.OnWheelClickedListener;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.WheelVerticalView;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;
import at.gepa.bloodpreasure.R;
import at.gepa.bloodpreasure.analyze.BloodPreasureAnalyze;
import at.gepa.bloodpreasure.medicine.DailyMedicationView;
import at.gepa.bloodpreasure.medicine.DailyMedicationView.MedicationDataHolder;
import at.gepa.bloodpreasure.pref.BloodPreasurePreferenceActivity;
import at.gepa.bloodpreasure.ui.TagListView;
import at.gepa.lib.model.BloodPreasure;
import at.gepa.lib.model.medicine.Medication;
import at.gepa.lib.model.medicine.Medication.eMedicationTime;
import at.gepa.lib.model.medicine.MedicineAmountList;
import at.gepa.lib.tools.Util;
import at.gepa.listener.IChangeListener;
import at.gepa.net.IElement;

public class ElementDataToFieldMapper {
	
	private static OnCheckedChangeListener checkBoxListenerMarkUntilRevoke;
	protected boolean timeScrolled;
	protected boolean valueChanged;
	private IBloodPreasureChangeListener changeListener;
	private int page;
	private LinearLayout ll;
	private IElement element;
	private Context context;
	private LayoutInflater inflater;
	private EditFragment parent;
	private EditText tv;
	private GradientDrawable shape;
	private NumericWheelAdapter adapter;
	private float textSize;
	private int prefixTextSize;

	public ElementDataToFieldMapper(IBloodPreasureChangeListener changeListener, int page, LinearLayout ll, IElement element, Context context, LayoutInflater inflater, EditFragment parent)
	{
		 timeScrolled = false;
         valueChanged = false;
         this.changeListener = changeListener;
         this.page = page;
         this.ll = ll;
         this.element = element;
         this.context = context;
         this.inflater = inflater;
         this.parent = parent;
         this.textSize = 32f;
         this.prefixTextSize = 24;
	}

	public EditViewHolder addEditFields() 
	{
		this.tv = ViewFactory.createEditView( context);
		tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		tv.getLayoutParams().width = LayoutParams.MATCH_PARENT;
		
		Resources res = parent.getResources();
		shape = (GradientDrawable)res.getDrawable(R.xml.tags_rounded_corners3, null);
		
        tv.setBackground(shape);	
        
		EditViewHolder evh = new EditViewHolder();
		evh.add( page, tv );
		
		ll.addView(tv, ll.getChildCount());
		
		Class<?> ty = element.getType(page);
		if( ty == java.util.Date.class || ty == java.sql.Date.class )
		{
			makeDateTimeFields( );
		}
		else if( ty == Integer.class )
		{
			makeSpinnerField();
		}
		else if( ty == Array.class )
		{
			makeTagListField(  );
		}
		else if( ty == String.class )
		{
			tv.setLines( element.getLines(page) );
			addPrefixField();
			addTextChangeListener();
		}
		else if( ty == Medication.class )
		{
			makeMedicationField();
		}
		else if( element.getPrefix(page) != null )
		{
			tv.setTextSize(this.textSize);
			addPrefixField();
			addTextChangeListener();
		}
		
		setToField(element, tv, page);
		
		return evh;
	}
	private void makeMedicationField() {
		TagListView.setChangeListener((IChangeListener)parent);
		
		//Tags, ListView
		tv.setKeyListener(null);
		tv.setVisibility(TextView.GONE);
		
		BloodPreasure bp = (BloodPreasure)element;

		Medication medicationPerDay = Medication.createInstance(bp.getMedikation());
		MedicationDataHolder listener = new MedicationDataHolder() {
			
			@Override
			public void updateMedicine(eMedicationTime mt, MedicineAmountList medicineAmountList) {
				BloodPreasure bp = (BloodPreasure)element;
				Medication med = Medication.createInstance(bp.getMedikation());
				med.set(mt, medicineAmountList);
				bp.setMedikation(med.toString());
			}
			
			@Override
			public Context getContext() {
				return tv.getContext();
			}

			@Override
			public boolean useSmallText() {
				return false;
			}
		};
		DailyMedicationView view = new DailyMedicationView(tv.getContext(), medicationPerDay, listener);
		tv.setTag(view);
		
		if( view.getLayoutParams() == null )
		{
			view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			((LinearLayout.LayoutParams)view.getLayoutParams()).weight = 1f;
			((LinearLayout.LayoutParams)view.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
		}
		
		//ll.setBackground(shape);

		ll.addView(view, ll.getChildCount());
		
		Button btTakeCurrentMed = new Button(ll.getContext());
		btTakeCurrentMed.setBackground(shape);
		btTakeCurrentMed.setText( "Aktuelle Medikation" );
		btTakeCurrentMed.setGravity(Gravity.CENTER_HORIZONTAL);
		btTakeCurrentMed.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DailyMedicationView view = (DailyMedicationView)tv.getTag();
				BloodPreasure bp = (BloodPreasure)element;
				bp.setMedikation( BloodPreasureAnalyze.getMedication() );
				bp.setChanged(true);
				Medication med = Medication.createInstance(bp.getMedikation());
				view.updateMedication(med);
				
			}
		});
		ll.addView(btTakeCurrentMed, ll.getChildCount());
	}

	private void addTextChangeListener() {
		tv.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				changeListener.dataChanged(element, page, s.toString() );
			}
		});
	}

	private TextView addPrefixField() {
		if( element.getPrefix(page) != null )
		{
			TextView textPrefix = createTextView( element.getPrefix(page), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, Color.parseColor("#2f6699"), 
					prefixTextSize );
			ll.addView(textPrefix, ll.getChildCount());
			return textPrefix;
		}
		return null;
	}

	private void makeSpinnerField() 
	{
//      this.textSize = 32f;
//      this.prefixTextSize = 24;
		
        this.prefixTextSize = 28;//(int)textSize;
        textSize = 42f;
        int visibleItems = 8; //10

		tv.setVisibility(View.GONE);

		WheelVerticalView spinner = new WheelVerticalView(context);
		if( spinner.getLayoutParams() == null )
			spinner.setLayoutParams( ViewFactory.createDefaultLayout() );
		spinner.setVisibleItems(visibleItems);
		
		LinearLayout.LayoutParams spl = (LinearLayout.LayoutParams)spinner.getLayoutParams();
		spl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		spl.height = LayoutParams.WRAP_CONTENT;
		spl.width = LayoutParams.WRAP_CONTENT;
		spl.weight = 0f;

		TextView textm = createTextView( "->", Gravity.END | Gravity.CENTER_VERTICAL, Color.parseColor("#2f6699"), textSize );
		TextView textPrefix = null;
		if( element.getPrefix(page) != null )
			textPrefix = createTextView( element.getPrefix(page), Gravity.START | Gravity.CENTER_VERTICAL, Color.parseColor("#2f6699"), textSize );
		
		LinearLayout lHorz = ViewFactory.createDefaultLinearLayout(context);
		LinearLayout.LayoutParams lph = (LinearLayout.LayoutParams)lHorz.getLayoutParams(); 
		lph.weight = 1f;
		lph.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		lph.width = LayoutParams.MATCH_PARENT;
		lHorz.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
		
		lHorz.setBackground(shape);
		RelativeLayout.LayoutParams lllp = (RelativeLayout.LayoutParams)ll.getLayoutParams();
		lllp.width = LayoutParams.MATCH_PARENT;
		
		lHorz.addView(textm, lHorz.getChildCount() );
		lHorz.addView(spinner, lHorz.getChildCount() );
		if( textPrefix != null )
			lHorz.addView(textPrefix, lHorz.getChildCount() );
		ll.addView(lHorz, ll.getChildCount() );
		
		adapter = new NumericWheelAdapter(context, 40, 280 );
		
		spinner.setViewAdapter(adapter);
		spinner.setCyclic(true);
		
		tv.setTag(spinner);
		spinner.setTag(tv);
		
		Integer compValue = (Integer)element.get(page);
		for( int i=0; i < adapter.getItemsCount(); i++ )
		{
			String v = adapter.getItemText(i).toString();
			int iv = Integer.parseInt(v);
			if( iv == compValue.intValue() )
			{
				spinner.setCurrentItem(i);
				break;
			}
		}
		
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
            	if (!timeScrolled) 
            	{
                    valueChanged = true;
                    EditText et = (EditText)wheel.getTag();
                    et.setText("" + newValue );
                    changeListener.dataChanged( element, page, newValue );
                    valueChanged = false;
            	}
            }
        };
        
        OnWheelClickedListener click = new OnWheelClickedListener() {
            public void onItemClicked(AbstractWheel wheel, int itemIndex) {
                wheel.setCurrentItem(itemIndex, true);
            }
        };
        
        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                timeScrolled = true;
            }
            public void onScrollingFinished(AbstractWheel wheel) {
                timeScrolled = false;
                valueChanged = true;
            	EditText et = (EditText)wheel.getTag();
            	int index = wheel.getCurrentItem();
            	int newValue = Integer.parseInt(adapter.getItemText(index).toString());
            	et.setText("" + newValue );
                changeListener.dataChanged( element, page, newValue );
                valueChanged = false;
            }
        };        
        spinner.addChangingListener(wheelListener);
        spinner.addClickingListener(click);
        spinner.addScrollingListener(scrollListener);
        spinner.invalidate();
        
        spinner.setSelectorPaintCoeff(1f);
        spinner.setSeparatorsPaintAlpha(0);
        adapter.setTextSize((int)textSize);
        adapter.setTextColor(Color.parseColor("#2f6699"));
        
	}
	private TextView createTextView(String text, int gravity, int color, float textSize) {
		TextView textm = new TextView(context);
		if( textm.getLayoutParams() == null )
			textm.setLayoutParams( ViewFactory.createDefaultLayout(0) );
		LinearLayout.LayoutParams textmll = (LinearLayout.LayoutParams)textm.getLayoutParams();
		textmll.height = LayoutParams.WRAP_CONTENT;
		textmll.width = LayoutParams.WRAP_CONTENT;
		textmll.gravity = gravity;
		textmll.weight = 0f;
		textm.setText(text);
		textm.setTextSize(textSize);
		textm.setTextColor(color);
		
		tv.setTextColor(color);
		
		return textm;
	}


	private void makeTagListField() {
		//http://adanware.blogspot.co.at/2012/03/android-custom-spinner-with-custom.html
		
		//TagListActivity.setChangeListener((IChangeListener)parent);
		TagListView.setChangeListener((IChangeListener)parent);
		
		//Tags, ListView
		tv.setKeyListener(null);
		tv.setVisibility(TextView.GONE);
		
		BloodPreasure bp = (BloodPreasure)element;
		ListView tagListView = new TagListView(tv.getContext(),  new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String checkedElements = Util.convertToString(((TagListView)tv.getTag()).getCheckedTags(true), BloodPreasurePreferenceActivity.TAG_DELIMITER);
				tv.setText(checkedElements);
				changeListener.dataChanged(element, page, checkedElements );
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				String checkedElements = Util.convertToString(((TagListView)tv.getTag()).getCheckedTags(true), BloodPreasurePreferenceActivity.TAG_DELIMITER);
				tv.setText(checkedElements);
				changeListener.dataChanged(element, page, checkedElements );
			}});
		tv.setTag(tagListView);
		((TagListView)tagListView).setChecked( bp.getTags() );
		
		
		if( tagListView.getLayoutParams() == null )
			tagListView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		((LinearLayout.LayoutParams)tagListView.getLayoutParams()).weight = 1f;
		((LinearLayout.LayoutParams)tagListView.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
		
		LinearLayout linlayout = new LinearLayout(tv.getContext()); 
		linlayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)linlayout.getLayoutParams();
		if( lp == null )
			linlayout.setLayoutParams(lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		lp.gravity = Gravity.CENTER;
		lp.weight = 1f;
		lp.height = LayoutParams.WRAP_CONTENT;
		lp.width = LayoutParams.MATCH_PARENT;
		linlayout.setLayoutParams(lp);
		
		linlayout.addView(tagListView, linlayout.getChildCount() );

		ImageButton btAdd = new ImageButton(tv.getContext());
		tagListView.setTag(btAdd);
		btAdd.setTag(tagListView);
		btAdd.setId(R.id.btApply);
		btAdd.setImageResource( R.drawable.plus );
		
		btAdd.setBackgroundResource( R.drawable.round_button );
		btAdd.setContentDescription("Hinzu");
		btAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((TagListView)tv.getTag()).addElement();
			}
		});
		LinearLayout.LayoutParams lparam = (LinearLayout.LayoutParams)btAdd.getLayoutParams();
		int btSize = 120;
		if( lparam == null )
			btAdd.setLayoutParams(lparam = new LinearLayout.LayoutParams(btSize, btSize));
		else
		{
			lparam.height = btSize;
			lparam.width = btSize;
		}
		lparam.weight = 1f;
		lparam.leftMargin = 10;
		linlayout.addView(btAdd, linlayout.getChildCount() );
		
		linlayout.setBackground(shape);

		ll.addView(linlayout, ll.getChildCount());
		
		android.widget.CheckBox cb = new android.widget.CheckBox( tv.getContext() );
		cb.setText( "Tags bis auf Widerruf" );
		cb.setChecked(BloodPreasurePreferenceActivity.isMarkTagsAsUntilRevoke());
		cb.setOnCheckedChangeListener(checkBoxListenerMarkUntilRevoke);
		LinearLayout.LayoutParams lpcbaram = (LinearLayout.LayoutParams)cb.getLayoutParams();
		if( lpcbaram == null )
			cb.setLayoutParams( (lpcbaram = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1)) );
		lpcbaram.gravity = Gravity.CENTER_HORIZONTAL;
		ll.addView(cb, ll.getChildCount() );
		
		
	}

	private void makeDateTimeFields() {
		DatePicker dt = new DatePicker(context);
		if( dt.getLayoutParams() == null )
			dt.setLayoutParams(ViewFactory.createDefaultLayout());
		dt.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
		((LinearLayout.LayoutParams)dt.getLayoutParams()).weight = 1f;
		tv.setTag(dt);
		dt.setBackground(shape);
		
		TimePicker tp = new TimePicker(context);
		tp.setIs24HourView(true);
		if( tp.getLayoutParams() == null )
			tp.setLayoutParams(ViewFactory.createDefaultLayout());
		tp.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
		((LinearLayout.LayoutParams)tp.getLayoutParams()).weight = 1f;
		dt.setTag(tp);
		tp.setBackground(shape);
		tp.setOnTimeChangedListener( new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				
				java.util.Date d = (java.util.Date)element.get(BloodPreasure.COL_IDX_DATUM);
				changeListener.dataChanged( element, page, (java.util.Date)at.gepa.lib.tools.time.TimeTool.makeDate( d, hourOfDay, minute) );				
			}} );
		
		tv.setVisibility(View.GONE);
		
		ll.addView(dt, ll.getChildCount() );
		ll.addView(tp, ll.getChildCount() );
	}

	private boolean setToField(IElement _element, EditText tv, int _page) {
		String key = _element.getTitle( page );
		Object o = _element.get(key);
		if( o instanceof java.util.Date || _element.getType(page) == java.sql.Date.class || _element.getType(page) == java.util.Date.class)
		{
			if( tv.getVisibility() != View.VISIBLE )
			{
				java.util.Date date = (java.util.Date)_element.get( page );
				DatePicker dt = (DatePicker)tv.getTag();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				dt.init( cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {
					
					@Override
					public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						java.util.Date d = (java.util.Date)element.get(BloodPreasure.COL_IDX_DATUM);
						changeListener.dataChanged( element, page, (java.util.Date) at.gepa.lib.tools.time.TimeTool.makeDate( d, year, monthOfYear, dayOfMonth) );				
					}
				} );
				
				TimePicker tp = (TimePicker) dt.getTag();
				tp.setCurrentHour(  cal.get(Calendar.HOUR_OF_DAY) );
				tp.setCurrentMinute( cal.get(Calendar.MINUTE) );
			}
			else
				o = at.gepa.lib.tools.time.TimeTool.toDateTimeString( (java.util.Date)o );
		}
		if( o == null )
		{
			o = "";
		}
		tv.setText( o.toString() );
		return o.toString().isEmpty();
	}


	public Object fromFieldtoElement(int page, IElement element, EditViewHolder editViewHolder) {

		return setToElement( element, editViewHolder, page );
	}
	
	private Object setToElement(IElement element, EditViewHolder editViewHolder, int page) {
		Object o = editViewHolder.getValue(page);
		
		if( element.getType(page) != String.class )
		{
			if( element.getType(page) == Integer.class )
			{
				o = Integer.parseInt(o == null ? "0" : o.toString());
			}
			else if( element.getType(page) == java.sql.Date.class || element.getType(page) == java.util.Date.class )
			{
				
				java.util.Date value = editViewHolder.getDate(page);
				
				o = value;
			}
			else if( element.getType(page) == Float.class )
			{
				o = Float.parseFloat(o == null ? "0" : o.toString());
			}
			else if( element.getType(page) == Double.class )
			{
				o = Double.parseDouble(o == null ? "0" : o.toString());
			}
			else if( element.getType(page) == Medication.class )
			{
				DailyMedicationView view = (DailyMedicationView)this.tv.getTag();
				o = view.getMedication().toString();
			}
		}
		return element.put( element.getTitle(page), o);
		
	}

	public static void setMarkTagsAsUntilRevoke(OnCheckedChangeListener onClickListener) {
		checkBoxListenerMarkUntilRevoke = onClickListener;
	}

}
