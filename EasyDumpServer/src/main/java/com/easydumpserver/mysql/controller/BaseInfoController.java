/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mysql.controller;

import com.easydumpserver.mysql.BackupStrategy;
import com.easydumpserver.mysql.helper.MySQLHelper;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 *
 * @author sheriff
 */
public class BaseInfoController {
    private Locale myLocale = null;
    private ResourceBundle rb = null;
    MySQLHelper mysqlcom=null;
    List baseList=null;
    BackupStrategy bkStrategy=null;
    public BaseInfoController(){
        myLocale = Locale.getDefault(Locale.Category.FORMAT);
        rb = ResourceBundle.getBundle("config",myLocale);
        mysqlcom=new MySQLHelper();
        bkStrategy=new BackupStrategy();
    }
    
    public void getDumpBaseInfo(){
        //System.out.println(rb.getString("mysql.server"));
        mysqlcom.setURL(rb.getString("mysql.server"),rb.getString("mysql.port"),rb.getString("mysql.user"), rb.getString("mysql.password"));
        baseList=mysqlcom.queryAll("select * from easydump.databases");
        //Map<String, Object> aa=(Map)baseList.get(0);
        //System.out.println(aa.get("ip"));
    }
    
    public void getDumpString(){
        int i=baseList.size();
        List tableList=null;
        while(--i>=0){
            Map<String, Object> baseInfoMap=(Map)baseList.get(i);
            //System.out.println(baseInfoMap.get("id"));
            //String aa=baseInfoMap.get("backuptable").toString();
            //System.out.println(aa);
            if(Integer.parseInt(baseInfoMap.get("backuptable").toString())==1){
                tableList=mysqlcom.queryAll("select * from easydump.backuptable where databaseid="+baseInfoMap.get("backuptable").toString());
            }else if(Integer.parseInt(baseInfoMap.get("igonretable").toString())==1){
                tableList=mysqlcom.queryAll("select * from easydump.igonretable where databaseid="+baseInfoMap.get("backuptable").toString());
            }
            int j=Integer.parseInt(baseInfoMap.get("strategy").toString());
            switch(j){
                case 0:
                    addLockDBDump(baseInfoMap,tableList);
                    break;
                case 1:
                    addUnlockDBDump(baseInfoMap,tableList);
                    break;
                case 2:
                    addOnlyStructDump(baseInfoMap,tableList);
                    break;
                case 3:
                    addOnlyDataDump(baseInfoMap,tableList);
                    break;
                case 4:
                    addplitTablesDump(baseInfoMap,tableList);
                    break;
            }
        }
    }
    
   private void addLockDBDump(Map<String, Object> baseInfoMap,List tableList){
       
   }
   private void addUnlockDBDump(Map<String, Object> baseInfoMap,List tableList){
   
   }
   private void addOnlyStructDump(Map<String, Object> baseInfoMap,List tableList){
   
   }
   private void addOnlyDataDump(Map<String, Object> baseInfoMap,List tableList){
   
   }
   private void addplitTablesDump(Map<String, Object> baseInfoMap,List tableList){
   
   }
}
