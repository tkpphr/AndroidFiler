package com.tkpphr.android.filer.view;

import java.io.File;

public interface OnDeleteFinishedListener {
	void onDeleteFinished(File file, boolean isSucceed);
}
