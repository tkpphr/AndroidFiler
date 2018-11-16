/*
   Copyright 2018 tkpphr

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.tkpphr.android.filer.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileSort;
import com.tkpphr.android.filer.util.FileUtils;

import java.io.File;
import java.io.FileFilter;

public class DirectoryListAdapter extends BaseAdapter{
	private File parentDirectory;
	private File[] directories;
	private LayoutInflater layoutInflater;

	public DirectoryListAdapter(Context context) {
		this.layoutInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return directories==null ? 0 : directories.length;
	}

	@Override
	public File getItem(int i) {
		return directories[i];
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		if(view==null){
			view=layoutInflater.inflate(R.layout.filr_directory_item,viewGroup,false);
		}
		File directory=getItem(i);
		((TextView)view.findViewById(R.id.filr_directory_name)).setText(directory.getName());
		((TextView)view.findViewById(R.id.filr_directory_date)).setText(FileUtils.getLastModifiedDate(directory));
		return view;
	}

	public void setParentDirectory(File parentDirectory){
		this.parentDirectory=parentDirectory;
		refresh();
	}

	public void refresh(){
		if(parentDirectory!=null){
			directories=parentDirectory.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory() && file.canRead() && file.canWrite();
				}
			});
			if(directories!=null) {
				FileSort.NAME_ASC.sort(directories);
			}
			notifyDataSetChanged();
		}
	}
}
