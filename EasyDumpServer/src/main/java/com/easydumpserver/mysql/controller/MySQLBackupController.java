/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mysql.controller;

import com.easydumpserver.helper.compress.ZipUtilsHelper;
import com.easydumpserver.helper.file.FileHelper;
import com.easydumpserver.helper.mysql.MySQLHelper;
import com.easydumpserver.mysql.controller.thread.BackupThread;
import com.easydumpserver.mysql.model.DumpArrObject;
import java.io.IOException;
import java.io.InputStream;
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
public class MySQLBackupController {
    private Logger log=null;
    private Locale myLocale = null;
    private ResourceBundle rb = null;
    private int threads=1;
    public MySQLBackupController(){
        this.log=Logger.getLogger(MySQLBackupController.class);
        myLocale = Locale.getDefault(Locale.Category.FORMAT);
        this.rb= ResourceBundle.getBundle("config",myLocale);
        this.threads=Integer.parseInt(rb.getString("threads"));
    }
    
    public int getThreadNum(){
        return this.threads;
    }
    
    public void singleTreadBackup(DumpArrObject dao){
        List dumpList=dao.getArrDump();
        List dumpPathList=dao.getDumpPath();
        int i=dumpList.size();
        ZipUtilsHelper zuh=new ZipUtilsHelper();
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
        FileHelper fh=new FileHelper();
        while(--i>=0){
            try{
                String datetime=df.format(new Date());
                InputStream in = Runtime.getRuntime().exec(dumpList.get(i).toString()).getInputStream();
                fh.createPath(dumpPathList.get(i).toString());
                zuh.zipStreamCompress(in, dumpPathList.get(i).toString(), datetime+".sql",datetime);
                //System.out.println(dumpPathList.get(i));
            }catch(IOException e){
                log.error(e.toString());
            }
        }
    }
    
    public void multipleTreadBackup(DumpArrObject dao){
        ExecutorService poolbk = Executors.newFixedThreadPool(threads);
        List dumpList=dao.getArrDump();
        List dumpPathList=dao.getDumpPath();
        int i=dumpList.size();
        ZipUtilsHelper zuh=new ZipUtilsHelper();
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
        FileHelper fh=new FileHelper();
        while(--i>=0){
            fh.createPath(dumpPathList.get(i).toString());
            BackupThread bt=new BackupThread(dumpList.get(i).toString(),dumpPathList.get(i).toString(),df.format(new Date()),zuh,this.log);
            poolbk.execute(bt);
        }
        poolbk.shutdown();
        try{
            poolbk.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);//等待线程池结束，注销将不等待
        }catch(InterruptedException e){
            log.error(e.toString());
        }
    }
}
