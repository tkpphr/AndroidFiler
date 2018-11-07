package com.tkpphr.android.filer.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileUtils;
import com.tkpphr.android.filer.view.OnCopyFinishedListener;

import java.io.File;

public class CopyProgressDialog extends BaseFileTaskProgressDialog{
	private OnCopyFinishedListener onCopyFinishedListener;

	public static CopyProgressDialog newInstance(File dstFile,File srcFile) {

		Bundle args = new Bundle();
		args.putSerializable("dst_file",dstFile);
		args.putSerializable("src_file",srcFile);
		CopyProgressDialog fragment = new CopyProgressDialog();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if(context instanceof OnCopyFinishedListener){
			onCopyFinishedListener=(OnCopyFinishedListener)context;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
			if (activity instanceof OnCopyFinishedListener) {
				onCopyFinishedListener = (OnCopyFinishedListener) activity;
			}
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		if(getTargetFragment() instanceof OnCopyFinishedListener){
			onCopyFinishedListener=(OnCopyFinishedListener)getTargetFragment();
		}else if(getParentFragment() instanceof OnCopyFinishedListener){
			onCopyFinishedListener=(OnCopyFinishedListener)getParentFragment();
		}
		super.onCreate(savedInstanceState);
	}



	@Override
	public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
		getLoaderManager().destroyLoader(loader.getId());
		if(onCopyFinishedListener!=null){
			onCopyFinishedListener.onCopyFinished((File) getArguments().getSerializable("dst_file"),data);
		}
		onDismiss(getDialog());
	}

	@Override
	public void onLoaderReset(Loader<Boolean> loader) {

	}

	@Override
	public boolean executeFileTask() {
		return FileUtils.copy((File) getArguments().getSerializable("src_file"),(File)getArguments().getSerializable("dst_file"));
	}


	@Override
	protected int getLoaderId() {
		return getResources().getInteger(R.integer.filr_copy_file_task_loader_id);
	}

	@Override
	protected String getProgressMessage() {
		return getString(R.string.filr_copying);
	}

}
