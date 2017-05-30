package at.gepa.androidlib.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DialogMessageBox
{
	public static void showMessage(String title, String msg, Context context )
	{
		if( context == null ) return;
		if( title == null )
		{
			title = extractTitle(context);
		}
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(title);
			builder.setMessage(msg);
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
	
			// Must call show() prior to fetching text view
			TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
			messageView.setGravity(Gravity.CENTER);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static interface EndEditListener
	{
		public void textChanged( String t[] );
	}
	public static void simpleEdit(String title, View view, Context context, final EndEditListener listener )
	{
		if( context == null ) return;
		if( listener == null ) return;
		if( title == null )
		{
			title = extractTitle(context);
		}
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(title);

			builder.setView(view);
			// Set up the buttons
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	listener.textChanged( new String[]{"OK"} );
			    	dialog.dismiss();
			    }
			});
			builder.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        dialog.cancel();
			    }
			});

			builder.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void simpleEdit(String title, String slabel, String defText, Context context, final EndEditListener listener )
	{
		String la [] = new String[]{slabel};
		String dta[] = new String[] {defText};
		simpleEdit(title, la, dta, context, listener );
	}
	public static void simpleEdit(String title, String slabel[], String defText[], Context context, final EndEditListener listener )
	{
		if( context == null ) return;
		if( listener == null ) return;
		if( title == null )
		{
			title = extractTitle(context);
		}
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(title);

			
			final LinearLayout llVertical = new LinearLayout(context);
			llVertical.setOrientation(LinearLayout.VERTICAL);
			llVertical.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			final int elementCount = slabel.length;
			for( int i=0; i < slabel.length; i++ )
			{
				TextView label = new TextView(context);
				label.setText(slabel[i] + ": ");
				// Set up the input
				EditText input = new EditText(context);
				// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
				input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				input.setText(defText[i]);
				input.setId(1000+i);
				
				LinearLayout ll = new LinearLayout(context);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				ll.addView( label, ll.getChildCount() );
				ll.addView( input, ll.getChildCount() );
				llVertical.addView(ll, llVertical.getChildCount());
			}
			builder.setView(llVertical);
			// Set up the buttons
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	String [] sa = new String[elementCount];
			    	for( int id = 0; id < elementCount; id++)
			    	{
			    		EditText input = (EditText)llVertical.findViewById(1000+id);
			    		sa[id] = input.getText().toString();
			    	}
			    	listener.textChanged(sa);
			    }
			});
			builder.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        dialog.cancel();
			    }
			});

			AlertDialog dialog = builder.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private static String extractTitle(Context context) {
		Activity a = (Activity)context;
		
		PackageManager pm = context.getPackageManager();
		ComponentName compName = a.getComponentName();
		ActivityInfo activityInfo;
		String title = null;
		try {
			activityInfo = pm.getActivityInfo( compName, PackageManager.GET_META_DATA);
			title = activityInfo.loadLabel(pm).toString();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			title = context.getPackageManager().getPackageInfo("at.gepa.phone", PackageManager.GET_CONFIGURATIONS).packageName;
//			
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
		return title;
	}
	
	public static class EndDialogListener implements OnClickListener
	{
		private boolean clickResult;
		public boolean getClickResult() {
			return clickResult;
		}
		public void setClickResult(boolean okResult) {
			this.clickResult = okResult;
		}
		public EndDialogListener()
		{
			setClickResult(false);
		}
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			setClickResult(true);
		}
	}
	public static boolean askQquestion(String title, String message, Context context) {
		EndDialogListener okListener = new EndDialogListener();
		return DialogMessageBox.askQquestion( title, message, context, okListener );
	} 
	public static boolean askQquestion(String title, String message, Context context, EndDialogListener okListener) {
		return DialogMessageBox.askQquestion( title, message, context, okListener, null );
	} 
	public static boolean askQquestion(String title, String message, Context context, EndDialogListener oklistener, EndDialogListener cancelListener) {
		boolean ret = false;
		if( context == null ) return ret;
		if( title == null )
		{
			title = extractTitle(context);
		}
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(title);
			builder.setMessage(message);
			builder.setPositiveButton("Ja", oklistener );
			builder.setNegativeButton("Nein", cancelListener );
			AlertDialog dialog = builder.show();
	
			// Must call show() prior to fetching text view
			TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
			messageView.setGravity(Gravity.CENTER);
			ret = oklistener.getClickResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	
	public static void sendBroadcastMessage( Context context, String message )
	{
		Intent intent = new Intent();
	    intent.setPackage(context.getPackageName());
	    intent.setAction("showtoast");
	    Bundle extra = new Bundle();
	    if( message != null )
	    	extra.putString("title", message);
        intent.putExtras(extra);
	    context.sendBroadcast(intent);

	}
}
