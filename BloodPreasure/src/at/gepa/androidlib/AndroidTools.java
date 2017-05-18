package at.gepa.androidlib;

import java.util.Locale;

import android.content.ClipData.Item;
import android.content.Context;
//import android.support.v4.app.FragmentActivity;

public class AndroidTools {
	
	public enum ContentType {
		Text,
		Html,
		Pdf,
		Word;
		
		public static ContentType getType( String s )
		{
			s = s.toLowerCase(Locale.GERMAN); 
			if(s.endsWith(".txt") )
				return Text;
			else if(s.endsWith(".pdf") )
				return Pdf;
			else if(s.endsWith(".doc") )
				return Word;
			return Text;
		}
	}	

	/*
	public static void showKeyboard(FragmentActivity activity, boolean show) {
		View view = activity.getCurrentFocus();
		if (view != null) {  
		    InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		    if( show )
			    imm.showSoftInput(view, 0);//InputMethodManager.SHOW_IMPLICIT);
		    else
		    	imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//InputMethodManager.HIDE_IMPLICIT_ONLY);
		}		
	}
*/

	public static String getClipboardData(Context context, ContentType ct)
	{
		return getClipboardData(context, ct, -1);
	}	
	public static String getClipboardData(Context context, ContentType ct, int pos)
	{
		CharSequence t = null;
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
		    android.text.ClipboardManager clipboard = (android.text.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
	    	t = clipboard.getText();
		} else {
		    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		    android.content.ClipData clip = clipboard.getPrimaryClip();
		    if( clip != null )
		    {
		    	Item item = null;
		    	if( pos < 0 || pos >= clip.getItemCount() )
		    	{
			    	for( pos = clip.getItemCount()-1; pos >= 0; pos-- )
			    	{
			    		item = clip.getItemAt( pos );
			    		if( ct == ContentType.Html )
			    		{
				    		String cs = item.getHtmlText();
				    		if( cs != null && !cs.isEmpty() )
				    			break;
			    		}
			    		else
			    		{
				    		CharSequence cs = item.getText();
				    		if( cs != null && !cs.toString().isEmpty() )
				    			break;
			    		}
			    	}
		    	}
		    	else
		    	{
		    		item = clip.getItemAt( pos );
		    	}
		    	if( item != null )
		    	{
		    		if( ct == ContentType.Html )
		    			t = item.getHtmlText();
		    		else if( ct == ContentType.Text )
		    			t = item.getText();
		    	}
		    }
		}
		if( t == null )
			return "";
		return t.toString();
	}	

	public static void setClipboardData(Context context, String label, String text)
	{
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
		    android.text.ClipboardManager clipboard = (android.text.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
		    clipboard.setText(text);
		} else {
		    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE); 
		    android.content.ClipData clip = android.content.ClipData.newPlainText(label, text);
		    clipboard.setPrimaryClip(clip);
		}
		
	}
}
	
