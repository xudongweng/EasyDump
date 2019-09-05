/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver;

import com.easydumpserver.mysql.controller.MySQLDumpStringController;
import com.easydumpserver.mysql.controller.MySQLBackupController;
import com.easydumpserver.mysql.model.DumpArrObject;
import java.io.IOException;

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
        dao.clear();
    }
}
