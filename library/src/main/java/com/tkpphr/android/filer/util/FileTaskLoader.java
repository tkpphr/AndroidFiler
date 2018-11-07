package com.tkpphr.android.filer.util;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class FileTaskLoader extends AsyncTaskLoader<Boolean>{
	private Boolean result;
	private FileTask fileTask;
	private boolean isStarted;

	public FileTaskLoader(Context context, FileTask fileTask) {
		super(context);
		this.result=null;
		this.fileTask=fileTask;
		this.isStarted=false;
	}

	@Override
	public Boolean loadInBackground() {
		return fileTask.executeFileTask();
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if(result!=null){
			deliverResult(result);
			return;
		}
		if(!isStarted || takeContentChanged()){
			forceLoad();
		}
	}

	@Override
	public void deliverResult(Boolean result) {
		this.result =result;
		super.deliverResult(result);
	}

	@Override
	public void forceLoad() {
		super.forceLoad();
		this.isStarted=true;
	}

	public interface FileTask{
		boolean executeFileTask();
	}
}
