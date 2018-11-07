package com.tkpphr.android.filer.util;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum FileSort implements Comparator<File>{
	NAME_ASC{
		@Override
		public SortBy sortBy() {
			return SortBy.SORT_BY_NAME;
		}

		@Override
		public boolean ascending() {
			return true;
		}

		@Override
		protected int directoryCompare(File file1, File file2) {
			return fileCompare(file1,file2);
		}

		@Override
		protected int fileCompare(File file1, File file2) {
			return file1.getAbsolutePath().toLowerCase().compareTo(file2.getAbsolutePath().toLowerCase());
		}
	},
	NAME_DESC{
		@Override
		public SortBy sortBy() {
			return SortBy.SORT_BY_NAME;
		}

		@Override
		public boolean ascending() {
			return false;
		}

		@Override
		protected int directoryCompare(File file1, File file2) {
			return fileCompare(file1,file2);
		}

		@Override
		protected int fileCompare(File file1, File file2) {
			return file2.getAbsolutePath().toLowerCase().compareTo(file1.getAbsolutePath().toLowerCase());
		}
	},
	SIZE_ASC{
		@Override
		public SortBy sortBy() {
			return SortBy.SORT_BY_SIZE;
		}

		@Override
		public boolean ascending() {
			return true;
		}

		@Override
		protected int directoryCompare(File file1, File file2) {
			return file1.getAbsolutePath().toLowerCase().compareTo(file2.getAbsolutePath().toLowerCase());
		}

		@Override
		protected int fileCompare(File file1, File file2) {
			if(file1.length()==file2.length()) return 0;
			return file1.length() > file2.length() ? 1 : -1;
		}
	},
	SIZE_DESC{
		@Override
		public SortBy sortBy() {
			return SortBy.SORT_BY_SIZE;
		}

		@Override
		public boolean ascending() {
			return false;
		}

		@Override
		protected int directoryCompare(File file1, File file2) {
			return file1.getAbsolutePath().toLowerCase().compareTo(file2.getAbsolutePath().toLowerCase());
		}

		@Override
		protected int fileCompare(File file1, File file2) {
			if(file1.length()==file2.length()) return 0;
			return file1.length() < file2.length() ? 1 : -1;
		}
	},
	DATE_ASC {
		@Override
		public SortBy sortBy() {
			return SortBy.SORT_BY_DATE;
		}

		@Override
		public boolean ascending() {
			return true;
		}

		@Override
		protected int directoryCompare(File file1, File file2) {
			return fileCompare(file1,file2);
		}

		@Override
		protected int fileCompare(File file1, File file2) {
			if(file1.lastModified()==file2.lastModified()) return 0;
			return file1.lastModified() > file2.lastModified() ? 1 : -1;
		}
	},
	DATE_DESC{
		@Override
		public SortBy sortBy() {
			return SortBy.SORT_BY_DATE;
		}

		@Override
		public boolean ascending() {
			return false;
		}

		@Override
		protected int directoryCompare(File file1, File file2) {
			return fileCompare(file1,file2);
		}

		@Override
		protected int fileCompare(File file1, File file2) {
			if(file1.lastModified()==file2.lastModified()) return 0;
			return file1.lastModified() < file2.lastModified() ? 1 : -1;
		}
	};

	public void sort(@NonNull List<File> src){
		Collections.sort(src,this);
	}

	public void sort(@NonNull File[] src){
		Arrays.sort(src,this);
	}

	@Override
	public int compare(File file1, File file2) {
		if(file1.isDirectory() && file2.isDirectory()){
			return directoryCompare(file1,file2);
		}else if(file1.isDirectory() && !file2.isDirectory()){
			return -1;
		}else if(!file1.isDirectory() && file2.isDirectory()){
			return 1;
		}else {
			return fileCompare(file1,file2);
		}
	}


	public abstract SortBy sortBy();
	public abstract boolean ascending();
	protected abstract int directoryCompare(File file1,File file2);
	protected abstract int fileCompare(File file1,File file2);

	public static FileSort getSortType(SortBy sortBy, boolean ascending){
		switch (sortBy){
			case SORT_BY_NAME:
				return ascending ? NAME_ASC : NAME_DESC;
			case SORT_BY_SIZE:
				return ascending ? SIZE_ASC : SIZE_DESC;
			default:
				return ascending ? DATE_ASC : DATE_DESC;
		}
	}
}
