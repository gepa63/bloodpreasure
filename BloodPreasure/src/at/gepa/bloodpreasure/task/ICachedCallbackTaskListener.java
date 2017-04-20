package at.gepa.bloodpreasure.task;

import at.gepa.net.IModel;

public interface ICachedCallbackTaskListener extends ICallbackTaskListener {

	public void saveCache(IModel list);
	public void readCache(IModel list);
}
