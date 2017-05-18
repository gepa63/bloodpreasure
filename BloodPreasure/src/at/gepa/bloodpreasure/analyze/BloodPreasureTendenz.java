package at.gepa.bloodpreasure.analyze;

import java.util.ArrayList;

import android.graphics.Color;
import at.gepa.bloodpreasure.R;
import at.gepa.lib.model.BloodPreasure;

public class BloodPreasureTendenz {

	private float k;
	
	public BloodPreasureTendenz()
	{
		k = 0;
	}
	
	public String getValue() {
		String ret = "wird besser";
		if( isZero() )
			ret = "gleichbleibend";
		else if( getWorst() )
			ret = "wird schlechter";
		return ret;
	}


	private boolean getWorst() {
		return k > 0;
	}

	private boolean getBetter() {
		return k < 0;
	}

	private boolean isZero() {
		int x = (int)k;
		float div = (k - x) * 100;
		return Math.round(div) == 0;
	}

	public void _calculate(ArrayList<BloodPreasure> tendenzBase) {
		/*
		tendenzBase = new ArrayList<BloodPreasure>();
		tendenzBase.add(new BloodPreasure("01.10.2017 07:00", 140, 110, 61, "m10") );
		tendenzBase.add(new BloodPreasure("01.09.2017 07:00", 140, 100, 61, "m9") );
		tendenzBase.add(new BloodPreasure("01.08.2017 07:00", 136, 92, 61, "m8") );
		tendenzBase.add(new BloodPreasure("01.07.2017 07:00", 140, 88, 61, "m7") );
		tendenzBase.add(new BloodPreasure("01.06.2017 07:00", 128, 88, 61, "m6") );
		tendenzBase.add(new BloodPreasure("01.05.2017 07:00", 125, 85, 61, "m5") );
		tendenzBase.add(new BloodPreasure("01.04.2017 07:00", 120, 82, 61, "m4") );
		tendenzBase.add(new BloodPreasure("01.03.2017 07:00", 120, 80, 61, "m3") );
		tendenzBase.add(new BloodPreasure("01.02.2017 07:00", 110, 78, 61, "m2") );
		tendenzBase.add(new BloodPreasure("01.01.2017 07:00", 100, 75, 61, "m1") );
		*/
/*
		tendenzBase = new ArrayList<BloodPreasure>();
		tendenzBase.add(new BloodPreasure("01.10.2017 07:00", 115, 75, 61, "m10") );
		tendenzBase.add(new BloodPreasure("01.09.2017 07:00", 110, 78, 61, "m9") );
		tendenzBase.add(new BloodPreasure("01.08.2017 07:00", 120, 80, 61, "m8") );
		tendenzBase.add(new BloodPreasure("01.07.2017 07:00", 122, 82, 61, "m7") );
		tendenzBase.add(new BloodPreasure("01.06.2017 07:00", 128, 85, 61, "m6") );
		tendenzBase.add(new BloodPreasure("01.05.2017 07:00", 140, 88, 61, "m5") );
		tendenzBase.add(new BloodPreasure("01.04.2017 07:00", 136, 88, 61, "m4") );
		tendenzBase.add(new BloodPreasure("01.03.2017 07:00", 134, 92, 61, "m3") );
		tendenzBase.add(new BloodPreasure("01.02.2017 07:00", 140, 100, 61, "m2") );
		tendenzBase.add(new BloodPreasure("01.01.2017 07:00", 135, 97, 61, "m1") );
		*/
		
		float artihMittelSys = 0f;
		float artihMittelDia = 0f;
		float calcSys = 0f;
		float calcDia = 0f;
		
		k = 0f;
		if( tendenzBase.size() == 0 ) return;
		
		//float bewertungValues[] = new float[]{2.0f, 1.9f, 1.8f, 1.8f, 1.7f, 1.7f, 1.6f, 1.5f, 1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f, 1f, 1f, 0.95f, .95f, 0.95f, .9f, 0.92f, .92f};
		float bewertungValues[] = new float[]{1.2f, 1.18f, 1.16f, 1.15f, 1.14f, 1.13f, 1.12f, 1.1f, 1.1f, 1.05f, 1.05f, 1.03f, 1.02f, 1f, 1f, 1f, 0.95f, .95f, 0.95f, .9f, 0.92f, .92f};
		for( int i=0; i < tendenzBase.size(); i++ )
		{
			BloodPreasure bp = tendenzBase.get(i);
			
			float bewertung = (i >= bewertungValues.length ? (1f - i / 100f) : bewertungValues[i]);
			calcSys += bp.getSystolisch() * bewertung;
			calcDia += bp.getDiastolisch() * bewertung;
			
			artihMittelSys += bp.getSystolisch();
			artihMittelDia += bp.getDiastolisch();
		}
		
		artihMittelSys /= tendenzBase.size();
		artihMittelSys *= 1.12;
		artihMittelDia /= tendenzBase.size();
		artihMittelDia *= 1.12;
		
		calcSys /= tendenzBase.size();
		calcDia /= tendenzBase.size();
		
		k = (artihMittelSys - calcSys);
		if( k < 0 )
			k += (artihMittelDia - calcDia);
		
	}	
	public void calculate(ArrayList<BloodPreasure> tendenzBase) {
		int sumXiYi = 0;
		int sumYi = 0;
		int sumXQ = 0;
		int sumXi = 0;

		int sumXiYiDia = 0;
		int sumYiDia = 0;
		int sumXQDia = 0;
		int sumXiDia = 0;

		/*
		tendenzBase = new ArrayList<BloodPreasure>();
		tendenzBase.add(new BloodPreasure("01.10.2017 07:00", 140, 110, 61, "m10") );
		tendenzBase.add(new BloodPreasure("01.09.2017 07:00", 140, 100, 61, "m9") );
		tendenzBase.add(new BloodPreasure("01.08.2017 07:00", 136, 92, 61, "m8") );
		tendenzBase.add(new BloodPreasure("01.07.2017 07:00", 140, 88, 61, "m7") );
		tendenzBase.add(new BloodPreasure("01.06.2017 07:00", 128, 88, 61, "m6") );
		tendenzBase.add(new BloodPreasure("01.05.2017 07:00", 125, 85, 61, "m5") );
		tendenzBase.add(new BloodPreasure("01.04.2017 07:00", 120, 82, 61, "m4") );
		tendenzBase.add(new BloodPreasure("01.03.2017 07:00", 120, 80, 61, "m3") );
		tendenzBase.add(new BloodPreasure("01.02.2017 07:00", 110, 78, 61, "m2") );
		tendenzBase.add(new BloodPreasure("01.01.2017 07:00", 100, 75, 61, "m1") );
		*/
		/*
		tendenzBase = new ArrayList<BloodPreasure>();
		tendenzBase.add(new BloodPreasure("01.10.2017 07:00", 115, 75, 61, "m10") );
		tendenzBase.add(new BloodPreasure("01.09.2017 07:00", 110, 78, 61, "m9") );
		tendenzBase.add(new BloodPreasure("01.08.2017 07:00", 120, 80, 61, "m8") );
		tendenzBase.add(new BloodPreasure("01.07.2017 07:00", 122, 82, 61, "m7") );
		tendenzBase.add(new BloodPreasure("01.06.2017 07:00", 128, 85, 61, "m6") );
		tendenzBase.add(new BloodPreasure("01.05.2017 07:00", 140, 88, 61, "m5") );
		tendenzBase.add(new BloodPreasure("01.04.2017 07:00", 136, 88, 61, "m4") );
		tendenzBase.add(new BloodPreasure("01.03.2017 07:00", 134, 92, 61, "m3") );
		tendenzBase.add(new BloodPreasure("01.02.2017 07:00", 140, 100, 61, "m2") );
		tendenzBase.add(new BloodPreasure("01.01.2017 07:00", 135, 97, 61, "m1") );
		*/

		float bewertung [] = new float [tendenzBase.size()];
		for( int b = 0; b < tendenzBase.size(); b++ )
		{
			//bewertung[b] = (((float)b)/7) * (((float)b)/2);
			
			bewertung[b] = 1.1f;
			
			//bewertung[b] = (float)Math.sqrt( (tendenzBase.size()- b) + 0.4 );
			//bewertung[b] = 1 / (float)Math.sqrt( (tendenzBase.size()- b) ) * 50;//100 ;
			//bewertung[b] = (float)Math.pow(( (tendenzBase.size()- b) + 0.4 ), 2);
		}
		
		
		for( int i=0; i < tendenzBase.size(); i++ )
		{
			BloodPreasure bp = tendenzBase.get(i);
			sumXiYi += bp.getSystolisch() * bewertung[i] * bewertung[i];
			sumXQ += bewertung[i] * bp.getSystolisch() * bp.getSystolisch();
			sumXi += bewertung[i] * bp.getSystolisch();
			sumYi += bewertung[i];
		}
		for( int i=0; i < tendenzBase.size(); i++ )
		{
			BloodPreasure bp = tendenzBase.get(i);
			sumXiYiDia += bp.getDiastolisch() * bewertung[i] * bewertung[i];
			sumXQDia += bewertung[i] * bp.getDiastolisch() * bp.getDiastolisch();
			sumXiDia += bewertung[i] * bp.getDiastolisch();
			sumYiDia += bewertung[i];
		}
		
		k = (float)(tendenzBase.size() * sumXiYi) - (float)(sumXi * sumYi);
		float div = (float)(tendenzBase.size() * sumXQ) - (float)(sumXi * sumXi); 
		if( div == 0f )
			div = 1f;
		k /= div;

		float kDia = (float)(tendenzBase.size() * sumXiYiDia) - (float)(sumXiDia * sumYiDia);
		float divDia = (float)(tendenzBase.size() * sumXQDia) - (float)(sumXiDia * sumXiDia); 
		if( divDia == 0f )
			divDia = 1f;
		kDia /= divDia;
		
		k += kDia;
		
		int x = (int)(k * 10);
		k = x / 10f;
	}

	public int getImage() {
		int _default = R.drawable.daumen_neutral_small;
		if( getBetter() )
			return R.drawable.daumen_hoch_small;
		else if( getWorst() )
			 return R.drawable.daumen_runter_small;
		return _default;
	}

	public int analyzeColor() 
	{
		if( isDramatic() )
			return Color.RED;
		if( getBetter() )
			return Color.BLUE;
		return Color.BLACK;
	}

	private boolean isDramatic() {
		return k >= 3;
	}


}
