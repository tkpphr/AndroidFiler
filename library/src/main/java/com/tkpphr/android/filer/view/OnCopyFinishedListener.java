package com.tkpphr.android.filer.view;

import java.io.File;

public interface OnCopyFinishedListener {
	void onCopyFinished(File file, boolean isSucceed);
}
