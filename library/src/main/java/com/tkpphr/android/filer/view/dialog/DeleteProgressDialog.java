package com.tkpphr.android.filer.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileUtils;
import com.tkpphr.android.filer.view.OnDeleteFinishedListener;

import java.io.File;

public class DeleteProgressDialog extends BaseFileTaskProgressDialog{
	private OnDeleteFinishedListener onDeleteFinishedListener;
	
	public static DeleteProgressDialog newInstance(File file) {

		Bundle args = new Bundle();
		args.putSerializable("file",file);
		DeleteProgressDialog fragment = new DeleteProgressDialog();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if(context instanceof OnDeleteFinishedListener){
			onDeleteFinishedListener=(OnDeleteFinishedListener)context;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
			if (activity instanceof OnDeleteFinishedListener) {
				onDeleteFinishedListener = (OnDeleteFinishedListener) activity;
			}
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		if(getTargetFragment() instanceof OnDeleteFinishedListener){
			onDeleteFinishedListener=(OnDeleteFinishedListener)getTargetFragment();
		}else if(getParentFragment() instanceof OnDeleteFinishedListener){
			onDeleteFinishedListener=(OnDeleteFinishedListener)getParentFragment();
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
		getLoaderManager().destroyLoader(loader.getId());
		if(onDeleteFinishedListener!=null){
			onDeleteFinishedListener.onDeleteFinished((File) getArguments().getSerializable("file"),data);
		}
		onDismiss(getDialog());
	}

	@Override
	public void onLoaderReset(Loader<Boolean> loader) {

	}

	@Override
	protected int getLoaderId() {
		return getResources().getInteger(R.integer.filr_delete_file_task_loader_id);
	}

	@Override
	protected String getProgressMessage() {
		return getString(R.string.filr_deleting);
	}

	@Override
	public boolean executeFileTask() {
		return FileUtils.delete((File) getArguments().getSerializable("file"));
	}
}
