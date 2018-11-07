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
import android.view.View;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileSort;
import com.tkpphr.android.filer.view.customview.FileSortMenuView;


public class FileSortMenuDialog extends AppCompatDialogFragment {
	private OnFileSortSelectedListener onFileSortSelectedListener;

	public static FileSortMenuDialog newInstance(FileSort defaultSelected) {

		Bundle args = new Bundle();
		args.putInt("file_sort",defaultSelected.ordinal());
		FileSortMenuDialog instance = new FileSortMenuDialog();
		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if(context instanceof OnFileSortSelectedListener){
			this.onFileSortSelectedListener=(OnFileSortSelectedListener)context;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
			if (activity instanceof OnFileSortSelectedListener) {
				this.onFileSortSelectedListener = (OnFileSortSelectedListener) activity;
			}
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(getTargetFragment() instanceof OnFileSortSelectedListener){
			this.onFileSortSelectedListener=(OnFileSortSelectedListener)getTargetFragment();
		}else if(getParentFragment() instanceof OnFileSortSelectedListener){
			this.onFileSortSelectedListener=(OnFileSortSelectedListener)getParentFragment();
		}
		AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
		View view=View.inflate(getContext(),R.layout.filr_dialog_file_sort_menu,null);
		final FileSortMenuView fileSortMenuView=(FileSortMenuView)view;
		if(savedInstanceState==null) {
			fileSortMenuView.setFileSort(FileSort.values()[getArguments().getInt("file_sort")]);
		}
		return dialog.setView(view)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if(onFileSortSelectedListener!=null){
							onFileSortSelectedListener.onFileSortSelected(fileSortMenuView.getFileSort());
						}
						onDismiss(getDialog());
					}
				})
				.create();
	}

	public interface OnFileSortSelectedListener{
		void onFileSortSelected(FileSort fileSort);
	}

}
