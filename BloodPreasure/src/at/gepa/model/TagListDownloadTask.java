package at.gepa.model;

import android.app.ProgressDialog;
import android.content.Context;
import at.gepa.bloodpreasure.MainActivityGrid;
import at.gepa.bloodpreasure.task.ICallbackTaskListener;
import at.gepa.bloodpreasure.task.SilentDownloadTask;
import at.gepa.files.CacheFile;
import at.gepa.lib.model.ITagsFileLoader;
import at.gepa.lib.model.TagModel;
import at.gepa.net.DataAccess;
import at.gepa.net.FileNameModel;
import at.gepa.net.IModel;


public class TagListDownloadTask extends SilentDownloadTask
implements ITagsFileLoader
{
	private String fileName;
	private DataAccess params;
	private static TagListDownloadTask self;
	
	public TagListDownloadTask(Context context) {
		super( getCallbackTaskListener(context) );
		self = this;
	}

	private static ICallbackTaskListener getCallbackTaskListener(Context context) {
		return new ICallbackTaskListener(){

			@Override
			public ProgressDialog getProgressDialog() {
				return null;
			}

			@Override
			public Context getContext() {
				return null;
			}

			@Override
			public void setModelDone(boolean b) {
			}

			@Override
			public void doUIRefresh() {
			}

			@Override
			public IModel getDownloadModel() {
				return self.getList();
			}

			@Override
			public Object _getSystemService(String powerService) {
				return null;
			}

			@Override
			public int getBloodPreasureCount() {
				return self.getList().size();
			}

			@Override
			public void setListSelection(int i) {
			}

			@Override
			public void _createDownloadTask() {
			}

			@Override
			public IModel getUploadModel() {
				return self.getList();
			}};
	}

	@Override
	public void load(String fileName, IModel tagModel) {
		this.fileName = fileName;
		setList( tagModel );
		readCache();
		
		FileNameModel fnm = new FileNameModel(this.fileName);
		fnm.setIsLocalFile(true);
		params = DataAccess.createInstance(fnm);
		
		if( params != null )
			super.execute(params);
	}
	
	public void saveCache() {
		CacheFile cf = new CacheFile(MainActivityGrid.self, "taglist_cached.txt");
		cf.clear();
		cf.writeTextFile(getList(), null);
		
	}
	public void readCache() {
		CacheFile cf = new CacheFile(MainActivityGrid.self, "taglist_cached.txt");
		if( cf.exists() )
			cf.readTextFile(getList(), null);
		
	}
	

	@Override
	public void save(String path, TagModel tagModel) {
		this.fileName = path;
		setList( tagModel );
		
		FileNameModel fnm = new FileNameModel(this.fileName);
		
		params = DataAccess.createInstance(fnm);
		
		super.execute(params);
		
	}
    @Override
    protected void onPostExecute(String result) {
    	super.onPostExecute(result);
    	saveCache();
    }
	
}
