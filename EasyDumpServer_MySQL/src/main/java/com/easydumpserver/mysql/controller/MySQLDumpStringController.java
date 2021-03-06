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
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class MySQLDumpStringController {
    private Locale myLocale = null;
    private ResourceBundle rb = null;
    private MySQLHelper mysqlcom=null;
    private List baseList=null;
    private MySQLDumpStrategy bkStrategy=null;
    
    private StringBuilder sbDump=null;//组合备份策略字符串使用
    private StringBuilder sbDumpPath=null;//组合备份路径使用
    private StringBuilder sbInfo=null;//日志信息
    
    
    private DumpArrObject dao=null;
    private Logger log=null;
    
    
    public MySQLDumpStringController(){
        myLocale = Locale.getDefault(Locale.Category.FORMAT);
        rb = ResourceBundle.getBundle("config",myLocale);
        mysqlcom=new MySQLHelper();
        bkStrategy=new MySQLDumpStrategy();
        dao=new DumpArrObject();
        sbDump=new StringBuilder();
        sbDumpPath=new StringBuilder();
        this.sbInfo=new StringBuilder();
        this.log=Logger.getLogger(MySQLDumpStringController.class);
    }
    
    // <editor-fold defaultstate="collapsed" desc="组合字符串第1步">
    //组合字符串第1步
    public void setDumpBaseInfo(){
        //System.out.println(rb.getString("mysql.server"));
        mysqlcom.setURL(rb.getString("mysql.server"),rb.getString("mysql.port"),rb.getString("mysql.user"), rb.getString("mysql.password"));
        baseList=mysqlcom.queryAll("select * from "+rb.getString("mysql.database")+".dbs where enable=1");
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
                tableList=mysqlcom.queryAll("select * from "+rb.getString("mysql.database")+".backuptables where dbid="+baseInfoMap.get("id").toString());
            }else if(Integer.parseInt(baseInfoMap.get("ignoretable").toString())==1){
                tableList=mysqlcom.queryAll("select * from "+rb.getString("mysql.database")+".ignoretables where dbid="+baseInfoMap.get("id").toString());
            }
            //检查备份库是否连通
            mysqlcom.setURL(baseInfoMap.get("host").toString(),baseInfoMap.get("port").toString(),
                    baseInfoMap.get("user").toString(), baseInfoMap.get("password").toString());
            boolean conn=mysqlcom.getConnection();
            if(conn){
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
    //锁表备份
    private void setLockDBDump(Map<String, Object> baseInfoMap,List tableList){
        if(!rb.getString("mysql.binpath").equals(""))
            this.sbDump.append(rb.getString("mysql.binpath")).append(File.separator);
        this.sbDump.append(baseInfoMap.get("backupcmd").toString()).append(" ");
        this.sbDump.append(bkStrategy.getLockDB(baseInfoMap.get("code").toString()));
        //组合数据库连接基本信息字符串
        this.sbDump.append("-h").append(baseInfoMap.get("host").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
        this.sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
        //System.out.println(sbDump.toString());
        this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                .append(baseInfoMap.get("host").toString()).append(File.separator)
                .append(baseInfoMap.get("database").toString()).append(File.separator);
        this.sbInfo.append(" [host]:").append(baseInfoMap.get("host").toString()).append(",")
                .append("[db]:").append(baseInfoMap.get("database").toString());
        
        this.dao.addDump(this.sbDump.toString());        
        this.dao.addDumpPath(this.sbDumpPath.toString());
        this.dao.addLogInfo(this.sbInfo.toString());
        this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));

        this.sbDump.delete(0, this.sbDump.length());
        this.sbDumpPath.delete(0, this.sbDumpPath.length());
        this.sbInfo.delete(0,this.sbInfo.length());
    }
    //不锁表备份
    private void setUnlockDBDump(Map<String, Object> baseInfoMap,List tableList){
        if(!rb.getString("mysql.binpath").equals(""))
            this.sbDump.append(rb.getString("mysql.binpath")).append(File.separator);
        this.sbDump.append(baseInfoMap.get("backupcmd").toString()).append(" ");
        this.sbDump.append(bkStrategy.getUnlockDB(baseInfoMap.get("code").toString()));
        //组合数据库连接基本信息字符串
        this.sbDump.append("-h").append(baseInfoMap.get("host").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
        this.sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
        this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                .append(baseInfoMap.get("host").toString()).append(File.separator)
                .append(baseInfoMap.get("database").toString()).append(File.separator);
        this.sbInfo.append(" [host]").append(baseInfoMap.get("host").toString()).append(",")
                .append("[db]").append(baseInfoMap.get("database").toString());
        
        this.dao.addDump(this.sbDump.toString());        
        this.dao.addDumpPath(this.sbDumpPath.toString());
        this.dao.addLogInfo(this.sbInfo.toString());
        this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));
        
        this.sbDump.delete(0, this.sbDump.length());
        this.sbDumpPath.delete(0, this.sbDumpPath.length());
        this.sbInfo.delete(0,this.sbInfo.length());
    }
    //仅备份表结构
    private void setOnlyStructDump(Map<String, Object> baseInfoMap,List tableList){
        if(!rb.getString("mysql.binpath").equals(""))
            this.sbDump.append(rb.getString("mysql.binpath")).append(File.separator);
        this.sbDump.append(baseInfoMap.get("backupcmd").toString()).append(" ");
        this.sbDump.append(bkStrategy.getOnlyStruct(baseInfoMap.get("code").toString()));
        //组合数据库连接基本信息字符串
        this.sbDump.append("-h").append(baseInfoMap.get("host").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
        this.sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
        this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                .append(baseInfoMap.get("host").toString()).append(File.separator)
                .append(baseInfoMap.get("database").toString()).append(File.separator);
        this.sbInfo.append(" [host]:").append(baseInfoMap.get("host").toString()).append(",")
                .append("[db]:").append(baseInfoMap.get("database").toString());
        
        this.dao.addDump(this.sbDump.toString());        
        this.dao.addDumpPath(this.sbDumpPath.toString());
        this.dao.addLogInfo(this.sbInfo.toString());
        this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));
        
        this.sbDump.delete(0, this.sbDump.length());
        this.sbDumpPath.delete(0, this.sbDumpPath.length());
        this.sbInfo.delete(0,this.sbInfo.length());
    }
    //仅备份表数据，insert语句
    private void setOnlyDataDump(Map<String, Object> baseInfoMap,List tableList){
        if(!rb.getString("mysql.binpath").equals(""))
            this.sbDump.append(rb.getString("mysql.binpath")).append(File.separator);
        this.sbDump.append(baseInfoMap.get("backupcmd").toString()).append(" ");
        this.sbDump.append(bkStrategy.getOnlyData(baseInfoMap.get("code").toString()));
        //组合数据库连接基本信息字符串
        this.sbDump.append("-h").append(baseInfoMap.get("host").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
               .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
               .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString());
        this.sbDump.append(this.setBkIgnTable(baseInfoMap, tableList));
        this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                .append(baseInfoMap.get("host").toString()).append(File.separator)
                .append(baseInfoMap.get("database").toString()).append(File.separator);
        this.sbInfo.append(" [host]:").append(baseInfoMap.get("host").toString()).append(",")
                .append("[db]:").append(baseInfoMap.get("database").toString());
        
        this.dao.addDump(this.sbDump.toString());        
        this.dao.addDumpPath(this.sbDumpPath.toString());
        this.dao.addLogInfo(this.sbInfo.toString());
        this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));
        
        this.sbDump.delete(0, this.sbDump.length());
        this.sbDumpPath.delete(0, this.sbDumpPath.length());
        this.sbInfo.delete(0,this.sbInfo.length());
    }
    //分表备份
    private void setSplitTablesDump(Map<String, Object> baseInfoMap,List tableList){
        if(!rb.getString("mysql.binpath").equals(""))
            this.sbDump.append(rb.getString("mysql.binpath")).append(File.separator);
        this.sbDump.append(baseInfoMap.get("backupcmd").toString()).append(" ");
        this.sbDump.append(bkStrategy.getSplitTables(baseInfoMap.get("code").toString()));
        //组合数据库连接基本信息字符串
        this.sbDump.append("-h").append(baseInfoMap.get("host").toString()).append(" --user=").append(baseInfoMap.get("user").toString())
                .append(" --password=").append(baseInfoMap.get("password").toString()).append(" --port=")
                .append(baseInfoMap.get("port").toString()).append(" ").append(baseInfoMap.get("database").toString()).append(" ");
        //System.out.println(sbDump.toString());
        StringBuilder sbDumpChanger=new StringBuilder();
        // <editor-fold defaultstate="collapsed" desc="单独备份存储过程">
        sbDumpChanger.append(this.sbDump.toString()).append(this.bkStrategy.getProduce(baseInfoMap.get("code").toString()));
        this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                        .append(baseInfoMap.get("host").toString()).append(File.separator)
                        .append(baseInfoMap.get("database").toString()).append(File.separator)
                        .append("produce").append(File.separator);
        this.sbInfo.append(" [host]:").append(baseInfoMap.get("host").toString()).append(",")
                     .append("[db]:").append(baseInfoMap.get("database").toString()).append(",")
                     .append("[produce]:").append("produce");
        this.dao.addDump(sbDumpChanger.toString());        
        this.dao.addDumpPath(this.sbDumpPath.toString());
        this.dao.addLogInfo(this.sbInfo.toString());
        this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));
        
        this.sbDumpPath.delete(0, this.sbDumpPath.length());
        sbDumpChanger.delete(0, sbDumpChanger.length());
        this.sbInfo.delete(0,this.sbInfo.length());
        // </editor-fold>
        if(Integer.parseInt(baseInfoMap.get("backuptable").toString())==1 && tableList.size()>0){ //分表备份仅备份指定表
            int i=tableList.size();
            while(--i>=0){
                sbDumpChanger.append(this.sbDump.toString());
                Map<String,Object> tbMap=(Map)tableList.get(i);
                sbDumpChanger.append(tbMap.get("tablename"));
                //System.out.println(sbDumpChanger.toString());
                this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                        .append(baseInfoMap.get("host").toString()).append(File.separator)
                        .append(baseInfoMap.get("database").toString()).append(File.separator)
                        .append(tbMap.get("tablename")).append(File.separator);
                this.sbInfo.append(" [host]:").append(baseInfoMap.get("host").toString()).append(",")
                     .append("[db]:").append(baseInfoMap.get("database").toString()).append(",")
                     .append("[table]:").append(tbMap.get("tablename"));
                
                this.dao.addDump(sbDumpChanger.toString());        
                this.dao.addDumpPath(this.sbDumpPath.toString());
                this.dao.addLogInfo(this.sbInfo.toString());
                this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));
                
                this.sbDumpPath.delete(0, this.sbDumpPath.length());
                sbDumpChanger.delete(0, sbDumpChanger.length());
                this.sbInfo.delete(0,this.sbInfo.length());
            }

        }else if(Integer.parseInt(baseInfoMap.get("ignoretable").toString())==1 && tableList.size()>0){//分表备份指定表部分表不备份
            /*
            mysqlcom.setURL(baseInfoMap.get("host").toString(),baseInfoMap.get("port").toString(),baseInfoMap.get("user").toString(), baseInfoMap.get("password").toString());
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
                    this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                            .append(baseInfoMap.get("host").toString()).append(File.separator)
                            .append(baseInfoMap.get("database").toString()).append(File.separator)
                            .append(gettablelist.get(j)).append(File.separator);
                    this.sbInfo.append(" [host]:").append(baseInfoMap.get("host").toString()).append(",")
                            .append("[db]:").append(baseInfoMap.get("database").toString()).append(",")
                            .append("[table]:").append(gettablelist.get(j));
                    
                    this.dao.addDump(sbDumpChanger.toString());        
                    this.dao.addDumpPath(this.sbDumpPath.toString());
                    this.dao.addLogInfo(this.sbInfo.toString());
                    this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));
                    
                    this.sbDumpPath.delete(0, this.sbDumpPath.length());
                    sbDumpChanger.delete(0, sbDumpChanger.length());
                    this.sbInfo.delete(0,this.sbInfo.length());
                }

            }
            gettablelist.clear();
            */
            mysqlcom.setURL(baseInfoMap.get("host").toString(),baseInfoMap.get("port").toString(),baseInfoMap.get("user").toString(), baseInfoMap.get("password").toString());
            List gettablelist=mysqlcom.getAllTables(baseInfoMap.get("database").toString());
            mysqlcom.setURL(rb.getString("mysql.server"),rb.getString("mysql.port"),rb.getString("mysql.user"), rb.getString("mysql.password"));
            int j=gettablelist.size();
            while(--j>=0){
                sbDumpChanger.append(this.sbDump.toString());
                int row=mysqlcom.querySize("SELECT * FROM "+rb.getString("mysql.database")+".ignoretables WHERE dbid="+baseInfoMap.get("id").toString()+" AND tablename='"
                +gettablelist.get(j)+"'");
                if(row==0){
                    sbDumpChanger.append(gettablelist.get(j));
                    //System.out.println(sbDumpChanger.toString());
                    this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                            .append(baseInfoMap.get("host").toString()).append(File.separator)
                            .append(baseInfoMap.get("database").toString()).append(File.separator)
                            .append(gettablelist.get(j)).append(File.separator);
                    this.sbInfo.append(" [host]:").append(baseInfoMap.get("host").toString()).append(",")
                            .append("[db]:").append(baseInfoMap.get("database").toString()).append(",")
                            .append("[table]:").append(gettablelist.get(j));
                    
                    this.dao.addDump(sbDumpChanger.toString());        
                    this.dao.addDumpPath(this.sbDumpPath.toString());
                    this.dao.addLogInfo(this.sbInfo.toString());
                    this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));
                    
                    this.sbDumpPath.delete(0, this.sbDumpPath.length());
                    sbDumpChanger.delete(0, sbDumpChanger.length());
                    this.sbInfo.delete(0,this.sbInfo.length());
                }
            }
        }else{//分表备份所有的表
            mysqlcom.setURL(baseInfoMap.get("host").toString(),baseInfoMap.get("port").toString(),baseInfoMap.get("user").toString(), baseInfoMap.get("password").toString());
            List gettablelist=mysqlcom.getAllTables(baseInfoMap.get("database").toString());
            int j=gettablelist.size();
            while(--j>=0){
                sbDumpChanger.append(this.sbDump.toString());
                sbDumpChanger.append(gettablelist.get(j));
                //System.out.println(sbDumpChanger.toString());
                this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                    .append(baseInfoMap.get("host").toString()).append(File.separator)
                    .append(baseInfoMap.get("database").toString()).append(File.separator)
                    .append(gettablelist.get(j)).append(File.separator);
                this.sbInfo.append(" [host]:").append(baseInfoMap.get("host").toString()).append(",")
                    .append("[db]:").append(baseInfoMap.get("database").toString()).append(",")
                    .append("[table]:").append(gettablelist.get(j));
                
                this.dao.addDump(sbDumpChanger.toString());        
                this.dao.addDumpPath(this.sbDumpPath.toString());
                this.dao.addLogInfo(this.sbInfo.toString());
                this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));
                
                this.sbDumpPath.delete(0, this.sbDumpPath.length());
                sbDumpChanger.delete(0, sbDumpChanger.length());
                this.sbInfo.delete(0,this.sbInfo.length());
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
                sbDump.append(" ").append(tableListMap.get("tablename").toString());
            }
        }else if(Integer.parseInt(baseInfoMap.get("ignoretable").toString())==1 && tableList.size()>0){
            int i=tableList.size();
            while(--i>=0){
                Map<String, Object> tableListMap=(Map)tableList.get(i);
                sbDump.append(" --ignore-table=").append(baseInfoMap.get("database").toString()).append(".").append(tableListMap.get("tablename").toString());
            }
        }
        return sbDump.toString();
    }
    // </editor-fold>
}
