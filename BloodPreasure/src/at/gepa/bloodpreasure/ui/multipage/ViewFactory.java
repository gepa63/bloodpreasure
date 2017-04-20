package at.gepa.bloodpreasure.ui.multipage;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ViewFactory {

	public static EditText createEditView(Context context) {
		android.widget.LinearLayout.LayoutParams params;
		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		params.setLayoutDirection(android.widget.LinearLayout.VERTICAL);
        params.setMargins(1, 1, 1, 1);
        
        EditText ev = new EditText(context );
        ev.setLayoutParams(params);
		
		return ev;
	}

	public static LayoutParams createDefaultLayout() {
		return createDefaultLayout(1);
	}
	public static LayoutParams createDefaultLayout(int margin) 
	{
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(margin, margin, margin, margin);
		return params;
	}

	public static LinearLayout createDefaultLinearLayout(Context context) {
		LinearLayout ll = new LinearLayout(context);
		ll.setLayoutParams(createDefaultLayout());
		ll.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
		return ll;
	}


}
