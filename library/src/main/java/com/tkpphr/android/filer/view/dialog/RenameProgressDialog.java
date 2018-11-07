package com.tkpphr.android.filer.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileUtils;
import com.tkpphr.android.filer.view.OnRenameFinishedListener;

import java.io.File;

public class RenameProgressDialog extends BaseFileTaskProgressDialog{
	private OnRenameFinishedListener onRenameFinishedListener;
	
	public static RenameProgressDialog newInstance(File srcFile,File dstFile) {
		Bundle args = new Bundle();
		args.putSerializable("src_file",srcFile);
		args.putSerializable("dst_file",dstFile);
		RenameProgressDialog fragment = new RenameProgressDialog();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if(context instanceof OnRenameFinishedListener){
			onRenameFinishedListener=(OnRenameFinishedListener)context;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
			if (activity instanceof OnRenameFinishedListener) {
				onRenameFinishedListener = (OnRenameFinishedListener) activity;
			}
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		if(getTargetFragment() instanceof OnRenameFinishedListener){
			onRenameFinishedListener=(OnRenameFinishedListener)getTargetFragment();
		}else if(getParentFragment() instanceof OnRenameFinishedListener){
			onRenameFinishedListener=(OnRenameFinishedListener)getParentFragment();
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
		getLoaderManager().destroyLoader(loader.getId());
		if(onRenameFinishedListener!=null){
			onRenameFinishedListener.onRenameFinished((File)getArguments().getSerializable("dst_file"),data);
		}
		onDismiss(getDialog());
	}

	@Override
	public void onLoaderReset(Loader<Boolean> loader) {

	}

	@Override
	protected int getLoaderId() {
		return getResources().getInteger(R.integer.filr_rename_file_task_loader_id);
	}

	@Override
	protected String getProgressMessage() {
		return getString(R.string.filr_renaming);
	}

	@Override
	public boolean executeFileTask() {
		return FileUtils.replace((File) getArguments().getSerializable("src_file"),(File)getArguments().getSerializable("dst_file"));
	}
}
