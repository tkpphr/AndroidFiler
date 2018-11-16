/*
   Copyright 2018 tkpphr

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
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
