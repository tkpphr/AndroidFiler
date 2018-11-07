package com.tkpphr.android.filer.view.customview;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.DirectorySelector;
import com.tkpphr.android.filer.util.FileUtils;
import com.tkpphr.android.filer.view.adapter.DirectoryListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectorySelectorView extends LinearLayout{
	private DirectorySelector directorySelector;
	private DirectoryPathBar directoryPathBar;
	private ListView directoryListView;
	private DirectoryListAdapter adapter;
	private OnDirectoryChangedListener onDirectoryChangedListener;


	public DirectorySelectorView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.filr_view_directory_selector,this,true);
		if(isInEditMode()){
			return;
		}
		directorySelector=new DirectorySelector();
		directorySelector.addCallback(new DirectorySelector.Callback() {
			@Override
			public void onCurrentDirectoryChanged(File currentDirectory) {
				directoryPathBar.setDirectory(currentDirectory);
				adapter.setParentDirectory(currentDirectory);
				if(onDirectoryChangedListener!=null){
					onDirectoryChangedListener.onDirectoryChanged(currentDirectory);
				}
			}
		});
		directoryPathBar=findViewById(R.id.filr_directory_path_bar);
		directoryPathBar.setOnClickPathListener(new DirectoryPathBar.OnClickPathListener() {
			@Override
			public void onClickPath(int depth) {
				File currentDirectory = directorySelector.getCurrentDirectory();
				if(currentDirectory!=null) {
					File ancestorDirectory=FileUtils.getAncestorDirectory(currentDirectory, depth);
					if(ancestorDirectory.isDirectory()) {
						directorySelector.moveTo(ancestorDirectory);
					}else {
						directoryPathBar.selectLastTab();
					}
				}
			}
		});
		directoryListView=findViewById(R.id.filr_list_view_files);
		directoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				directorySelector.moveTo(adapter.getItem(i));
			}
		});
		adapter=new DirectoryListAdapter(context);
		directoryListView.setAdapter(adapter);
	}

	@Nullable
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle outState=new Bundle();
		outState.putParcelable("super_state",super.onSaveInstanceState());
		outState.putSerializable("directories",new ArrayList<>(directorySelector.getDirectories()));
		return outState;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onRestoreInstanceState(Parcelable state) {
		if(state instanceof Bundle){
			Bundle savedState=(Bundle)state;
			state=savedState.getParcelable("super_state");
			directorySelector.addDirectories((List<File>) savedState.getSerializable("directories"));
		}
		super.onRestoreInstanceState(state);
	}

	public void setDirectory(@NonNull File directory){
		directorySelector.moveTo(directory);
	}

	@Nullable
	public File getDirectory(){
		return directorySelector.getCurrentDirectory();
	}

	public void refresh(){
		if(directorySelector.getCurrentDirectory()==null){
			return;
		}
		if(directorySelector.getCurrentDirectory().exists()) {
			adapter.refresh();
		}else {
			directorySelector.back();
		}
	}

	public boolean back(){
		return directorySelector.back();
	}

	public void setOnDirectoryChangedListener(OnDirectoryChangedListener onDirectoryChangedListener) {
		this.onDirectoryChangedListener = onDirectoryChangedListener;
	}

	public interface OnDirectoryChangedListener {
		void onDirectoryChanged(File directory);
	}
}
