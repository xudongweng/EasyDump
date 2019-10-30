/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.helper.file;

import java.io.File;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class FileHelper {
    private Logger log=null;
    
    public FileHelper(){
        this.log=Logger.getLogger(FileHelper.class);
    }
    
    public boolean getFileExist(String filename){
        File file=new File(filename);
        return file.exists();
    }
    
    public boolean deleteFile(String filename){
        File file=new File(filename);
        if (file.isFile()&&file.exists()){
            file.delete();
            return true;
        }
        return false;
    }
    
    public boolean deleteFolder(String directory){
        File folder=new File(directory);
        if(folder.isDirectory()){
            if(folder.listFiles().length>0){
                File[] files=folder.listFiles();
                for(File file:files){
                    file.delete();
                }
            }
            folder.delete();
            return true;
        }
        return false;
    }
    
    public boolean renameFile(String oldname,String newname){
        if(!oldname.equals(newname)){
            File of=new File(oldname);
            File nf=new File(newname);
            if(!of.exists()){
                //System.out.println(oldname+" is not exist.");
                this.log.warn(oldname+" is not exist.");
                return false;
            }
            if(nf.exists()){
                //System.out.println(newname+" is exist.");
                this.log.warn(newname+" is exist.");
                return false;
            }else{
                of.renameTo(nf);
                return true;
            }
        }
        //System.out.println("The name of file is same.");
        this.log.warn("The name of file is same.");
        return false;
    }
    
    public void createPath(String directory){
        File file =new File(directory);
        if  (!file.exists()  && !file.isDirectory()) {        
            file.mkdirs();
        }
    }
    //获取最早文件路径
    public String getOlderFile(String directory){
        File file=new File(directory);
        File[] tempList=file.listFiles();
        if(tempList==null)
            return "";
        long filedate=0;
        String filepath="";
        if(tempList.length>0){
            for (File tempfile : tempList) {
                if(filedate==0){
                    filepath=tempfile.toString();
                    filedate=tempfile.lastModified();
                }else if(filedate>tempfile.lastModified()){
                    filepath=tempfile.toString();
                    filedate=tempfile.lastModified();
                }
            }
            return filepath;
        }else{
            return filepath;
        }
    }
    
    //获取路径下文件数量
    public int getFileNum(String directory){
        File file=new File(directory);
        File[] tempList=file.listFiles();
        return tempList.length;
    }
}
