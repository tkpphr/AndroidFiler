package com.tkpphr.android.filer.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileUtils;

import java.io.File;

public class DeleteFileDialog extends AppCompatDialogFragment{
    private File file;

    public static DeleteFileDialog newInstance(@NonNull File file){
        DeleteFileDialog instance=new DeleteFileDialog();
        Bundle args=new Bundle();
        args.putSerializable("file",file);
        instance.setArguments(args);
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        file=(File)getArguments().getSerializable("file");
        return new AlertDialog.Builder(getContext())
                .setTitle(FileUtils.getFileName(file))
                .setMessage(R.string.filr_confirm_delete)
                .setPositiveButton(R.string.filr_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteProgressDialog deleteProgressDialog=DeleteProgressDialog.newInstance(file);
                        if(getTargetFragment() instanceof DeleteProgressDialog){
                            deleteProgressDialog.setTargetFragment(getTargetFragment(),0);
                        }
                        deleteProgressDialog.show(getFragmentManager(),null);
                    }
                })
                .setNegativeButton(R.string.filr_no,null)
                .create();
    }
}
