package at.gepa.tools2;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public abstract class DoubleClickListener implements OnItemClickListener {

    private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

    long lastClickTime = 0;

    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA)
        {
        	onSingleClick(parent, view, position, id);
        } else {
        	onDoubleClick(parent, view, position, id);
        }
        lastClickTime = clickTime;
    }

    public abstract void onSingleClick(AdapterView<?> parent, View view, int position, long id);
    public abstract void onDoubleClick(AdapterView<?> parent, View view, int position, long id);
}