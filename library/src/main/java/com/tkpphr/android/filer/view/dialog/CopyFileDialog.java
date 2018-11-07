package com.tkpphr.android.filer.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileUtils;
import com.tkpphr.android.filer.view.OnCopyFinishedListener;
import com.tkpphr.android.filer.view.customview.CopyFileNameInputView;

import java.io.File;

public class CopyFileDialog extends AppCompatDialogFragment implements DirectorySelectorDialog.OnDirectorySelectedListener{
    private File dstDirectory;
    private File srcFile;
    private CopyFileNameInputView copyFileNameInputView;

    public static CopyFileDialog newInstance(@NonNull File srcFile,boolean copyToAnotherDirectory){
        if(copyToAnotherDirectory) {
            return newInstance(srcFile,null);
        }else {
            return newInstance(srcFile, srcFile.getParentFile());
        }
    }

    public static CopyFileDialog newInstance(@NonNull File srcFile,@Nullable File dstDirectory){
        CopyFileDialog instance=new CopyFileDialog();
        Bundle args=new Bundle();
        args.putSerializable("src_file",srcFile);
        args.putSerializable("dst_directory",dstDirectory);
        instance.setArguments(args);
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
        srcFile=(File)getArguments().getSerializable("src_file");
        dstDirectory=(File)getArguments().getSerializable("dst_directory");
        if(dstDirectory==null){
            if(savedInstanceState==null) {
                DirectorySelectorDialog directorySelectorDialog=DirectorySelectorDialog.newInstance(
                        getString(R.string.filr_select_copy_to_directory),
                        srcFile.getParentFile());
                directorySelectorDialog.setTargetFragment(this,0);
                directorySelectorDialog.show(getActivity().getSupportFragmentManager(),null);
            }
            return dialog.create();
        }else{
            View view=View.inflate(getContext(),R.layout.filr_dialog_copy_file_name_input,null);
            copyFileNameInputView=(CopyFileNameInputView)view;
            copyFileNameInputView.reset(dstDirectory,srcFile);
            copyFileNameInputView.setOnInputErrorChangedListener(new CopyFileNameInputView.OnInputErrorChangedListener() {
                @Override
                public void onInputErrorChanged(boolean isInputErrorEnabled) {
                    if(getDialog()!=null){
                        ((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!isInputErrorEnabled);
                    }
                }
            });
            return dialog.setTitle(FileUtils.getFileName(srcFile))
                    .setMessage(R.string.filr_copy_to)
                    .setView(view)
                    .setPositiveButton(R.string.filr_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            CopyProgressDialog copyProgressDialog=CopyProgressDialog.newInstance(copyFileNameInputView.getDstFile(),srcFile);
                            if(getTargetFragment() instanceof OnCopyFinishedListener){
                                copyProgressDialog.setTargetFragment(getTargetFragment(),0);
                            }
                            copyProgressDialog.show(getFragmentManager(),null);
                        }
                    })
                    .setNegativeButton(R.string.filr_cancel,null)
                    .create();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(copyFileNameInputView!=null) {
            ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!copyFileNameInputView.isInputErrorEnabled());
        }
    }

    @Override
    public void onDestroyView() {
        if(getRetainInstance()) {
            if (getDialog() != null && getRetainInstance()) {
                getDialog().setDismissMessage(null);
            }
        }
        super.onDestroyView();
    }

    @Override
    public void onDirectorySelected(File directory) {
        CopyFileDialog copyFileDialog=newInstance(srcFile,directory);
        if(getTargetFragment()!=null){
            copyFileDialog.setTargetFragment(getTargetFragment(),0);
        }
        copyFileDialog.show(getFragmentManager(),null);
        onDismiss(getDialog());
    }

    @Override
    public void onDirectorySelectCanceled(){
        onDismiss(getDialog());
    }


}
