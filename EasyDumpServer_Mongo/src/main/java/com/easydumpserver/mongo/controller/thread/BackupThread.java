/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mongo.controller.thread;

import com.easydumpserver.helper.compress.ZipUtilsHelper;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class BackupThread extends Thread{
    private String dummp="";
    private String dumpPath="";
    private String datetime="";
    private ZipUtilsHelper zuh=null;
    private Logger log=null;
    private String logInfo="";

    public BackupThread(String dump,String dumpPath,String datetime,ZipUtilsHelper zuh,Logger log,String logInfo){
        this.dummp=dump;
        this.dumpPath=dumpPath;
        this.datetime=datetime;
        this.zuh=zuh;
        this.log=log;
        this.logInfo=logInfo;
    }
    @Override
    public void run() {
        try{
            log.info(" Backup startup --"+this.logInfo);
            InputStream in = Runtime.getRuntime().exec(this.dummp).getInputStream();
            zuh.zipStreamCompress(in, this.dumpPath, datetime+".bson",datetime);
            log.info(" Backup finished --"+this.logInfo);
        }catch(IOException e){
            log.error(e.toString());
        }
    }
}
