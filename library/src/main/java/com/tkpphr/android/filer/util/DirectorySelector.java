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

import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DirectorySelector{
    private LinkedList<File> directories;
    private List<Callback> callbacks;

    public DirectorySelector() {
        this.directories=new LinkedList<>();
        this.callbacks=new ArrayList<>();
    }

    public DirectorySelector(List<File> directories) {
        this.directories=new LinkedList<>(directories);
        this.callbacks=new ArrayList<>();
    }

    public List<File> getDirectories(){
        return Collections.unmodifiableList(this.directories);
    }

    public void addDirectories(List<File> directories){
        File oldCurrentDirectory=getCurrentDirectory();
        this.directories.addAll(directories);
        if(oldCurrentDirectory==null || !FileUtils.equals(oldCurrentDirectory,getCurrentDirectory())) {
            onCurrentDirectoryChanged();
        }
    }

    @Nullable
    public File getCurrentDirectory() {
        return directories.size()==0 ? null : directories.peek();
    }

    public void moveTo(File directory){
        if(!directory.isDirectory()){
            return;
        }
        if(directories.size()==0 || !FileUtils.equals(getCurrentDirectory(),directory)){
            directories.push(directory);
            onCurrentDirectoryChanged();
        }
    }

    public boolean back(){
        Iterator<File> iterator=directories.iterator();
        while (iterator.hasNext()){
            if(!iterator.next().exists()){
                iterator.remove();
            }
        }
        File previousDirectory=directories.pop();
        while (directories.size()>1 && FileUtils.equals(previousDirectory,directories.peek())) {
            previousDirectory = directories.pop();
            if(directories.size()==0){
                break;
            }
        }
        if(directories.size()>=1){
            onCurrentDirectoryChanged();
            return true;
        }else {
            directories.push(previousDirectory);
            return false;
        }
    }

    public int getCount(){
        return directories.size();
    }

    public void addCallback(Callback callback){
        this.callbacks.add(callback);
    }

    public void removeCallback(Callback callback){
        this.callbacks.remove(callback);
    }

    private void onCurrentDirectoryChanged(){
        for(Callback callback : callbacks){
            callback.onCurrentDirectoryChanged(getCurrentDirectory());
        }
    }

    public interface Callback{
        void onCurrentDirectoryChanged(File currentDirectory);
    }

}
