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
package com.tkpphr.android.filer.view.customview;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileSort;
import com.tkpphr.android.filer.util.SortBy;


public class FileSortMenuView extends LinearLayout{
	private RadioButton sortByName;
	private RadioButton sortByDate;
	private RadioButton sortBySize;
	private RadioButton orderByAsc;
	private RadioButton orderByDesc;
	private FileSort fileSort;
	private OnFileSortChangedListener onFileSortChangedListener;

	public FileSortMenuView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.filr_view_file_sort_menu,this,true);
		if(isInEditMode()){
			return;
		}
		sortByName=findViewById(R.id.filr_sort_by_name);
		sortByName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setFileSort(FileSort.getSortType(SortBy.SORT_BY_NAME,fileSort.ascending()));
			}
		});
		sortBySize=findViewById(R.id.filr_sort_by_size);
		sortBySize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setFileSort(FileSort.getSortType(SortBy.SORT_BY_SIZE,fileSort.ascending()));
			}
		});
		sortByDate=findViewById(R.id.filr_sort_by_date);
		sortByDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setFileSort(FileSort.getSortType(SortBy.SORT_BY_DATE,fileSort.ascending()));
			}
		});
		orderByAsc=findViewById(R.id.filr_order_by_asc);
		orderByAsc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setFileSort(FileSort.getSortType(fileSort.sortBy(),true));
			}
		});
		orderByDesc=findViewById(R.id.filr_order_by_desc);
		orderByDesc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setFileSort(FileSort.getSortType(fileSort.sortBy(),false));
			}
		});
		setFileSort(FileSort.NAME_ASC);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle outState=new Bundle();
		outState.putParcelable("super_state",super.onSaveInstanceState());
		outState.putInt("file_sort",fileSort.ordinal());
		return outState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if(state instanceof Bundle){
			Bundle savedState=(Bundle)state;
			state=savedState.getParcelable("super_state");
			setFileSort(FileSort.values()[savedState.getInt("file_sort")]);
		}
		super.onRestoreInstanceState(state);
	}

	public FileSort getFileSort(){
		return this.fileSort;
	}

	public void setFileSort(FileSort fileSort){
		if(this.fileSort!=fileSort) {
			if(this.onFileSortChangedListener!=null){
				this.onFileSortChangedListener.onFileSortChanged(fileSort);
			}
			this.fileSort = fileSort;
			refresh();
		}
	}

	public void setOnFileSortChangedListener(OnFileSortChangedListener onFileSortChangedListener) {
		this.onFileSortChangedListener = onFileSortChangedListener;
	}

	private void refresh(){
		sortByName.setChecked(fileSort.sortBy()==SortBy.SORT_BY_NAME);
		sortByDate.setChecked(fileSort.sortBy()==SortBy.SORT_BY_DATE);
		sortBySize.setChecked(fileSort.sortBy()==SortBy.SORT_BY_SIZE);
		orderByAsc.setChecked(fileSort.ascending());
		orderByDesc.setChecked(!fileSort.ascending());
	}

	public interface OnFileSortChangedListener {
		void onFileSortChanged(FileSort fileSort);
	}

}
