package com.tkpphr.android.filer.demo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tkpphr.android.filer.util.FileUtils;
import com.tkpphr.android.filer.view.dialog.CopyFileDialog;
import com.tkpphr.android.filer.view.dialog.DeleteFileDialog;
import com.tkpphr.android.filer.view.dialog.MoveFileDialog;
import com.tkpphr.android.filer.view.dialog.RenameFileDialog;

import java.io.File;

public class FileMenuDialog extends DialogFragment implements AdapterView.OnItemClickListener{
	private File file;

	public static FileMenuDialog newInstance(File file) {

		Bundle args = new Bundle();
		args.putSerializable("file", file);
		FileMenuDialog fragment = new FileMenuDialog();
		fragment.setArguments(args);
		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
		file=(File) getArguments().getSerializable("file");
		String title=file.getName();
		String message= FileUtils.getLastModifiedDate(file) + "\n"  + FileUtils.getReadableFileSize(file);
		String[] items=new String[]{"Rename","Move","Copy","Delete"};
		ListView listView=new ListView(getContext(),null);
		listView.setOnItemClickListener(this);
		listView.setAdapter(new ArrayAdapter<>(getContext(),R.layout.file_menu_item,items));
		return dialog.setTitle(title)
				.setMessage(message)
				.setView(listView)
				.setPositiveButton("Close",null)
				.create();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		switch (i){
			case 0:
				RenameFileDialog.newInstance(file).show(getFragmentManager(),null);
				break;
			case 1:
				MoveFileDialog.newInstance(file).show(getFragmentManager(),null);
				break;
			case 2:
				CopyFileDialog.newInstance(file,true).show(getFragmentManager(),null);
				break;
			case 3:
				DeleteFileDialog.newInstance(file).show(getFragmentManager(),null);
				break;
		}
		onDismiss(getDialog());
	}
}
