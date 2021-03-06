/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mongo.controller;

import com.easydumpserver.helper.file.FileHelper;
import com.easydumpserver.mongo.controller.thread.BackupThread;
import com.easydumpserver.mongo.model.DumpArrObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class MongoBackupController {
    private Logger log=null;
    private Locale myLocale = null;
    private ResourceBundle rb = null;
    private int threads=1;
    public MongoBackupController(){
        this.log=Logger.getLogger(MongoBackupController.class);
        myLocale = Locale.getDefault(Locale.Category.FORMAT);
        this.rb= ResourceBundle.getBundle("config",myLocale);
        this.threads=Integer.parseInt(rb.getString("threads"));
    }
    
    public int getThreadNum(){
        return this.threads;
    }
    
    public void singleTreadBackup(DumpArrObject dao){
        
        List dumpList=dao.getDump();
        List dumpPathList=dao.getDumpPath();
        List numberList=dao.getFileNum();
        List logInfoList=dao.getLogInfo();
        
        int i=dumpList.size();
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
        FileHelper fh=new FileHelper();
        String datetime=df.format(new Date());
        while(--i>=0){
            fh.createPath(dumpPathList.get(i).toString());
            try{
                log.info(" Backup Startup --"+logInfoList.get(i).toString());
                //log.info(dumpList.get(i).toString());
                Runtime.getRuntime().exec(dumpList.get(i).toString()+datetime);
                log.info(" Backup Finished --"+logInfoList.get(i).toString());
                //System.out.println(dumpPathList.get(i));
            }catch(IOException e){
                log.error(e.toString());
            }
            //删除过期文件
            if(fh.getFileNum(dumpPathList.get(i).toString())>=Integer.parseInt(numberList.get(i).toString())){
                //System.out.println(fh.getOlderFile(dumpPathList.get(i).toString()));
                String filename=fh.getOlderFile(dumpPathList.get(i).toString());
                log.info("Prepare to delete expire "+filename);
                if(fh.deleteFolder(filename)){
                    log.info("Delete "+filename + " is sucess");
                }else{
                    log.warn("Delete "+filename + " is failure");
                }
            }
        }
    }
    
    public void multipleTreadBackup(DumpArrObject dao){
        ExecutorService poolbk = Executors.newFixedThreadPool(threads);
        List dumpList=dao.getDump();
        List dumpPathList=dao.getDumpPath();
        List numberList=dao.getFileNum();
        List logInfoList=dao.getLogInfo();
        
        int i=dumpList.size();
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
        String datetime=df.format(new Date());
        FileHelper fh=new FileHelper();
        while(--i>=0){
            //log.info(dumpList.get(i).toString());
            fh.createPath(dumpPathList.get(i).toString());
            BackupThread bt=new BackupThread(dumpList.get(i).toString(),datetime,this.log,logInfoList.get(i).toString());
            poolbk.execute(bt);
            //删除过期文件
            if(fh.getFileNum(dumpPathList.get(i).toString())>=Integer.parseInt(numberList.get(i).toString())){
                //System.out.println(fh.getOlderFile(dumpPathList.get(i).toString()));
                String filename=fh.getOlderFile(dumpPathList.get(i).toString());
                log.info("Prepare to delete expire "+filename);
                if(fh.deleteFolder(filename)){
                    log.info("Delete "+filename + " is sucess");
                }else{
                    log.warn("Delete "+filename + " is failure");
                }
            }
        }
        poolbk.shutdown();
        try{
            poolbk.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);//等待线程池结束，注销将不等待
        }catch(InterruptedException e){
            log.error(e.toString());
        }
    }
}
