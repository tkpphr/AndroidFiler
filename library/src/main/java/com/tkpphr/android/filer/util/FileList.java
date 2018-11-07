package com.tkpphr.android.filer.util;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FileList extends ArrayList<File> {
    private File directory;
    private FileSort fileSort;

    public FileList() {
        this.fileSort=FileSort.NAME_ASC;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory){
        this.directory=directory;
        clear();
        refresh();
    }

    public FileSort getFileSort(){
        return this.fileSort;
    }

    public void setFileSort(FileSort fileSort){
        this.fileSort=fileSort;
    }

    public void refresh() {
        if(directory==null || !directory.isDirectory()){
            return;
        }
        File[] listFiles=directory.listFiles();
        clear();
        if(listFiles==null || listFiles.length==0){
            return;
        }
        List<File> fileList=new ArrayList<>(Arrays.asList(listFiles));
        fileSort.sort(fileList);
        addAll(fileList);
    }

    @Override
    public boolean add(File object) {
        if(isAddable(object)) {
            return super.add(object);
        }else {
            return false;
        }
    }

    @Override
    public void add(int index, File object) {
        if(isAddable(object)) {
            super.add(index, object);
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends File> collection) {
        Iterator<? extends File> iterator=collection.iterator();
        while (iterator.hasNext()){
            if(!isAddable(iterator.next())){
                iterator.remove();
            }
        }
        return super.addAll(index,collection);
    }

    @Override
    public boolean addAll(Collection<? extends File> collection) {
        Iterator<? extends File> iterator=collection.iterator();
        while (iterator.hasNext()){
            if(!isAddable(iterator.next())){
                iterator.remove();
            }
        }
        return super.addAll(collection);
    }

    public boolean isExists(File file){
        for (File file1 : this){
            if(FileUtils.equals(file1,file)){
                return true;
            }
        }
        return false;
    }

    public boolean isAddable(File file){
        return !isExists(file);
    }

}
