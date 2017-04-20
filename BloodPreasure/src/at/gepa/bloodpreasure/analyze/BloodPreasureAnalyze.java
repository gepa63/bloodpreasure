package at.gepa.bloodpreasure.analyze;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.graphics.Color;
import at.gepa.lib.model.medicine.Medication;

//analyse seiten:
//http://www.pkdcure.de/index.php?page=bluthochdruck&gclid=Cj0KEQiAxrW2BRCFidKbqKyq1YEBEiQAnMDWxhY_2oycJX_rNXksFXombvO8qJJanyxIVLgB02tuC3waAm4l8P8HAQ
//http://nierenrechner.de/egfr-formeln/umrechnungsformeln.html
//https://www.blutdruckdaten.de/lexikon/blutdruck-normalwerte.html
//http://www.diabetes-ratgeber.net/Blutzucker/Blutzucker-Masseinheiten-27868.html
//http://bluthochdruck-kompakt.de/ideale-blutdruckwerte/
//http://www.bluthochdruck-hilfe.net/hormonanalyse-bluthochdruck.html



public class BloodPreasureAnalyze 
{
	public static boolean SHOW_ANALYZE_ON_MAINSCREEN = true;
	public static final int ABS_MIN = 40;
	public static final int ABS_MAX = 280;
	
//	private static int AGE = 0;
	private static Date BIRTHDAY;
	private static float GEWICHT = 0f;
	private static Medication MEDICATION = new Medication();
	private static String FEMALE =  "Weiblich";
	private static int HUEFTE = 0;
	private static int TAILE = 0;
	
	private static String GESCHLECHT = "Männlich";
	
	public static void setMedication(String med)
	{
		MEDICATION = Medication.createInstance(med);
	}
	public static int getAge() {
		if( BIRTHDAY == null )
			setBirthday( (String)null);
		
		return at.gepa.lib.tools.time.TimeTool.getAge(BIRTHDAY);
	}
	public static String getMedication() {
		return MEDICATION.toString();
	}
	public static Medication getMedicationObject() {
		return MEDICATION;
	}
	public static float getGewicht() {
		return GEWICHT;
	}
	public static void setGewicht(float g) {
		GEWICHT = g;
	}
	public static int GROESSE 					= 0;
	public static int getGroesse() {
		return GROESSE;
	}
	public static void setGroesse(int g) {
		GROESSE = g;
	}
	
	private int syst;
	private int diast;
	private int txtColor;
	private String text;
	private int defColor;

	public static final int COLOR_LOW_PREASURE			= Color.parseColor("#FF00AA88");
	public static final int COLOR_OPTIMAL				= Color.parseColor("#0000FF");//#ddffdd");
	public static final int COLOR_NORMAL				= Color.parseColor("#FFCC6600"); //"#FFFABA33");
	
	public static final int COLOR_PREHYPERTORNIE		= Color.parseColor("#FFFF3333");//ffba0aff"); //CC9900");//#CC9900");
	
	public static final int COLOR_HYPERTORNIE_LEICHT	= Color.parseColor("#ffba0a99"); //#ffba0a99
	public static final int COLOR_HYPERTORNIE_MITTEL	= Color.parseColor("#ff0cfa");//#ffb02aee");
	public static final int COLOR_HYPERTORNIE_SCHWER	= Color.parseColor("#ff0cfaff");
	public static final int COLOR_ISOLIERTE_HYPERTORNIE = Color.parseColor("#aaaaaa");
	public static final int COLOR_ERROR					= Color.RED;

	public static final String TEXT_BLOODPREASURE_LOW		= "Niedriger Blutdruck";
	public static final String TEXT_BLOODPREASURE_OPTIMAL	= "Optimaler Blutdruck";
	public static final String TEXT_BLOODPREASURE_NORMAL	= "Normaler Blutdruck";
	public static final String TEXT_BLOODPREASURE_PRAEH		= "Prä-Hypertonie";
	public static final String TEXT_BLOODPREASURE_LIGHTH	= "Leichte Hypertonie";
	public static final String TEXT_BLOODPREASURE_MIDDLEH	= "Mittelschwere Hypertonie";
	public static final String TEXT_BLOODPREASURE_HEAVYH	= "Schwere Hypertonie";
	public static final String TEXT_BLOODPREASURE_ISOLIERT	= "Isolierte systolische Hypertonie";
	public static final String TEXT_BLOODPREASURE_FEHLER	= "Blutdruck-Fehler";

	
	private class EvalInfo
	{
		public EvalInfo(int m, int ma, String t, int col)
		{
			sys = m;
			dia = ma;
			text = t;
			color = col;
		}
		public int sys;
		public int dia;
		public String text;
		public int color;
		public boolean isInRegion(int syst, int diast) {
			if( this.sys != 0 )
			{
				if( this.sys < 0 )
				{
					if( !(syst >= Math.abs(sys)) )
						return false;
				}
				else if( syst >= sys )
					return false;
			}			
			if( dia == 0 ) return true;
			if( this.dia < 0 )
			{
				if( !(diast >= Math.abs(dia)) )
					return false;
			}
			return diast < dia;
		}
	}
	
	private ArrayList<EvalInfo> evalValues;
	public BloodPreasureAnalyze(int syst, int diast)
	{
		this(syst, diast, COLOR_OPTIMAL );// Color.GREEN);
	}	
	public BloodPreasureAnalyze(int syst, int diast, int defColor)
	{
		this.syst = syst;
		this.diast = diast;
		this.defColor = defColor;
		this.txtColor = COLOR_ERROR;
		this.text = TEXT_BLOODPREASURE_FEHLER;
		evalNew();
	}
	
	private void fillEvalsValues() {
		evalValues = new ArrayList<BloodPreasureAnalyze.EvalInfo>();

		if( isFemale() )
		{
			evalValues.add(new EvalInfo(100, 60, TEXT_BLOODPREASURE_LOW, COLOR_LOW_PREASURE) );
		}
		else
		{
			evalValues.add(new EvalInfo(110, 60, TEXT_BLOODPREASURE_LOW, COLOR_LOW_PREASURE) );
		}
		evalValues.add(new EvalInfo(120, 80, TEXT_BLOODPREASURE_OPTIMAL, defColor) );
		evalValues.add(new EvalInfo(130, 85, TEXT_BLOODPREASURE_NORMAL, COLOR_NORMAL) );
		evalValues.add(new EvalInfo(140, 90, TEXT_BLOODPREASURE_PRAEH, COLOR_PREHYPERTORNIE) );

		boolean addDefault = false;
		if( getAge() <= 0 )
		{
			addDefault = true;
		}
		else
		{
			if( isFemale() )
			{
				if( getAge() >= 65 && getAge() <= 74)
				{
					evalValues.add(new EvalInfo(167, 0 /*100*/, TEXT_BLOODPREASURE_LIGHTH, COLOR_HYPERTORNIE_LEICHT) );
					evalValues.add(new EvalInfo(167, 0 /*110*/, TEXT_BLOODPREASURE_MIDDLEH, COLOR_HYPERTORNIE_MITTEL) );
					evalValues.add(new EvalInfo(-167, 0 /*-110*/, TEXT_BLOODPREASURE_HEAVYH, COLOR_HYPERTORNIE_SCHWER) );
				}
				else
				{
					addDefault = true;
				}
			}
			else
			{
				if( getAge() >= 55 && getAge() <= 64 )
				{
					evalValues.add(new EvalInfo(148, 0 /*100*/, TEXT_BLOODPREASURE_LIGHTH, COLOR_HYPERTORNIE_LEICHT) );
					evalValues.add(new EvalInfo(168, 0 /*110*/, TEXT_BLOODPREASURE_MIDDLEH, COLOR_HYPERTORNIE_MITTEL) );
					evalValues.add(new EvalInfo(-168, 0 /*-110*/, TEXT_BLOODPREASURE_HEAVYH, COLOR_HYPERTORNIE_SCHWER) );
				}
				else if( getAge() >= 65 && getAge() <= 74 )
				{
					evalValues.add(new EvalInfo(159, 0 /*100*/, TEXT_BLOODPREASURE_LIGHTH, COLOR_HYPERTORNIE_LEICHT) );
					evalValues.add(new EvalInfo(159, 0 /*110*/, TEXT_BLOODPREASURE_MIDDLEH, COLOR_HYPERTORNIE_MITTEL) );
					evalValues.add(new EvalInfo(-159, 0 /*-110*/, TEXT_BLOODPREASURE_HEAVYH, COLOR_HYPERTORNIE_SCHWER) );
				}
				else
				{
					addDefault = true;
				}
			}
		}
		if( addDefault )
		{
			evalValues.add(new EvalInfo(160, 100, TEXT_BLOODPREASURE_LIGHTH, COLOR_HYPERTORNIE_LEICHT) );
			evalValues.add(new EvalInfo(180, 110, TEXT_BLOODPREASURE_MIDDLEH, COLOR_HYPERTORNIE_MITTEL) );
			evalValues.add(new EvalInfo(-180, -110, TEXT_BLOODPREASURE_HEAVYH, COLOR_HYPERTORNIE_SCHWER) );
		}
		evalValues.add(new EvalInfo( -140, 90, TEXT_BLOODPREASURE_ISOLIERT, COLOR_ISOLIERTE_HYPERTORNIE) );
		evalValues.add(new EvalInfo(0, 0, TEXT_BLOODPREASURE_FEHLER, COLOR_ERROR) );
	}
	public void evalNew()
	{
		fillEvalsValues();
		for( int i=0; i < evalValues.size()-1; i++ )
		{
			if( evalValues.get(i).isInRegion(syst, diast) )
			{
				text = evalValues.get(i).text;
				txtColor = evalValues.get(i).color;
				return;
			}
		}
		text = evalValues.get( evalValues.size()-1 ).text;
		txtColor = evalValues.get( evalValues.size()-1 ).color;
	}	
	
	@Deprecated
	public void eval()
	{
		if( syst < 70 && diast < 66 )
		{
			text = TEXT_BLOODPREASURE_LOW;
			txtColor = COLOR_LOW_PREASURE;
		}
		else if( syst < 120 && diast < 80 )
		{
			text = TEXT_BLOODPREASURE_OPTIMAL;
			txtColor = defColor;
		}
		else if( syst < 130 && diast < 85 )
		{
			text = TEXT_BLOODPREASURE_NORMAL;
			txtColor = COLOR_NORMAL;
		}
		else if( syst <= 139 && diast <= 89 )
		{
			text = TEXT_BLOODPREASURE_PRAEH;
			txtColor = COLOR_PREHYPERTORNIE;//#FFFFBB33"); //Orange
		}
		else if( syst <= 159 && diast <= 99 )
		{
			text = TEXT_BLOODPREASURE_LIGHTH;
			txtColor = COLOR_HYPERTORNIE_LEICHT;
		}
		else if( syst < 179 && diast < 109 )
		{
			text = TEXT_BLOODPREASURE_MIDDLEH;
			txtColor = COLOR_HYPERTORNIE_MITTEL;
		}
		else if( syst >= 180 && diast >= 110 )
		{
			text = TEXT_BLOODPREASURE_HEAVYH;
			txtColor = COLOR_HYPERTORNIE_SCHWER;
		}
		else //if( syst < diast && syst >= ABS_MIN )
		{
			text = TEXT_BLOODPREASURE_FEHLER;
			txtColor = Color.RED;
		}
			
	}
	public CharSequence analyzeText() {
		return this.text;
	}
	public int analyzeColor() {
		return this.txtColor;
	}
	public static void setShowAnalyze(boolean b) {
		SHOW_ANALYZE_ON_MAINSCREEN = b;
	}
	
	public static String getGeschlecht() {
		return GESCHLECHT;
	}
	public static void setGeschlecht(String s) {
		GESCHLECHT = s;
	}
	public static boolean isFemale() 
	{
		return GESCHLECHT.equals(FEMALE);
	}
	
	public static String getBirthdayString() 
	{
		if( BIRTHDAY == null )
			setBirthday( (String)null);
		return at.gepa.lib.tools.time.TimeTool.toDateStringUS(BIRTHDAY);
	}
	public static void setBirthday(String sDateUS ) 
	{
		if( sDateUS == null || sDateUS.isEmpty() )
			sDateUS = "01.01.2000";
		if( sDateUS.contains("-") )
			BIRTHDAY = at.gepa.lib.tools.time.TimeTool.toDate(sDateUS, at.gepa.lib.tools.time.TimeTool.TEMPLATE_DATE_US);
		else
			BIRTHDAY = at.gepa.lib.tools.time.TimeTool.toDate(sDateUS, at.gepa.lib.tools.time.TimeTool.TEMPLATE_DATE);
	}
	public static void setBirthday(Calendar date) {
		BIRTHDAY = date.getTime();
	}
	public static Calendar getBirthday() {
		if( BIRTHDAY == null )
			setBirthday((String)null);
		Calendar cal = Calendar.getInstance();
		cal.setTime(BIRTHDAY);
		return cal;
	}
	public static float getBMI()
	{
		float f = GROESSE;
		if( f == 0 ) return 0f;
		f /= 100; //calc in meter
		if( GEWICHT == 0 ) return 0f;
		return ((float)GEWICHT) / (f * f);
	}
	public static String getBMIAnalyse() 
	{
		float bmi = getBMI();
		if( bmi == 0f) return "Daten fehlen";
		String ret = "";
		boolean addInfo = (bmi > 25);
		
		if( isFemale() )
		{
			if( bmi < 19 )
				ret = "Untergewicht";
			else if( bmi <= 24 )
				ret = "Normalgewicht";
			else if( bmi <= 30 )
				ret = "Übergewicht";
			else if( bmi <= 34.9 )
				ret = "Adipositas Grad I";
			else if( bmi <= 39.9 )
				ret = "Adipositas Grad II";
			else
				ret = "Starke Adipositas Grad III";
		}
		else
		{
			if( bmi < 20 )
				ret = "Untergewicht";
			else if( bmi <= 25 )
				ret = "Normalgewicht";
			else if( bmi <= 30 )
				ret = "Übergewicht";
			else if( bmi <= 34.9 )
				ret = "Adipositas Grad I";
			else if( bmi <= 39.9 )
				ret = "Adipositas Grad II";
			else
				ret = "Starke Adipositas Grad III";
		}
		String aret = String.format(Locale.GERMAN, "%s", ret); 
		if( addInfo )
		{
			int age = getAge();
			int min = 0;
			int max = 0;
			
			if( age <= 24 )
			{				
				min = 19; 
				max = 24;
			}
			else if( age <= 34 )
			{				
				min = 20; 
				max = 25;
			}
			else if( age <= 44 )
			{				
				min = 21; 
				max = 26;
			}
			else if( age <= 54 )
			{				
				min = 22; 
				max = 27;
			}
			else if( age <= 64 )
			{				
				min = 23; 
				max = 28;
			}
			else 
			{				
				min = 24; 
				max = 29;
			}
			float g = ((float)GROESSE) / 100;
			float idealGewicht = max * (g*g);
			aret += "!\n" + String.format("sollte sein %d-%d=%d Kg", min, max, Math.round(idealGewicht) );
		}
		return aret;
	}
	
	static private boolean ROUND_QUARTERS = true;
	public static boolean getRoundQuarters() {
		return ROUND_QUARTERS;
	}
	public static void setRoundQuarters(boolean b) {
		ROUND_QUARTERS = b;
	}
	public static int calcKalorienGrundVerbrauch() 
	{
		float kgv = 0f;
		if( isFemale() )
		{
			kgv = (float)((getGewicht() * 9.6) + (getGroesse() * 1.8) + (getAge() * 4.7) + 655.1f);
		}
		else
		{
			kgv = (float)( (getGewicht() * 13.7) + (getGroesse() * 5) + (getAge() * 6.8) + 66.47);
		}
		return Math.round(kgv);
	}
	
	public static int getHuefte() {
		return HUEFTE;
	}
	public static void setHuefte(int h) {
		HUEFTE = h;
	}
	
	public static int getTaile() {
		return TAILE;
	}
	public static void setTaile(int h) {
		TAILE = h;
	}
	public static float getTHV() {
		float t = BloodPreasureAnalyze.getTaile();
		float h = BloodPreasureAnalyze.getHuefte();
		if( h == 0f) return 0f;
		return t / h;
	}
	public static String getTHVAnalyzeText()
	{
		String ret = "fehlende Daten";
		float thv = getTHV();
		if( thv == 0 ) return ret;
		float soll = 0f;
		if( isFemale() )
		{
			soll = 0.8f;
			if( thv < 0.8 )
				ret = "Normalverhältnis";
			else if( thv <= 0.84 )
				ret = "verbreiterte Taile";
			else 
				ret = "apfelförmiges Übergewicht";
		}
		else
		{
			soll = 0.9f;
			if( thv < 0.9 )
				ret = "Normalverhältnis";
			else if( thv <= 0.99 )
				ret = "verbreiterte Taile";
			else 
				ret = "apfelförmiges Übergewicht";
		}
		ret += String.format(" (%.2f/%.2f)", thv, soll);
		return ret;
	}
	public static float getKVI() {
		float kvi =0f;
		float h = getHuefte();
		if( h == 0f ) return kvi;
		float t = getTaile();
		kvi = (t * t) / h;
		return kvi;
	}
	public static String getKVIAnalyzeText()
	{
		String ret = "fehlende Daten";
		float kvi = getKVI();
		if( kvi == 0 ) return ret;
		float soll = 0f;
		if( isFemale() )
		{
			soll = 59;
			if( kvi < 60 )
				ret = "Normal";
			else if( kvi <= 74 )
				ret = "erhötes Krankheitssisiko";
			else 
				ret = "hohes Krankheitssisiko";
		}
		else
		{
			soll = 74;
			if( kvi < 75 )
				ret = "Normal";
			else if( kvi <= 84 )
				ret = "erhötes Krankheitssisiko";
			else 
				ret = "hohes Krankheitssisiko";
		}
		ret += String.format(" (%.2f/%.2f)", kvi, soll);
		return ret;
	}
	public static float getTGV() {
		float tgv = 0f;
		float g = getGroesse();
		if( g == 0 ) return 0f;
		float t = getTaile();
		tgv = t/g;
		return tgv;
	}
	public static String getTGVAnalyzeText() 
	{
		String ret = "fehlende Daten";
		float tgv = getTGV();
		if( tgv == 0 ) return ret;
		int age = getAge();
		float minv = 0;

		if( age < 40 )
		{
			minv = 0.5f;
		}
		else if( age <= 50 )
		{
			minv = 0.6f;
		}
		else
		{
			minv = 0.6f;
		}
		if( tgv <= minv )
			ret = "Normal";
		else 
			ret = "erhötes Krankheitssisiko";
		ret += String.format(" (%.2f/%.2f)", tgv, minv);
		return ret;
	}
	public static void setMedicationObj(Medication medicationPerDay) {
		MEDICATION = medicationPerDay;
	}
}
