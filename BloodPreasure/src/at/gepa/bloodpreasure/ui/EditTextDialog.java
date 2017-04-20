package at.gepa.bloodpreasure.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import at.gepa.bloodpreasure.R;

public class EditTextDialog {

	public static interface MyIChangeListener
	{
		void newEntry(EditTextDialog dlg);
		void editEntry(EditTextDialog dlg);
	}
	
	private LayoutInflater layoutInflater;
	private View promptsView;
	private String result;
	private String defText;
	private boolean okClicked;
	private Context context;
	private MyIChangeListener listener;

	public EditTextDialog( int resId, Context context, String t )
	{
		this(resId, "", context);
	}
	public EditTextDialog( int resId, String defText, Context context)
	{
		layoutInflater = (LayoutInflater)LayoutInflater.from(context);
		promptsView = layoutInflater.inflate(resId, null);
		result = "";
		this.defText = defText;
		okClicked = false;
		this.context = context;
		listener = null;
	}
	
	public void show()
	{
		final EditTextDialog _dlg = this;
		result = "";
		okClicked = false;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
		userInput.append(defText);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
			    okClicked = true;
				result = userInput.getText().toString();
				if( listener != null )
				{
					if( defText.isEmpty() )
						listener.newEntry(_dlg);
					else
						listener.editEntry(_dlg);
				}
			  }})
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();		
	}
	
	public String getResult()
	{
		return result;
	}
	public boolean isOKClicked()
	{
		return okClicked;
	}
	public void setListener(MyIChangeListener c) {
		this.listener = c;
	}

}
