/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mysql.controller;

import com.easydumpserver.mysql.MySQLDumpStrategy;
import com.easydumpserver.mysql.helper.MySQLHelper;
import java.io.File;
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
    MySQLDumpStrategy bkStrategy=null;
    
    public MySQLDumpStringController(){
        myLocale = Locale.getDefault(Locale.Category.FORMAT);
        rb = ResourceBundle.getBundle("config",myLocale);
        mysqlcom=new MySQLHelper();
        bkStrategy=new MySQLDumpStrategy();
    }
    
    //组合字符串第1步
    public void getDumpBaseInfo(){
        //System.out.println(rb.getString("mysql.server"));
        mysqlcom.setURL(rb.getString("mysql.server"),rb.getString("mysql.port"),rb.getString("mysql.user"), rb.getString("mysql.password"));
        baseList=mysqlcom.queryAll("select * from easydump.databases where enable=1");
        //Map<String, Object> aa=(Map)baseList.get(0);
        //System.out.println(aa.get("ip"));
    }
    
    //组合字符串第2步
    public void getDumpString(){
        int i=baseList.size();
        List tableList=null;
        while(--i>=0){
            mysqlcom.setURL(rb.getString("mysql.server"),rb.getString("mysql.port"),rb.getString("mysql.user"), rb.getString("mysql.password"));//需要在分表备份调用后数据库访问参数重置
            Map<String, Object> baseInfoMap=(Map)baseList.get(i);
            //System.out.println(baseInfoMap.get("id"));
            //String aa=baseInfoMap.get("backuptable").toString();
            //System.out.println(aa);
            if(Integer.parseInt(baseInfoMap.get("backuptable").toString())==1){
                tableList=mysqlcom.queryAll("select * from easydump.backuptables where databaseid="+baseInfoMap.get("id").toString());
            }else if(Integer.parseInt(baseInfoMap.get("ignoretable").toString())==1){
                tableList=mysqlcom.queryAll("select * from easydump.ignoretables where databaseid="+baseInfoMap.get("id").toString());
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
                    addSplitTablesDump(baseInfoMap,tableList);
                    break;
            }
        }
    }
    
   // <editor-fold defaultstate="collapsed" desc="组合每种备份策略的备份字符串">
   private void addLockDBDump(Map<String, Object> baseInfoMap,List tableList){
       StringBuilder sbDump=new StringBuilder(rb.getString("mysql.binpath"));
       sbDump.append(File.separator).append(baseInfoMap.get("backupcmd").toString()).append(" ");
       sbDump.append(bkStrategy.getLockDB(baseInfoMap.get("code").toString()));
       //组合数据库连接基本信息字符串
       sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
       sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
       
       System.out.println(sbDump.toString());
   }
   private void addUnlockDBDump(Map<String, Object> baseInfoMap,List tableList){
       StringBuilder sbDump=new StringBuilder(rb.getString("mysql.binpath"));
       sbDump.append(File.separator).append(baseInfoMap.get("backupcmd").toString()).append(" ");
       sbDump.append(bkStrategy.getUnlockDB(baseInfoMap.get("code").toString()));
       //组合数据库连接基本信息字符串
       sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
       sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
       
       System.out.println(sbDump.toString());
   }
   private void addOnlyStructDump(Map<String, Object> baseInfoMap,List tableList){
       StringBuilder sbDump=new StringBuilder(rb.getString("mysql.binpath"));
       sbDump.append(File.separator).append(baseInfoMap.get("backupcmd").toString()).append(" ");
       sbDump.append(bkStrategy.getOnlyStruct(baseInfoMap.get("code").toString()));
       //组合数据库连接基本信息字符串
       sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
       sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
       
       System.out.println(sbDump.toString());
   }
   private void addOnlyDataDump(Map<String, Object> baseInfoMap,List tableList){
       StringBuilder sbDump=new StringBuilder(rb.getString("mysql.binpath"));
       sbDump.append(File.separator).append(baseInfoMap.get("backupcmd").toString()).append(" ");
       sbDump.append(bkStrategy.getOnlyData(baseInfoMap.get("code").toString()));
       //组合数据库连接基本信息字符串
       sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
       sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
       System.out.println(sbDump.toString());
   }
   private void addSplitTablesDump(Map<String, Object> baseInfoMap,List tableList){
       StringBuilder sbDump=new StringBuilder(rb.getString("mysql.binpath"));
       sbDump.append(File.separator).append(baseInfoMap.get("backupcmd").toString()).append(" ");
       sbDump.append(bkStrategy.getSplitTables(baseInfoMap.get("code").toString()));
       //组合数据库连接基本信息字符串
       sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
       //System.out.println(sbDump.toString());
       mysqlcom.setURL(baseInfoMap.get("ip").toString(),baseInfoMap.get("port").toString(),baseInfoMap.get("user").toString(), baseInfoMap.get("password").toString());
       List gettablelist=mysqlcom.getAllTables(baseInfoMap.get("database").toString());
       System.out.println(sbDump.toString());
       if(Integer.parseInt(baseInfoMap.get("backuptable").toString())==1 && tableList.size()>0){ //分表备份仅备份指定表
           
       }else if(Integer.parseInt(baseInfoMap.get("ignoretable").toString())==1 && tableList.size()>0){//分表备份指定表部分表不备份
       
       }else{//分表备份所有的表
       
       }
   }
   // </editor-fold>

   // <editor-fold defaultstate="collapsed" desc="设置单独备份或忽略的表的字符串">
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
               sbDump.append(" --ignore-table=").append(baseInfoMap.get("database").toString()).append(".").append("\"").append(tableListMap.get("tablename").toString()).append("\"");
           }
       }
       return sbDump.toString();
   }
   // </editor-fold>
}
