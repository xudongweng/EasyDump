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
public class MySQLDumpStringController {
    private Locale myLocale = null;
    private ResourceBundle rb = null;
    MySQLHelper mysqlcom=null;
    List baseList=null;
    BackupStrategy bkStrategy=null;
    
    public MySQLDumpStringController(){
        myLocale = Locale.getDefault(Locale.Category.FORMAT);
        rb = ResourceBundle.getBundle("config",myLocale);
        mysqlcom=new MySQLHelper();
        bkStrategy=new BackupStrategy();
    }
    //first step
    public void getDumpBaseInfo(){
        //System.out.println(rb.getString("mysql.server"));
        mysqlcom.setURL(rb.getString("mysql.server"),rb.getString("mysql.port"),rb.getString("mysql.user"), rb.getString("mysql.password"));
        baseList=mysqlcom.queryAll("select * from easydump.databases where enable=1");
        //Map<String, Object> aa=(Map)baseList.get(0);
        //System.out.println(aa.get("ip"));
    }
    //second step
    public void getDumpString(){
        int i=baseList.size();
        List tableList=null;
        while(--i>=0){
            Map<String, Object> baseInfoMap=(Map)baseList.get(i);
            //System.out.println(baseInfoMap.get("id"));
            //String aa=baseInfoMap.get("backuptable").toString();
            //System.out.println(aa);
            if(Integer.parseInt(baseInfoMap.get("backuptable").toString())==1){
                tableList=mysqlcom.queryAll("select * from easydump.backuptables where databaseid="+baseInfoMap.get("id").toString());
            }else if(Integer.parseInt(baseInfoMap.get("igonretable").toString())==1){
                tableList=mysqlcom.queryAll("select * from easydump.igonretables where databaseid="+baseInfoMap.get("id").toString());
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
       StringBuilder sbDump=new StringBuilder(bkStrategy.getLockDB(baseInfoMap.get("code").toString()));
       //组合数据库连接基本信息字符串
       sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
       sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
       
       System.out.println(sbDump.toString());
   }
   private void addUnlockDBDump(Map<String, Object> baseInfoMap,List tableList){
       StringBuilder sbDump=new StringBuilder(bkStrategy.getUnlockDB(baseInfoMap.get("code").toString()));
       //组合数据库连接基本信息字符串
       sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString());
       sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
       
       System.out.println(sbDump.toString());
   }
   private void addOnlyStructDump(Map<String, Object> baseInfoMap,List tableList){
       StringBuilder sbDump=new StringBuilder(bkStrategy.getOnlyStruct(baseInfoMap.get("code").toString()));
       //组合数据库连接基本信息字符串
       sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString());
       sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
       
       System.out.println(sbDump.toString());
   }
   private void addOnlyDataDump(Map<String, Object> baseInfoMap,List tableList){
       StringBuilder sbDump=new StringBuilder(bkStrategy.getOnlyData(baseInfoMap.get("code").toString()));
       //组合数据库连接基本信息字符串
       sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString());
       sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
       
       System.out.println(sbDump.toString());
   }
   private void addplitTablesDump(Map<String, Object> baseInfoMap,List tableList){
       StringBuilder sbDump=new StringBuilder(bkStrategy.getSplitTables(baseInfoMap.get("code").toString()));
       //组合数据库连接基本信息字符串
       sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString());
   }
   
   //设置单独备份或忽略的表的字符串
   private String setBkIgnTable(Map<String, Object> baseInfoMap,List tableList){
       StringBuilder sbDump=new StringBuilder();
       if(Integer.parseInt(baseInfoMap.get("backuptable").toString())==1 && tableList.size()>0){
           int i=tableList.size();
           sbDump.append(" --tables");
           while(--i>=0){
               Map<String, Object> tableListMap=(Map)tableList.get(i);
               sbDump.append(" ").append("\"").append(tableListMap.get("tablename").toString()).append("\"");
           }
       }else if(Integer.parseInt(baseInfoMap.get("ignoretable").toString())==1 && tableList.size()>0){
           int i=tableList.size();
           while(--i>=0){
               Map<String, Object> tableListMap=(Map)tableList.get(i);
               sbDump.append(" --ignore-table=").append(tableListMap.get("database").toString()).append(".").append("\"").append(tableListMap.get("tablename").toString()).append("\"");
           }
       }
       return sbDump.toString();
   }
}
