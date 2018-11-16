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
