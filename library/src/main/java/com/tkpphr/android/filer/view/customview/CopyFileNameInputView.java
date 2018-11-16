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
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileUtils;

import java.io.File;

public class CopyFileNameInputView extends TextInputLayout{
	private File dstDirectory;
	private File srcFile;
	private TextInputLayout textInputLayout;
	private TextInputEditText textInputEditText;
	private boolean isInputErrorEnabled;
	private OnInputErrorChangedListener onInputErrorChangedListener;

	public CopyFileNameInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.filr_name_input,this,true);
		if(isInEditMode()){
			return;
		}
		textInputLayout=findViewById(R.id.filr_text_input_layout);
		textInputLayout.setHint(getContext().getString(R.string.filr_hint_enter));
		textInputEditText=findViewById(R.id.filr_text_input_edit_text);
		textInputEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				checkFileName();
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Bundle outState=new Bundle();
		outState.putParcelable("super_state",super.onSaveInstanceState());
		outState.putSerializable("dst_directory", dstDirectory);
		outState.putSerializable("src_file",srcFile);
		outState.putString("text",textInputEditText.getText().toString());
		outState.putBoolean("is_input_error_enabled",isInputErrorEnabled);
		return outState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if(state instanceof Bundle){
			Bundle savedState=(Bundle)state;
			state=savedState.getParcelable("super_state");
			dstDirectory =(File) savedState.getSerializable("dst_directory");
			srcFile=(File)savedState.getSerializable("src_file");
			textInputEditText.setText(savedState.getString("text"));
			isInputErrorEnabled=savedState.getBoolean("is_input_error_enabled");
			checkFileName();
		}
		super.onRestoreInstanceState(state);
	}

	public void reset(File directory,File srcFile) {
		this.dstDirectory =directory;
		this.srcFile=srcFile;
		textInputEditText.setText(FileUtils.getFileName(srcFile));
		this.isInputErrorEnabled=true;
		checkFileName();
	}

	public String getText(){
		return textInputEditText.getText().toString();
	}

	public File getDstFile(){
		return new File(dstDirectory.getAbsolutePath()+"/"+getText()+FileUtils.getFileExtension(srcFile));
	}

	public void setOnInputErrorChangedListener(OnInputErrorChangedListener onInputErrorChangedListener) {
		this.onInputErrorChangedListener = onInputErrorChangedListener;
	}

	public boolean isInputErrorEnabled(){
		return this.isInputErrorEnabled;
	}

	private void checkFileName(){
		if(dstDirectory ==null){
			return;
		}
		boolean isInputErrorEnabled;
		String fileExtension= FileUtils.getFileExtension(srcFile);
		String fileName=textInputEditText.getText().toString();
		if(TextUtils.isEmpty(fileName)) {
			isInputErrorEnabled = true;
			textInputLayout.setError(getContext().getString(R.string.filr_error_empty));
		}else if(FileUtils.isExistsInDirectory(dstDirectory,fileName+fileExtension)) {
			isInputErrorEnabled = true;
			textInputLayout.setError(getContext().getString(R.string.filr_error_exists));
		}else {
			isInputErrorEnabled=false;
			textInputLayout.setError("");
		}
		if(this.isInputErrorEnabled!=isInputErrorEnabled){
			if(onInputErrorChangedListener!=null){
				onInputErrorChangedListener.onInputErrorChanged(isInputErrorEnabled);
			}
			this.isInputErrorEnabled=isInputErrorEnabled;
		}
		textInputLayout.setErrorEnabled(isInputErrorEnabled);
	}

	public interface OnInputErrorChangedListener{
		void onInputErrorChanged(boolean isInputErrorEnabled);
	}
}
