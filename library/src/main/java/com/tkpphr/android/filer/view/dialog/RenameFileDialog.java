package com.tkpphr.android.filer.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileUtils;
import com.tkpphr.android.filer.view.OnRenameFinishedListener;
import com.tkpphr.android.filer.view.customview.RenameFileNameInputView;

import java.io.File;


public class RenameFileDialog extends AppCompatDialogFragment{
    private File file;
    private RenameFileNameInputView renameFileNameInputView;

    public static RenameFileDialog newInstance(@NonNull File file){
        RenameFileDialog instance=new RenameFileDialog();
        Bundle args=new Bundle();
        args.putSerializable("file",file);
        instance.setArguments(args);
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        file=(File)getArguments().getSerializable("file");
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
        View view=View.inflate(getContext(),R.layout.filr_dialog_rename_file_name_input,null);
        renameFileNameInputView=(RenameFileNameInputView)view;
        if(savedInstanceState==null) {
            renameFileNameInputView.setFile(file);
        }
        renameFileNameInputView.setOnInputErrorChangedListener(new RenameFileNameInputView.OnInputErrorChangedListener() {
            @Override
            public void onInputErrorChanged(boolean isInputErrorEnabled) {
                if(getDialog()!=null){
                    ((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!isInputErrorEnabled);
                }
            }
        });
        return dialog.setTitle(FileUtils.getFileName(file))
                .setMessage(R.string.filr_rename_to)
                .setView(view)
                .setPositiveButton(R.string.filr_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RenameProgressDialog renameProgressDialog=RenameProgressDialog.newInstance(file,renameFileNameInputView.getRenamedFile());
                        if(getTargetFragment() instanceof OnRenameFinishedListener){
                            renameProgressDialog.setTargetFragment(getTargetFragment(),getTargetRequestCode());
                        }
                        renameProgressDialog.show(getFragmentManager(),null);
                    }
                })
                .setNegativeButton(R.string.filr_cancel,null)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!renameFileNameInputView.isInputErrorEnabled());
    }

}
