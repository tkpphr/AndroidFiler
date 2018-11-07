package com.tkpphr.android.filer.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileUtils;
import com.tkpphr.android.filer.view.customview.DirectorySelectorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectorySelectorDialog extends AppCompatDialogFragment{
	private Button selectButton;
	private Button cancelButton;
	private List<File> ignoreDirectoryList;
	private DirectorySelectorView directorySelectorView;
	private OnDirectorySelectedListener onDirectorySelectedListener;

	public static DirectorySelectorDialog newInstance(String title,File firstDirectory) {
		return newInstance(title,firstDirectory,new ArrayList<File>());
	}

	public static DirectorySelectorDialog newInstance(String title,File firstDirectory,List<File> ignoreDirectoryList) {
		Bundle args = new Bundle();
		args.putString("title",title);
		args.putSerializable("start_directory", firstDirectory);
		args.putSerializable("ignore_directory_list",new ArrayList<>(ignoreDirectoryList));
		DirectorySelectorDialog dialog = new DirectorySelectorDialog();
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if(context instanceof OnDirectorySelectedListener){
			this.onDirectorySelectedListener=(OnDirectorySelectedListener)context;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
			if (activity instanceof OnDirectorySelectedListener) {
				this.onDirectorySelectedListener = (OnDirectorySelectedListener) activity;
			}
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(getTargetFragment() instanceof OnDirectorySelectedListener){
			this.onDirectorySelectedListener=(OnDirectorySelectedListener)getTargetFragment();
		}else if(getParentFragment() instanceof OnDirectorySelectedListener){
			this.onDirectorySelectedListener=(OnDirectorySelectedListener)getParentFragment();
		}
		final AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
		ignoreDirectoryList=(List<File>)getArguments().getSerializable("ignore_directory_list");
		View view= LayoutInflater.from(getContext()).inflate(R.layout.filr_dialog_directory_selector,null);
		cancelButton=view.findViewById(R.id.filr_cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(onDirectorySelectedListener!=null) {
					onDirectorySelectedListener.onDirectorySelectCanceled();
				}
				onDismiss(getDialog());
			}
		});
		selectButton=view.findViewById(R.id.filr_select_button);
		selectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(onDirectorySelectedListener!=null){
					onDirectorySelectedListener.onDirectorySelected(directorySelectorView.getDirectory());
				}
				onDismiss(getDialog());
			}
		});
		directorySelectorView=view.findViewById(R.id.commons_file_directory_selector_view);
		directorySelectorView.setFocusable(true);
		directorySelectorView.setFocusableInTouchMode(true);
		directorySelectorView.requestFocus();
		directorySelectorView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int i, KeyEvent keyEvent) {
				if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_BACK && keyEvent.getAction()==KeyEvent.ACTION_DOWN) {
					return directorySelectorView.back();
				}
				return false;
			}
		});
		directorySelectorView.setOnDirectoryChangedListener(new DirectorySelectorView.OnDirectoryChangedListener() {
			@Override
			public void onDirectoryChanged(File directory) {
				selectButton.setEnabled(directory.canRead() && directory.canWrite());
				for(File ignoreDirectory : ignoreDirectoryList){
					if(FileUtils.equals(directory,ignoreDirectory)){
						selectButton.setEnabled(false);
						break;
					}
				}
			}
		});


		if(savedInstanceState==null){
			directorySelectorView.setDirectory((File)getArguments().getSerializable("start_directory"));
		}
		return dialog.setTitle(getArguments().getString("title"))
				.setView(view)
				.create();
	}

	@Override
	public void onResume() {
		super.onResume();
		directorySelectorView.refresh();
		File directory=directorySelectorView.getDirectory();
		if(getDialog()!=null && directory!=null){
			selectButton.setEnabled(directory.canRead() && directory.canWrite());
			for(File ignoreDirectory : ignoreDirectoryList){
				if(FileUtils.equals(directory,ignoreDirectory)){
					selectButton.setEnabled(false);
					break;
				}
			}
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		if(onDirectorySelectedListener!=null){
			onDirectorySelectedListener.onDirectorySelectCanceled();
		}
	}

	public interface OnDirectorySelectedListener{
		void onDirectorySelected(File directory);
		void onDirectorySelectCanceled();
	}
}
