package com.tkpphr.android.filer.view.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.io.File;

public class DirectoryPathBar extends TabLayout{
    private String oldDirectoryPath;
    private OnClickPathListener onClickPathListener;

    public DirectoryPathBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllTabs();
    }

    public void setOnClickPathListener(OnClickPathListener onClickPathListener) {
        this.onClickPathListener = onClickPathListener;
    }

    public void setDirectory(@NonNull File directory){
        String directoryPath=directory.getAbsolutePath();
        if(TextUtils.equals(directoryPath,oldDirectoryPath)){
            return;
        }
        this.oldDirectoryPath = directoryPath;
        removeAllTabs();
        removeOnTabSelectedListener(onTabSelectedListener);
        String[] pathNames=directoryPath.split("/");
        for(int i=0;i<pathNames.length;i++){
            Tab tab=newTab();
            if(i==pathNames.length-1){
                tab.setText(pathNames[i]);
            }else {
                tab.setText(String.format("%s /",pathNames[i]));
            }
            addTab(tab);
        }
        post(new Runnable() {
                 @Override
                 public void run() {
                     Tab tab = getTabAt(getTabCount() - 1);
                     if(tab!=null && !tab.isSelected()){
                         tab.select();
                     }
                     addOnTabSelectedListener(onTabSelectedListener);
                 }
             }
        );
    }

    public void selectLastTab(){
        post(new Runnable() {
                 @Override
                 public void run() {
                     Tab tab = getTabAt(getTabCount() - 1);
                     if(tab!=null && !tab.isSelected()){
                         tab.select();
                     }
                 }
             }
        );
    }

    private final OnTabSelectedListener onTabSelectedListener=new OnTabSelectedListener() {
        @Override
        public void onTabSelected(Tab tab) {
            if(onClickPathListener !=null) {
                onClickPathListener.onClickPath(tab.getPosition());
            }
        }

        @Override
        public void onTabUnselected(Tab tab) {

        }

        @Override
        public void onTabReselected(Tab tab) {

        }
    };

    public interface OnClickPathListener {
        void onClickPath(int depth);
    }
}
