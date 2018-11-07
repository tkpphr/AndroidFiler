package com.tkpphr.android.filer.demo;

import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.tkpphr.android.filer.demo.databinding.ActivityMainBinding;
import com.tkpphr.android.filer.util.DirectorySelector;
import com.tkpphr.android.filer.util.FileListCache;
import com.tkpphr.android.filer.util.FileSort;
import com.tkpphr.android.filer.util.FileUtils;
import com.tkpphr.android.filer.view.OnCopyFinishedListener;
import com.tkpphr.android.filer.view.OnDeleteFinishedListener;
import com.tkpphr.android.filer.view.OnMoveFinishedListener;
import com.tkpphr.android.filer.view.OnRenameFinishedListener;
import com.tkpphr.android.filer.view.dialog.DirectorySelectorDialog;
import com.tkpphr.android.filer.view.dialog.FileSortMenuDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnCopyFinishedListener,
																	OnDeleteFinishedListener,
																	OnRenameFinishedListener,
																	OnMoveFinishedListener,
																	DirectorySelectorDialog.OnDirectorySelectedListener,
																	FileSortMenuDialog.OnFileSortSelectedListener,
																	RecentFilesDialog.OnRecentFileSelectedListener{
	private ActivityMainBinding binding;
	private FileListAdapter adapter;
	private FileListCache recentFiles;
	private DirectorySelector directorySelector;

	@Override
	@SuppressWarnings("unchecked")
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recentFiles=new FileListCache(getApplicationContext(),"recent",10);
		if(recentFiles.isExists()) {
			recentFiles.delete();
		}
		directorySelector=new DirectorySelector();
		directorySelector.addCallback(directory -> {
			binding.directoryPathBar.setVisibility(View.VISIBLE);
			binding.imageViewSearch.setVisibility(View.VISIBLE);
			binding.fileSearchView.setVisibility(View.INVISIBLE);
			binding.directoryPathBar.setDirectory(directory);
			adapter.setSearchString("");
			adapter.setDirectory(directory);
			binding.listViewFile.setSelection(0);
		});
		binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
		adapter=new FileListAdapter(this);
		binding.listViewFile.setAdapter(adapter);
		binding.listViewFile.setOnItemClickListener((adapterView, view, i, l) -> {
			File file=adapter.getItem(i);
			if(file.isFile()){
				FileMenuDialog.newInstance(file).show(getSupportFragmentManager(),null);
				recentFiles.addFile(file.getAbsolutePath());
			}else {
				directorySelector.moveTo(file);
			}
		});
		TypedValue outValue=new TypedValue();
		getTheme().resolveAttribute(android.R.attr.textColorPrimary,outValue,true);
		binding.imageViewSearch.getDrawable().setColorFilter(ResourcesCompat.getColor(getResources(),outValue.resourceId,getTheme()), PorterDuff.Mode.SRC_ATOP);
		binding.imageViewSearch.setOnClickListener(v->{
			binding.directoryPathBar.setVisibility(View.INVISIBLE);
			binding.imageViewSearch.setVisibility(View.INVISIBLE);
			binding.fileSearchView.setVisibility(View.VISIBLE);
			adapter.setSearchString(binding.fileSearchView.getQuery().toString());
			adapter.refresh();
		});
		binding.fileSearchView.setVisibility(View.INVISIBLE);
		binding.fileSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				adapter.setSearchString(newText);
				adapter.refresh();
				return true;
			}
		});
		binding.directoryPathBar.setOnClickPathListener(depth -> {
			File currentDirectory=directorySelector.getCurrentDirectory();
			if(currentDirectory!=null){
				File ancestorDirectory=FileUtils.getAncestorDirectory(currentDirectory, depth);
				if(ancestorDirectory.isDirectory()) {
					directorySelector.moveTo(ancestorDirectory);
				}else {
					binding.directoryPathBar.selectLastTab();
				}
			}
		});

		if(savedInstanceState==null) {
			directorySelector.moveTo(Environment.getExternalStorageDirectory());
		}else {
			directorySelector.addDirectories((List<File>) savedInstanceState.getSerializable("directories"));
		}
		ActivityCompat.requestPermissions(this,new String[]{"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"},10);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("directories",new ArrayList<>(directorySelector.getDirectories()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,0,0,"Sort").setIcon(R.drawable.ic_sort).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0,1,1,"Recent Files").setIcon(R.drawable.ic_history).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0,2,2,"Select Directory").setIcon(R.drawable.ic_action_folder_tabs).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0,3,3,"Exit").setIcon(R.drawable.ic_exit_to_app).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case 0:
				FileSortMenuDialog.newInstance(adapter.getFileSort()).show(getSupportFragmentManager(),null);
				break;
			case 1:
				RecentFilesDialog.newInstance().show(getSupportFragmentManager(),null);
				break;
			case 2:
				DirectorySelectorDialog.newInstance("Select Directory",Environment.getExternalStorageDirectory()).show(getSupportFragmentManager(),null);
				break;
			case 3:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.refresh();
	}

	@Override
	public void onBackPressed() {
		if(binding.fileSearchView.getVisibility()==View.VISIBLE){
			binding.directoryPathBar.setVisibility(View.VISIBLE);
			binding.imageViewSearch.setVisibility(View.VISIBLE);
			binding.fileSearchView.setVisibility(View.INVISIBLE);
			adapter.setSearchString("");
			adapter.refresh();
			binding.listViewFile.setSelection(0);
		}else if(!directorySelector.back()) {
			super.onBackPressed();
		}
	}

	@Override
	public void onRecentFileSelected(File file) {
		recentFiles.addFile(file.getAbsolutePath());
		FileMenuDialog.newInstance(file).show(getSupportFragmentManager(),null);
	}

	@Override
	public void onFileSortSelected(FileSort fileSort) {
		adapter.setFileSort(fileSort);
	}

	@Override
	public void onDeleteFinished(File file, boolean isSucceed) {
		Toast.makeText(getApplicationContext(),isSucceed ? "Succeed deleting file." : "Failed deleting file.",Toast.LENGTH_SHORT).show();
		adapter.refresh();
	}

	@Override
	public void onRenameFinished(File file, boolean isSucceed) {
		Toast.makeText(getApplicationContext(),isSucceed ? "Succeed renaming file." : "Failed renaming file.",Toast.LENGTH_SHORT).show();
		adapter.refresh();
		binding.listViewFile.setSelection(adapter.indexOf(file));
	}

	@Override
	public void onMoveFinished(File file, boolean isSucceed) {
		Toast.makeText(getApplicationContext(),isSucceed ? "Succeed moving file." : "Failed moving file.",Toast.LENGTH_SHORT).show();
		directorySelector.moveTo(file.getParentFile());
		binding.listViewFile.setSelection(adapter.indexOf(file));
	}

	@Override
	public void onCopyFinished(File file, boolean isSucceed) {
		Toast.makeText(getApplicationContext(),isSucceed ? "Succeed copying file." : "Failed copying file.",Toast.LENGTH_SHORT).show();
		if(FileUtils.equals(directorySelector.getCurrentDirectory(),file.getParentFile())) {
			adapter.refresh();
		}else {
			directorySelector.moveTo(file.getParentFile());
		}
		binding.listViewFile.setSelection(adapter.indexOf(file));
	}

	@Override
	public void onDirectorySelected(File directory) {
		directorySelector.moveTo(directory);
	}

	@Override
	public void onDirectorySelectCanceled() {

	}
}
