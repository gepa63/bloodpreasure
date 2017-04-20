package at.gepa.tools2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.app.ShareCompat;
import android.view.Gravity;
import android.widget.TextView;
import at.gepa.androidlib.SystemInfo;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.ui.BloodPreasureInfoActivity;

public class BloodPreasureDialogMessageBox
{
	public static void ShowMessage(String msg )
	{
		ShowMessage(msg,null);
	}	
	public static void ShowMessage(String msg, Context context )
	{
		if( context ==  null ) return;

		String title = SystemInfo.getApplicationName(context);
		try {
			title = context.getPackageManager().getPackageInfo("at.gepa.phone", PackageManager.GET_CONFIGURATIONS).packageName;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
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
	public static void ShowMessage_SendMailTo(Context context, String msg, final String[] addresses, final String subject, final String mailMsg) {
		String title = SystemInfo.getApplicationName(context);
		try {
			title = context.getPackageManager().getPackageInfo("at.gepa.phone", PackageManager.GET_CONFIGURATIONS).packageName;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(title);
			builder.setMessage(msg);
			builder.setPositiveButton("OK", null);
			builder.setNegativeButton("E-Mail senden", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final Intent intent = ShareCompat.IntentBuilder
	                        .from(MainActivityGrid.self)
	                        .setEmailTo(addresses)
	                        .setType("application/txt")
	                        .setSubject(subject)
	                        .setText(mailMsg)
	                        .setChooserTitle("E-Mail senden mit ...")
	                        .createChooserIntent()
	                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
	                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

					MainActivityGrid.self.startActivity(intent);					
				}
			});
			AlertDialog dialog = builder.show();
	
			// Must call show() prior to fetching text view
			TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
			messageView.setGravity(Gravity.CENTER);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void ShowMessage_OnlyPRO(Context context) {
		String msg = "Leider nur in der PRO Version enthalten\n\nSenden Sie ein E-Mail an " + 
				BloodPreasureInfoActivity.getContact() + " um zur PRO Version umzusteigen.";

		String addresses[] = new String[]{ BloodPreasureInfoActivity.getContact2() };
		
		String subject = "Upgrade zur PRO Version von Blutdruck!";
		
		String mailMsg = "Hallo Herr Payer,\n" +
        		"ich möchte gerne zur PRO Version upgraden.\nBitte sende Sie mir weitere Informationen zu.\n\nVG\n";
		
		ShowMessage_SendMailTo( context, msg, addresses, subject, mailMsg );
	}
}
