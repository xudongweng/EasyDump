/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mongo.controller;

import com.easydumpserver.helper.mongo.MongoHelper;
import com.easydumpserver.helper.mysql.MySQLHelper;
import com.easydumpserver.mongo.model.DumpArrObject;
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
public class MongoDumpStringController {
    private Locale myLocale = null;
    private ResourceBundle rb = null;
    private MySQLHelper mysqlcom=null;
    private MongoHelper mongocom=null;
    private List baseList=null;
    
    private StringBuilder sbDump=null;//组合备份策略字符串使用
    private StringBuilder sbDumpPath=null;//组合备份路径使用
    private StringBuilder sbInfo=null;//日志信息
    
    
    private DumpArrObject dao=null;
    private Logger log=null;
    
    
    public MongoDumpStringController(){
        myLocale = Locale.getDefault(Locale.Category.FORMAT);
        rb = ResourceBundle.getBundle("config",myLocale);
        mysqlcom=new MySQLHelper();
        mongocom=new MongoHelper();
        dao=new DumpArrObject();
        sbDump=new StringBuilder();
        sbDumpPath=new StringBuilder();
        this.sbInfo=new StringBuilder();
        this.log=Logger.getLogger(MongoDumpStringController.class);
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
        Map<String, String> mongotbmap=null;
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
            //设置mongodb的url，有用户名和密码的情况下，url的数据库名为验证数据库名
            if(baseInfoMap.get("user").toString().equals("")){
                mongocom.setUrl(baseInfoMap.get("host").toString()+":"+baseInfoMap.get("port").toString(), baseInfoMap.get("database").toString());
                mongotbmap=mongocom.getTables();
            }
            else{
                mongocom.setUrl(baseInfoMap.get("user").toString()+":"+baseInfoMap.get("password").toString()+"@"+baseInfoMap.get("host").toString()+":"+baseInfoMap.get("port").toString()
                        , baseInfoMap.get("authdb").toString());
                mongotbmap=mongocom.getTables(baseInfoMap.get("database").toString());
            }
            //判断mongodb确实有需要备份的集合
            if(mongotbmap!=null){
                if(mongotbmap.size()>0){
                    int j=Integer.parseInt(baseInfoMap.get("strategy").toString());
                    switch(j){
                        case 0:
                            setUnlockDBDump(baseInfoMap,tableList,mongotbmap);
                            break;
                        case 1:
                            setSplitTablesDump(baseInfoMap,tableList,mongotbmap);
                            break;
                    }
                    mongotbmap.clear();
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
    //不锁表备份
    private void setUnlockDBDump(Map<String, Object> baseInfoMap,List tableList,Map<String, String> mongotbmap){
        if(!rb.getString("mongo.binpath").equals(""))
            this.sbDump.append(rb.getString("mongo.binpath")).append(File.separator);
        this.sbDump.append(baseInfoMap.get("backupcmd").toString()).append(" ");
        //组合数据库连接基本信息字符串
        if(baseInfoMap.get("user").toString().equals(""))
            this.sbDump.append("-h ").append(baseInfoMap.get("host").toString()).append(" --port ").append(baseInfoMap.get("port").toString())
                   .append(" -d ").append(baseInfoMap.get("database").toString()).append(" --gzip ");
        else
            this.sbDump.append("-h ").append(baseInfoMap.get("host").toString()).append(" -u ").append(baseInfoMap.get("user").toString())
                   .append(" -p ").append(baseInfoMap.get("password").toString()).append(" --port ").append(baseInfoMap.get("port").toString())
                   .append(baseInfoMap.get(" --authenticationDatabase ").toString()).append(baseInfoMap.get("authdb").toString())
                   .append(" -d ").append(baseInfoMap.get("database").toString()).append(" --gzip ");
        this.sbDump.append(this.setBkIgnTable(baseInfoMap, tableList,mongotbmap));
        this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                .append(baseInfoMap.get("host").toString()).append(File.separator);
        this.sbInfo.append(" [host]").append(baseInfoMap.get("host").toString()).append(",")
                .append("[db]").append(baseInfoMap.get("database").toString());
        
        this.dao.addDump(this.sbDump.append(" -o ").append(this.sbDumpPath.toString()).toString());        
        this.dao.addDumpPath(this.sbDumpPath.toString());
        this.dao.addLogInfo(this.sbInfo.toString());
        this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));
        
        this.sbDump.delete(0, this.sbDump.length());
        this.sbDumpPath.delete(0, this.sbDumpPath.length());
        this.sbInfo.delete(0,this.sbInfo.length());
    }

    //分表备份
    private void setSplitTablesDump(Map<String, Object> baseInfoMap,List tableList,Map<String, String> mongotbmap){
        if(!rb.getString("mongo.binpath").equals(""))
            this.sbDump.append(rb.getString("mongo.binpath")).append(File.separator);
        this.sbDump.append(baseInfoMap.get("backupcmd").toString()).append(" ");
        //组合数据库连接基本信息字符串
        if(baseInfoMap.get("user").toString().equals(""))
            this.sbDump.append("-h ").append(baseInfoMap.get("host").toString()).append(" --port ").append(baseInfoMap.get("port").toString())
                   .append(" -d ").append(baseInfoMap.get("database").toString()).append(" --gzip ");
        else
            this.sbDump.append("-h ").append(baseInfoMap.get("host").toString()).append(" -u ").append(baseInfoMap.get("user").toString())
                   .append(" -p ").append(baseInfoMap.get("password").toString()).append(" --port ").append(baseInfoMap.get("port").toString())
                   .append(baseInfoMap.get(" --authenticationDatabase ").toString()).append(baseInfoMap.get("authdb").toString())
                   .append(" -d ").append(baseInfoMap.get("database").toString()).append(" --gzip ");
        //System.out.println(sbDump.toString());
        StringBuilder sbDumpChanger=new StringBuilder();

        if(Integer.parseInt(baseInfoMap.get("backuptable").toString())==1 && tableList.size()>0){ //分表备份仅备份指定表
            int i=tableList.size();
            while(--i>=0){
                sbDumpChanger.append(this.sbDump.toString());
                Map<String,Object> tbMap=(Map)tableList.get(i);
                sbDumpChanger.append(" -c ").append(tbMap.get("tablename")).append(" ");
                //System.out.println(sbDumpChanger.toString());
                this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                        .append(baseInfoMap.get("host").toString()).append(File.separator);
                this.sbInfo.append(" [host]:").append(baseInfoMap.get("host").toString()).append(",")
                     .append("[db]:").append(baseInfoMap.get("database").toString()).append(",")
                     .append("[table]:").append(tbMap.get("tablename"));
                
                this.dao.addDump(sbDumpChanger.append(" -o ").append(this.sbDumpPath.toString()).toString());        
                this.dao.addDumpPath(this.sbDumpPath.toString());
                this.dao.addLogInfo(this.sbInfo.toString());
                this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));
                
                this.sbDumpPath.delete(0, this.sbDumpPath.length());
                sbDumpChanger.delete(0, sbDumpChanger.length());
                this.sbInfo.delete(0,this.sbInfo.length());
            }

        }else if(Integer.parseInt(baseInfoMap.get("ignoretable").toString())==1 && tableList.size()>0){//分表备份指定表部分表不备份
            mysqlcom.setURL(rb.getString("mysql.server"),rb.getString("mysql.port"),rb.getString("mysql.user"), rb.getString("mysql.password"));
            int i=tableList.size();
            while(--i>=0){
                Map<String, Object> tableListMap=(Map)tableList.get(i);
                //sbDump.append(" ").append(tableListMap.get("tablename").toString());
                mongotbmap.remove(tableListMap.get("tablename").toString());
            }
            for(String t:mongotbmap.keySet()){
                sbDumpChanger.append(this.sbDump.toString());
                sbDumpChanger.append(" -c ").append(t).append(" ");
                this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                        .append(baseInfoMap.get("host").toString()).append(File.separator);
                this.sbInfo.append(" [host]:").append(baseInfoMap.get("host").toString()).append(",")
                        .append("[db]:").append(baseInfoMap.get("database").toString()).append(",")
                        .append("[table]:").append(t);
                this.dao.addDump(sbDumpChanger.append(" -o ").append(this.sbDumpPath.toString()).toString());        
                this.dao.addDumpPath(this.sbDumpPath.toString());
                this.dao.addLogInfo(this.sbInfo.toString());
                this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));

                this.sbDumpPath.delete(0, this.sbDumpPath.length());
                sbDumpChanger.delete(0, sbDumpChanger.length());
                this.sbInfo.delete(0,this.sbInfo.length());
            }
        }else{//分表备份所有的表
            for(String t:mongotbmap.keySet()){
                sbDumpChanger.append(this.sbDump.toString());
                sbDumpChanger.append(" -c ").append(t).append(" ");
                this.sbDumpPath.append(baseInfoMap.get("backuppath").toString()).append(File.separator)
                    .append(baseInfoMap.get("host").toString()).append(File.separator);
                this.sbInfo.append(" [host]:").append(baseInfoMap.get("host").toString()).append(",")
                    .append("[db]:").append(baseInfoMap.get("database").toString()).append(",")
                    .append("[table]:").append(t);
                
                this.dao.addDump(sbDumpChanger.append(" -o ").append(this.sbDumpPath.toString()).toString());        
                this.dao.addDumpPath(this.sbDumpPath.toString());
                this.dao.addLogInfo(this.sbInfo.toString());
                this.dao.addFileNum(Integer.parseInt(baseInfoMap.get("filenum").toString()));
                
                this.sbDumpPath.delete(0, this.sbDumpPath.length());
                sbDumpChanger.delete(0, sbDumpChanger.length());
                this.sbInfo.delete(0,this.sbInfo.length());
            }
        }
        this.sbDump.delete(0, this.sbDump.length());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="设置单独备份或忽略的表的字符串">
    //设置单独备份或忽略的表的字符串
    private String setBkIgnTable(Map<String, Object> baseInfoMap,List tableList,Map<String, String> mongotbmap){
        StringBuilder sbDump=new StringBuilder();
        if(Integer.parseInt(baseInfoMap.get("backuptable").toString())==1 && tableList.size()>0){
            int i=tableList.size();
            
            while(--i>=0){
                Map<String, Object> tableListMap=(Map)tableList.get(i);
                //sbDump.append(" ").append(tableListMap.get("tablename").toString());
                
                mongotbmap.remove(tableListMap.get("tablename").toString());
            }
            for(String t:mongotbmap.keySet()){
                sbDump.append(" --excludeCollection=").append(t);
            }
            
        }else if(Integer.parseInt(baseInfoMap.get("ignoretable").toString())==1 && tableList.size()>0){
            int i=tableList.size();
            while(--i>=0){
                Map<String, Object> tableListMap=(Map)tableList.get(i);
                sbDump.append(" --excludeCollection=").append(tableListMap.get("tablename").toString());
            }
        }
        return sbDump.toString();
    }
    // </editor-fold>
}
