/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mysql.helper.file.object;

/**
 *
 * @author sheriff
 */
public class FilePathObject {
    private long filedate=0;
    private String filename="";
    private String filepath="";
    
    public FilePathObject(){}
    public FilePathObject(long filedate,String filename,String filepath){
        this.filedate=filedate;
        this.filename=filename;
        this.filepath=filepath;
    }

    public int compareTo(FilePathObject o) {
        return o.getFiledate().compareTo(this.getFiledate());
    }

    /**
     * @return the filedate
     */
    public String getFiledate() {
        return String.valueOf(filedate);
    }

    /**
     * @param filedate the filedate to set
     */
    public void setFiledate(long filedate) {
        this.filedate = filedate;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the filepath
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * @param filepath the filepath to set
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
