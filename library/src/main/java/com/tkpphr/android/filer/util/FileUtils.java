package com.tkpphr.android.filer.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FileUtils {
    private FileUtils(){}

    public static boolean equals(File file1, File file2){
        if(file1==null || file2==null){
            return false;
        }
        return file1.getAbsolutePath().toLowerCase().equals(file2.getAbsolutePath().toLowerCase());
    }

    public static boolean isFile(@NonNull File file){
        return file.exists() && file.isFile();
    }

    public static boolean isFile(@NonNull File file, String... filterExtensions){
        if(!isFile(file)){
            return false;
        }
        for(String extension : filterExtensions){
            if(file.getAbsolutePath().toLowerCase().endsWith(extension.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public static boolean isFile(String filePath){
        if(TextUtils.isEmpty(filePath)){
            return false;
        }
        return isFile(new File(filePath));
    }

    public static boolean isFile(String filePath,String... filterExtensions){
        return isFile(new File(filePath),filterExtensions);
    }

    public static String getFullFileName(String filePath){
        if(TextUtils.isEmpty(filePath)) {
            return "";
        }
        return new File(filePath).getName();
    }

    public static String getFullFileName(File file){
        return getFullFileName(file.getAbsolutePath());
    }

    public static String getFileName(String filePath){
        String fullFileName=getFullFileName(filePath);
        int index=fullFileName.lastIndexOf(".");
        if(index!=-1){
            return fullFileName.substring(0,index);
        }
        return fullFileName;
    }

    public static String getFileName(File file){
        return getFileName(file.getAbsolutePath());
    }

    public static String getFileExtension(String filePath){
        String fullFileName=getFullFileName(filePath);
        int index=fullFileName.lastIndexOf(".");
        if(index!=-1){
            return fullFileName.substring(index,fullFileName.length());
        }
        return fullFileName;
    }

    public static String getFileExtension(File file){
        return getFileExtension(file.getAbsolutePath());
    }

    public static long getFileSize(File file){
        if(isFile(file)){
            return file.length();
        }else {
            return 0;
        }
    }

    public static long getFileSize(String filePath){
        return getFileSize(new File(filePath));
    }

    public static String getReadableFileSize(File file){
        if(!file.exists()){
            return "";
        }
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(file.length())/Math.log10(1024));
        if(digitGroups >= 0 && digitGroups < units.length){
            return new DecimalFormat("#,##0.#").format(file.length()/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        }else {
            return "0 B";
        }
    }

    public static String getReadableFileSize(String filePath){
        return getReadableFileSize(new File(filePath));
    }

    public static String getLastModifiedDate(File file){
        return DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.SHORT, Locale.getDefault())
                .format(new Date(file.lastModified()));
    }

    public static String getLastModifiedDate(String filePath){
        if(FileUtils.isFile(filePath)){
            return getLastModifiedDate(new File(filePath));
        }else {
            return "";
        }
    }

    public static boolean isExistsInDirectory(File directory,String fullFileName){
        return isExistsInDirectory(directory.list(),fullFileName);
    }

    public static boolean isExistsInDirectory(String[] fileNames,String fullFileName){
        if(fileNames==null || fileNames.length==0){
            return false;
        }
        String fullFileNameLowerCase=fullFileName.toLowerCase();
        for(String fileName : fileNames){
            if(TextUtils.equals(fileName.toLowerCase(),fullFileNameLowerCase)){
                return true;
            }
        }
        return false;
    }

    public static byte[] convertFileToByteArray(File file){
        if(!isFile(file)){
            return null;
        }
        byte[] data;
        data=new byte[(int)file.length()];
        FileInputStream inputStream=null;
        ByteArrayOutputStream outputStream=null;
        try {
            inputStream=new FileInputStream(file);
            outputStream=new ByteArrayOutputStream();
            outputStream.write(inputStream.read(data));
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] convertFileToByteArray(String filePath){
        return convertFileToByteArray(new File(filePath));
    }

    public static boolean delete(File file){
        if(file.exists()){
            if(file.delete()){
                return true;
            }
        }
        return false;
    }

    public static boolean delete(String filePath){
        return delete(new File(filePath));
    }

    public static boolean replace(File srcFile,File dstFile){
        if(isFile(srcFile) && !equals(srcFile,dstFile)){
            if(dstFile.exists()){
                return dstFile.delete() && srcFile.renameTo(dstFile);
            }else {
                return srcFile.renameTo(dstFile);
            }
        }else {
            return false;
        }

    }

    public static boolean replace(String srcPath,String dstPath){
        return replace(new File(srcPath),new File(dstPath));
    }

    public static boolean rename(File srcFile,String newFileName){
        if(isFile(srcFile)) {
            File dstFile=new File(srcFile.getParentFile().getAbsolutePath() + "/" + newFileName + getFileExtension(srcFile));
            return !dstFile.exists() && replace(srcFile, dstFile);
        }else {
            return false;
        }
    }

    public static boolean rename(String srcFilePath,String newFileName){
        return rename(new File(srcFilePath),newFileName);
    }

    public static boolean copy(File srcFile,File dstFile){
        if(!isFile(srcFile) || equals(srcFile,dstFile)){
            return false;
        }
        FileChannel srcChannel = null;
        FileChannel dstChannel=null;
        boolean isSucceed;
        try {
            srcChannel = new FileInputStream(srcFile).getChannel();
            dstChannel = new FileOutputStream(dstFile).getChannel();
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
            isSucceed=true;
        }catch (IOException e){
            e.printStackTrace();
            isSucceed=false;
        }finally {
            closeFileChannel(srcChannel);
            closeFileChannel(dstChannel);
        }
        return isSucceed;
    }

    public static boolean copy(String srcPath,String dstPath){
        return copy(new File(srcPath),new File(dstPath));
    }

    public static void closeFileChannel(FileChannel fileChannel){
        if(fileChannel!=null) {
            try {
                fileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getAncestorDirectory(File directory, int depth){
        if(!directory.isDirectory()){
            throw new IllegalArgumentException("argument[directory] not directory.");
        }
        String[] pathNames=directory.getAbsolutePath().split("/");
        if(depth >= pathNames.length || depth < 0){
            throw new IndexOutOfBoundsException("depth is out of range.");
        }
        StringBuilder ancestorPath= new StringBuilder();
        ancestorPath.append(pathNames[0]);
        for(int i=1;i<depth+1;i++){
            ancestorPath.append("/");
            ancestorPath.append(pathNames[i]);
        }
        return new File(new String(ancestorPath));
    }

    public static List<File> searchAll(File directory, FileFilter fileFilter){
        List<File> foundList=new ArrayList<>();
        searchAllRecursive(foundList,directory,fileFilter);
        return foundList;
    }

    private static void searchAllRecursive(List<File> foundList, File parentDirectory, FileFilter fileFilter){
        File[] files=parentDirectory.listFiles();
        if(files==null){
            return;
        }
        for(File file:files){
            if(file.isDirectory()){
                searchAllRecursive(foundList,file,fileFilter);
            }else {
                if(fileFilter.accept(file)) {
                    foundList.add(file);
                }
            }
        }
    }
}
