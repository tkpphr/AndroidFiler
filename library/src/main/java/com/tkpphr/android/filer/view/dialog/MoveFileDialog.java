package com.tkpphr.android.filer.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.tkpphr.android.filer.R;
import com.tkpphr.android.filer.util.FileUtils;
import com.tkpphr.android.filer.view.OnMoveFinishedListener;

import java.io.File;
import java.util.Arrays;

public class MoveFileDialog extends AppCompatDialogFragment implements DirectorySelectorDialog.OnDirectorySelectedListener{
    private File srcFile;
    private File moveFile;
    private File dstDirectory;

    public static MoveFileDialog newInstance(@NonNull File srcFile, @Nullable File dstDirectory){
        MoveFileDialog dialog=new MoveFileDialog();
        Bundle args=new Bundle();
        args.putSerializable("src_file",srcFile);
        args.putSerializable("dst_directory",dstDirectory);
        dialog.setArguments(args);
        return dialog;
    }

    public static MoveFileDialog newInstance(@NonNull File srcFile){
        return newInstance(srcFile,null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
        srcFile=(File)getArguments().getSerializable("src_file");
        dstDirectory=(File)getArguments().getSerializable("dst_directory");
        if(dstDirectory==null){
            if(savedInstanceState==null) {
                DirectorySelectorDialog directoryDialog=DirectorySelectorDialog.newInstance(getString(R.string.filr_select_move_to_directory),
                                                                                                srcFile.getParentFile(),
                                                                                                Arrays.asList(srcFile.getParentFile()));
                directoryDialog.setTargetFragment(this,0);
                directoryDialog.show(getFragmentManager(), null);
            }
            return dialog.create();
        }else{
            moveFile=new File(dstDirectory.getAbsolutePath()+"/"+srcFile.getName());
            if(moveFile.exists()){
                return dialog.setTitle(FileUtils.getFileName(srcFile))
                        .setMessage(R.string.filr_confirm_move_to)
                        .setPositiveButton(R.string.filr_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MoveProgressDialog moveProgressDialog=MoveProgressDialog.newInstance(moveFile,srcFile);
                                if(getTargetFragment() instanceof OnMoveFinishedListener){
                                    moveProgressDialog.setTargetFragment(getTargetFragment(),getTargetRequestCode());
                                }
                                moveProgressDialog.show(getFragmentManager(),null);
                            }
                        })
                        .setNegativeButton(R.string.filr_no,null)
                        .create();
            }else {
                setShowsDialog(false);
                dismiss();
                if(FileUtils.equals(srcFile,moveFile)) {

                }else {
                    MoveProgressDialog moveProgressDialog=MoveProgressDialog.newInstance(moveFile,srcFile);
                    if(getTargetFragment() instanceof OnMoveFinishedListener){
                        moveProgressDialog.setTargetFragment(getTargetFragment(),0);
                    }
                    moveProgressDialog.show(getFragmentManager(),null);
                }
                return dialog.create();
            }
        }
    }

    @Override
    public void onDirectorySelected(File directory) {
        MoveFileDialog moveFileDialog=newInstance(srcFile,directory);
        if(getTargetFragment() instanceof OnMoveFinishedListener){
            moveFileDialog.setTargetFragment(getTargetFragment(),0);
        }
        moveFileDialog.show(getFragmentManager(), null);
        onDismiss(getDialog());
    }

    @Override
    public void onDirectorySelectCanceled() {
        onDismiss(getDialog());
    }

}
