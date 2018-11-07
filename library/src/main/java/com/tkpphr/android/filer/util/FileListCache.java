package com.tkpphr.android.filer.util;

import android.content.Context;
import android.support.annotation.NonNull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class FileListCache {
	private Context context;
	private String cacheName;
	private int limit;
	private boolean excludeNotExists;
	private boolean checkDistinct;

	public FileListCache(Context context,String cacheName, int limit) {
		this(context,cacheName,limit,true,true);
	}

	public FileListCache(Context context,String cacheName, int limit, boolean excludeNotExists, boolean checkDistinct) {
		this.context=context;
		this.cacheName = cacheName+".xml";
		this.limit = limit;
		this.excludeNotExists = excludeNotExists;
		this.checkDistinct = checkDistinct;
	}

	public void addFile(String filePath){
		List<String> filePaths=new ArrayList<>();
		filePaths.add(filePath);
		addFiles(filePaths);
	}

	public void addFiles(List<String> filePaths){
		List<String> fileList=new ArrayList<>();
		fileList.addAll(filePaths);
		fileList.addAll(getFilePathList());
		if(checkDistinct) {
			HashSet<String> hashSet=new LinkedHashSet<>(fileList);
			fileList = new ArrayList<>(hashSet);
		}
		if(limit > 0 && fileList.size() >= limit) {
			fileList = fileList.subList(0, limit);
		}
		write(fileList);
	}

	public List<String> getFilePathList(){
		List<String> filePathList=new ArrayList<>();
		FileInputStream fileInputStream=null;
		try {
			fileInputStream=context.openFileInput(cacheName);
			DocumentBuilder documentBuilder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document=documentBuilder.parse(fileInputStream);
			Element root=document.getDocumentElement();
			NodeList children=root.getChildNodes();
			for(int i=0;i<children.getLength();i++){
				Node child=children.item(i);
				if(child.getNodeType()==Node.ELEMENT_NODE) {
					String filePath=child.getTextContent();
					if(excludeNotExists && !new File(filePath).exists()){
						continue;
					}
					filePathList.add(filePath);
				}
			}
		}catch (ParserConfigurationException | SAXException | IOException e){
			e.printStackTrace();
		}finally {
			if(fileInputStream!=null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return filePathList;
	}

	public List<File> getFiles(){
		List<File> files=new ArrayList<>();
		for(String filePath : getFilePathList()){
			files.add(new File(filePath));
		}
		return files;
	}

	public boolean isExists(){
		for (String file : context.fileList()){
			if(new File(file).getName().equals(cacheName)){
				return true;
			}
		}
		return false;
	}

	public boolean delete(){
		return context.deleteFile(cacheName);
	}

	private void write(@NonNull List<String> fileList){
		FileOutputStream fileOutputStream=null;
		try {
			DocumentBuilder documentBuilder= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document=documentBuilder.newDocument();
			Element root=document.createElement("files");
			for(String file : fileList){
				Element child=document.createElement("file");
				child.setTextContent(file);
				root.appendChild(child);
			}
			document.appendChild(root);
			Transformer transformer= TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			fileOutputStream=context.openFileOutput(cacheName,Context.MODE_PRIVATE);
			transformer.transform(new DOMSource(document),new StreamResult(fileOutputStream));
		}catch (ParserConfigurationException | TransformerException | IOException e){
			e.printStackTrace();
		}finally {
			if(fileOutputStream!=null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
