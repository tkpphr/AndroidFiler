package com.tkpphr.android.filer.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ListView;

import com.tkpphr.android.filer.util.FileListCache;

import java.io.File;
import java.util.List;


public class RecentFilesDialog extends DialogFragment {
	private OnRecentFileSelectedListener onRecentFileSelectedListener;

	public static RecentFilesDialog newInstance() {
		Bundle args = new Bundle();

		RecentFilesDialog fragment = new RecentFilesDialog();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if(context instanceof OnRecentFileSelectedListener){
			this.onRecentFileSelectedListener =(OnRecentFileSelectedListener)context;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnRecentFileSelectedListener){
			this.onRecentFileSelectedListener =(OnRecentFileSelectedListener)activity;
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(getTargetFragment() instanceof OnRecentFileSelectedListener){
			this.onRecentFileSelectedListener =(OnRecentFileSelectedListener)getTargetFragment();
		}
		AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
		FileListCache cache=new FileListCache(getContext(),"recent",10);
		dialog.setTitle("Select Recent File").setPositiveButton("Close", null);
		if(cache.isExists()) {
			final List<File> files= cache.getFiles();
			ListView listView = new ListView(getContext(), null);
			FileListAdapter adapter = new FileListAdapter(getContext(), files.toArray(new File[files.size()]));
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(((adapterView, view, i, l) -> {
				if(onRecentFileSelectedListener !=null){
					onRecentFileSelectedListener.onRecentFileSelected(files.get(i));
				}
				onDismiss(getDialog());
			}));
			return dialog.setView(listView).create();
		}else {
			return dialog.setMessage("No Recent Files").create();
		}
	}

	public interface OnRecentFileSelectedListener {
		void onRecentFileSelected(File file);
	}
}
