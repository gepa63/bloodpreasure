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
		String ret = "optimal";
		if( isZero() )
			ret = "gleichbleibend";
		else if( isGreaterZero() )
			ret = "schlechter";
		return ret;
	}


	private boolean isGreaterZero() {
		return k > 0;
	}

	private boolean isLessZero() {
		return k < 0;
	}

	private boolean isZero() {
		int x = (int)k;
		float div = (k - x) * 100;
		return Math.round(div) == 0;
	}

	public void calculate(ArrayList<BloodPreasure> tendenzBase) {
		int sumXiYi = 0;
		int sumYi = 0;
		int sumXQ = 0;
		int sumXi = 0;
		
		float bewertung [] = new float [tendenzBase.size()];
		for( int b = 0; b < tendenzBase.size(); b++ )
		{
			bewertung[b] = (((float)b)/7) * (((float)b)/2);
			//bewertung[b] = b;
			bewertung[b] = 1;
		}
		
		
		for( int i=0; i < tendenzBase.size(); i++ )
		{
			BloodPreasure bp = tendenzBase.get(i);
			sumXiYi += bp.getSystolisch() * bewertung[i];
			sumXQ += bewertung[i] * bp.getSystolisch() * bp.getSystolisch();
			sumXi += bewertung[i] * bp.getSystolisch();
			sumYi += bewertung[i];
		}
		
		k = (tendenzBase.size() * sumXiYi) - (sumXi * sumYi);
		float div = (tendenzBase.size() * sumXQ) - (sumXi * sumXi); 
		k /= div;
		
	}

	public int getImage() {
		int _default = R.drawable.daumen_neutral_small;
		if( isLessZero() )
			return R.drawable.daumen_hoch_small;
		else if( isGreaterZero() )
			 return R.drawable.daumen_runter_small;
		return _default;
	}

	public int analyzeColor() 
	{
		if( isGreater(2) )
			return Color.RED;
		return Color.BLACK;
	}

	private boolean isGreater(int i) {
		return k > i;
	}


}
