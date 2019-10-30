/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver;

import com.easydumpserver.mongo.controller.MongoDumpStringController;
import com.easydumpserver.mongo.controller.MongoBackupController;
import com.easydumpserver.mongo.model.DumpArrObject;
import java.io.IOException;

/**
 *
 * @author sheriff
 */
public class EasyDumpTool {
    public static void main(String[] args) throws IOException{
        //System.out.println(EasyDumpTool.class.getResource("/"));
        MongoDumpStringController dumpctl=new MongoDumpStringController();
        dumpctl.setDumpBaseInfo();
        dumpctl.setDumpString();
        DumpArrObject dao=dumpctl.getDumpString();
        MongoBackupController bkctl=new MongoBackupController();
        if(bkctl.getThreadNum()==1){
            bkctl.singleTreadBackup(dao);
        }else{
            bkctl.multipleTreadBackup(dao);
        }
        dao.clear();
    }
}
