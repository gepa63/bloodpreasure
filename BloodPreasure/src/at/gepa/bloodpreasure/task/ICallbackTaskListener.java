package at.gepa.bloodpreasure.task;

import android.app.ProgressDialog;
import android.content.Context;
import at.gepa.net.IModel;

public interface ICallbackTaskListener {

	public ProgressDialog getProgressDialog();

	public Context getContext();

	public void setModelDone(boolean b);

	public void doUIRefresh();

	public IModel getDownloadModel();

	public Object _getSystemService(String powerService);

	public int getBloodPreasureCount();

	public void setListSelection(int i);

	public void _createDownloadTask();

	public IModel getUploadModel();

}
