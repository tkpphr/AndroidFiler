package com.tkpphr.android.filer.view.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileTaskLoader;

public abstract class BaseFileTaskProgressDialog extends AppCompatDialogFragment implements LoaderManager.LoaderCallbacks<Boolean>,FileTaskLoader.FileTask{
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLoaderManager().initLoader(getLoaderId(),null,this);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
		View view= LayoutInflater.from(getContext()).inflate(R.layout.filr_dialog_progress,null,false);
		((TextView)view.findViewById(R.id.filr_progress_message)).setText(getProgressMessage());
		setCancelable(false);
		return dialog.setView(view).create();
	}

	@Override
	public Loader<Boolean> onCreateLoader(int id, Bundle args) {
		return new FileTaskLoader(getContext(),this);
	}

	protected abstract int getLoaderId();
	protected abstract String getProgressMessage();
}
