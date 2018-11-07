package com.tkpphr.android.filer.demo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tkpphr.android.filer.util.FileSort;
import com.tkpphr.android.filer.util.FileUtils;

import java.io.File;

public class FileListAdapter extends BaseAdapter{
    private Context context;
    private File directory;
    private File[] files;
    private FileSort fileSort;
    private String searchString;
    private Drawable folderIcon;
    private Drawable fileIcon;

    public FileListAdapter(@NonNull Context context) {
        this.context = context;
        this.fileSort=FileSort.NAME_ASC;
        initializeIcon();
    }

    public FileListAdapter(Context context,File[] files) {
        this.context=context;
        this.files = files;
        initializeIcon();
    }

    @Override
    public int getCount() {
        return files==null ? 0 : files.length;
    }

    @Override
    public File getItem(int position) {
        return files[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.file_list_item,parent,false);
        }
        File file=getItem(position);
        ((ImageView)convertView.findViewById(R.id.icon)).setImageDrawable(file.isDirectory() ? folderIcon : fileIcon);
        ((TextView)convertView.findViewById(R.id.text)).setText(createBackgroundSpan(file.getName(),searchString));
        ((TextView)convertView.findViewById(R.id.text2)).setText(FileUtils.getLastModifiedDate(file));
        ((TextView)convertView.findViewById(R.id.text3)).setText(file.isDirectory() ? "" : FileUtils.getReadableFileSize(file));
        return convertView;
    }

    public int indexOf(File file){
        if(files==null){
            return 0;
        }
        for(int i=0;i<files.length;i++){
            if(FileUtils.equals(file,files[i])){
                return i;
            }
        }
        return 0;
    }

    public void setDirectory(File directory){
        this.directory = directory;
        refresh();
    }

    public void setFileSort(FileSort fileSort){
        this.fileSort=fileSort;
        refresh();
    }

    public FileSort getFileSort(){
        return fileSort;
    }

    public void setSearchString(String searchString){
        this.searchString=searchString;
    }

    public void refresh(){
        if(directory==null){
            return;
        }
        if(TextUtils.isEmpty(searchString)){
            files=directory.listFiles(file -> file.canRead() && file.canWrite());
        }else {
            String searchStringLowerCase=searchString.toLowerCase();
            files=directory.listFiles(file->file.canRead() && file.canWrite() && file.getName().toLowerCase().contains(searchStringLowerCase));
        }
        if(files!=null) {
            fileSort.sort(files);
        }
        notifyDataSetChanged();
    }

    public static SpannableStringBuilder createBackgroundSpan(String src,String searchString){
        SpannableStringBuilder srcBuilder=new SpannableStringBuilder();
        srcBuilder.append(src);
        String srcLowerCase=src.toLowerCase();
        if(TextUtils.isEmpty(searchString)){
            return srcBuilder;
        }
        String searchStringLowerCase=searchString.toLowerCase();
        int index = srcLowerCase.indexOf(searchStringLowerCase);
        while (index >= 0) {
            srcBuilder.setSpan(new BackgroundColorSpan(Color.rgb(255,140,0)), index, index + searchStringLowerCase.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            index = srcLowerCase.indexOf(searchStringLowerCase, index + searchString.length());
        }
        return srcBuilder;
    }

    private void initializeIcon(){
        folderIcon= ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_action_folder_tabs,context.getTheme());
        fileIcon= ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_action_document,context.getTheme());
        TypedValue outValue=new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorPrimary,outValue,true);
        int color=ResourcesCompat.getColor(context.getResources(),outValue.resourceId,context.getTheme());
        folderIcon.setColorFilter(color,PorterDuff.Mode.SRC_ATOP);
        fileIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
}
