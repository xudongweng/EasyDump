/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver;

import com.easydumpserver.mysql.controller.MySQLDumpStringController;
import com.easydumpserver.helper.compress.ZipUtilsHelper;
import com.easydumpserver.helper.file.FileHelper;
import com.easydumpserver.mysql.controller.MySQLBackupController;
import com.easydumpserver.mysql.model.DumpArrObject;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author sheriff
 */
public class EasyDumpTool {
    public static void main(String[] args) throws IOException{
        
        MySQLDumpStringController dumpctl=new MySQLDumpStringController();
        dumpctl.setDumpBaseInfo();
        dumpctl.setDumpString();
        DumpArrObject dao=dumpctl.getDumpString();
        MySQLBackupController bkctl=new MySQLBackupController();
        if(bkctl.getThreadNum()==1){
            bkctl.singleTreadBackup(dao);
        }else{
            bkctl.multipleTreadBackup(dao);
        }
    }
}
