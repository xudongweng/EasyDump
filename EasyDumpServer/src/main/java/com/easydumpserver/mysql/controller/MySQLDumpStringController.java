/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mysql.controller;

import com.easydumpserver.mysql.MySQLDumpStrategy;
import com.easydumpserver.helper.mysql.MySQLHelper;
import com.easydumpserver.mysql.model.DumpArrObject;
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
    
    StringBuilder sbDump=null;//组合备份策略字符串使用
    StringBuilder sbDumpPath=null;//组合备份路径使用
    
    DumpArrObject dao=null;
    
    
    
    public MySQLDumpStringController(){
        myLocale = Locale.getDefault(Locale.Category.FORMAT);
        rb = ResourceBundle.getBundle("config",myLocale);
        mysqlcom=new MySQLHelper();
        bkStrategy=new MySQLDumpStrategy();
        dao=new DumpArrObject();
        sbDump=new StringBuilder();
        sbDumpPath=new StringBuilder();
    }
    
    // <editor-fold defaultstate="collapsed" desc="组合字符串第1步">
    //组合字符串第1步
    public void setDumpBaseInfo(){
        //System.out.println(rb.getString("mysql.server"));
        mysqlcom.setURL(rb.getString("mysql.server"),rb.getString("mysql.port"),rb.getString("mysql.user"), rb.getString("mysql.password"));
        baseList=mysqlcom.queryAll("select * from easydump.databases where enable=1");
        //Map<String, Object> aa=(Map)baseList.get(0);
        //System.out.println(aa.get("ip"));
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="组合字符串第2步">
    //组合字符串第2步
    public void setDumpString(){
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
                    setLockDBDump(baseInfoMap,tableList);
                    break;
                case 1:
                    setUnlockDBDump(baseInfoMap,tableList);
                    break;
                case 2:
                    setOnlyStructDump(baseInfoMap,tableList);
                    break;
                case 3:
                    setOnlyDataDump(baseInfoMap,tableList);
                    break;
                case 4:
                    setSplitTablesDump(baseInfoMap,tableList);
                    break;
            }
        }
        if(tableList!=null)
            tableList.clear();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="返回组合字符串第3步">
    public DumpArrObject getDumpString(){
        return this.dao;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="组合每种备份策略的备份字符串">
    private void setLockDBDump(Map<String, Object> baseInfoMap,List tableList){
        this.sbDump.append(rb.getString("mysql.binpath"));
        this.sbDump.append(File.separator).append(baseInfoMap.get("backupcmd").toString()).append(" ");
        this.sbDump.append(bkStrategy.getLockDB(baseInfoMap.get("code").toString()));
        //组合数据库连接基本信息字符串
        this.sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
        this.sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
        //System.out.println(sbDump.toString());
        this.dao.addDump(this.sbDump.toString());
        this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                .append(baseInfoMap.get("ip").toString()).append(File.separator)
                .append(baseInfoMap.get("database").toString()).append(File.separator);
        this.dao.addDumpPath(this.sbDumpPath.toString());
        this.sbDump.delete(0, this.sbDump.length());
        this.sbDumpPath.delete(0, this.sbDumpPath.length());
    }
    
    private void setUnlockDBDump(Map<String, Object> baseInfoMap,List tableList){
        this.sbDump.append(rb.getString("mysql.binpath"));
        this.sbDump.append(File.separator).append(baseInfoMap.get("backupcmd").toString()).append(" ");
        this.sbDump.append(bkStrategy.getUnlockDB(baseInfoMap.get("code").toString()));
        //组合数据库连接基本信息字符串
        this.sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
        this.sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
        this.dao.addDump(this.sbDump.toString());
        this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                .append(baseInfoMap.get("ip").toString()).append(File.separator)
                .append(baseInfoMap.get("database").toString()).append(File.separator);
        this.dao.addDumpPath(this.sbDumpPath.toString());
        
        this.sbDump.delete(0, this.sbDump.length());
        this.sbDumpPath.delete(0, this.sbDumpPath.length());
    }
    
    private void setOnlyStructDump(Map<String, Object> baseInfoMap,List tableList){
        this.sbDump.append(rb.getString("mysql.binpath"));
        this.sbDump.append(File.separator).append(baseInfoMap.get("backupcmd").toString()).append(" ");
        this.sbDump.append(bkStrategy.getOnlyStruct(baseInfoMap.get("code").toString()));
        //组合数据库连接基本信息字符串
        this.sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
        this.sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
        this.dao.addDump(this.sbDump.toString());
        this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                .append(baseInfoMap.get("ip").toString()).append(File.separator)
                .append(baseInfoMap.get("database").toString()).append(File.separator);
        this.dao.addDumpPath(this.sbDumpPath.toString());
        
        this.sbDump.delete(0, this.sbDump.length());
        this.sbDumpPath.delete(0, this.sbDumpPath.length());
    }
    private void setOnlyDataDump(Map<String, Object> baseInfoMap,List tableList){
        this.sbDump.append(rb.getString("mysql.binpath"));
        this.sbDump.append(File.separator).append(baseInfoMap.get("backupcmd").toString()).append(" ");
        this.sbDump.append(bkStrategy.getOnlyData(baseInfoMap.get("code").toString()));
        //组合数据库连接基本信息字符串
        this.sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
        this.sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
        this.dao.addDump(this.sbDump.toString());
        this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                .append(baseInfoMap.get("ip").toString()).append(File.separator)
                .append(baseInfoMap.get("database").toString()).append(File.separator);
        this.dao.addDumpPath(this.sbDumpPath.toString());
        
        this.sbDump.delete(0, this.sbDump.length());
        this.sbDumpPath.delete(0, this.sbDumpPath.length());
    }
    
    private void setSplitTablesDump(Map<String, Object> baseInfoMap,List tableList){
        this.sbDump.append(rb.getString("mysql.binpath"));
        this.sbDump.append(File.separator).append(baseInfoMap.get("backupcmd").toString()).append(" ");
        this.sbDump.append(bkStrategy.getSplitTables(baseInfoMap.get("code").toString()));
        //组合数据库连接基本信息字符串
        this.sbDump.append("-h").append(baseInfoMap.get("ip").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
                .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
                .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString()).append(" ");
        //System.out.println(sbDump.toString());
        StringBuilder sbDumpChanger=new StringBuilder();
        if(Integer.parseInt(baseInfoMap.get("backuptable").toString())==1 && tableList.size()>0){ //分表备份仅备份指定表
            int i=tableList.size();
            while(--i>=0){
                sbDumpChanger.append(this.sbDump.toString());
                Map<String,Object> tbMap=(Map)tableList.get(i);
                sbDumpChanger.append(tbMap.get("tablename"));
                //System.out.println(sbDumpChanger.toString());
                this.dao.addDump(sbDumpChanger.toString());
                this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                        .append(baseInfoMap.get("ip").toString()).append(File.separator)
                        .append(baseInfoMap.get("database").toString()).append(File.separator)
                        .append(tbMap.get("tablename")).append(File.separator);
                this.dao.addDumpPath(this.sbDumpPath.toString());
                
                this.sbDumpPath.delete(0, this.sbDumpPath.length());
                sbDumpChanger.delete(0, sbDumpChanger.length());
            }

        }else if(Integer.parseInt(baseInfoMap.get("ignoretable").toString())==1 && tableList.size()>0){//分表备份指定表部分表不备份
            mysqlcom.setURL(baseInfoMap.get("ip").toString(),baseInfoMap.get("port").toString(),baseInfoMap.get("user").toString(), baseInfoMap.get("password").toString());
            List gettablelist=mysqlcom.getAllTables(baseInfoMap.get("database").toString());
            int i=tableList.size();
            int j=gettablelist.size();
            while(--j>=0){
                sbDumpChanger.append(this.sbDump.toString());
                int k=i;
                boolean check=true;
                while(--k>=0){
                    Map<String,Object> tbMap=(Map)tableList.get(k);
                    if(tbMap.get("tablename").equals(gettablelist.get(j))){//判断备份表是否与不需要备份表相同
                        check=false;
                        break;
                    }
                }
                if(check){//备份表与不需要备份表不相同，则备份
                    sbDumpChanger.append("\"").append(gettablelist.get(j)).append("\"");
                    //System.out.println(sbDumpChanger.toString());
                    this.dao.addDump(sbDumpChanger.toString());
                    this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                            .append(baseInfoMap.get("ip").toString()).append(File.separator)
                            .append(baseInfoMap.get("database").toString()).append(File.separator)
                            .append(gettablelist.get(j)).append(File.separator);
                    this.dao.addDumpPath(this.sbDumpPath.toString());
                
                    this.sbDumpPath.delete(0, this.sbDumpPath.length());
                    sbDumpChanger.delete(0, sbDumpChanger.length());
                }

            }
            gettablelist.clear();
        }else{//分表备份所有的表
            mysqlcom.setURL(baseInfoMap.get("ip").toString(),baseInfoMap.get("port").toString(),baseInfoMap.get("user").toString(), baseInfoMap.get("password").toString());
            List gettablelist=mysqlcom.getAllTables(baseInfoMap.get("database").toString());
            int j=gettablelist.size();
            while(--j>=0){
                sbDumpChanger.append(this.sbDump.toString());
                sbDumpChanger.append("\"").append(gettablelist.get(j)).append("\"");
                //System.out.println(sbDumpChanger.toString());
                this.dao.addDump(sbDumpChanger.toString());
                this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                    .append(baseInfoMap.get("ip").toString()).append(File.separator)
                    .append(baseInfoMap.get("database").toString()).append(File.separator)
                    .append(gettablelist.get(j)).append(File.separator);
                this.dao.addDumpPath(this.sbDumpPath.toString());
                
                this.sbDumpPath.delete(0, this.sbDumpPath.length());
                sbDumpChanger.delete(0, sbDumpChanger.length());
            }
            gettablelist.clear();
        }
        this.sbDump.delete(0, this.sbDump.length());
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
