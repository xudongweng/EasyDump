/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mongo.controller.thread;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class BackupThread extends Thread{
    private String dummp="";
    private Logger log=null;
    private String logInfo="";
    private String datetime="";
    
    public BackupThread(String dump,String datetime,Logger log,String logInfo){
        this.dummp=dump;
        this.log=log;
        this.logInfo=logInfo;
        this.datetime=datetime;
    }
    @Override
    public void run() {
        try{
            log.info(" Backup startup --"+this.logInfo);
            Runtime.getRuntime().exec(this.dummp+this.datetime);
            log.info(" Backup finished --"+this.logInfo);
        }catch(IOException e){
            log.error(e.toString());
        }
    }
}
