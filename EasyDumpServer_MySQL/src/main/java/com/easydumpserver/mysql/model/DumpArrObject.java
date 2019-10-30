/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mysql.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sheriff
 */
public class DumpArrObject {
    private List<String> arrDump=new ArrayList<>();//mysqldump字符串集合
    private List<String> arrDumpPath=new ArrayList<>();//mysqldump路径字符串集合
    private List<String> arrLogInfo=new ArrayList<>();//日志备份信息
    private List<Integer> arrFileNum=new ArrayList<>();//备份文件数量
    
    public void addDump(String dump){
        this.arrDump.add(dump);
    }
    public List<String> getDump(){
        return this.arrDump;
    }
    public void addFileNum(int FileNum){
        this.arrFileNum.add(FileNum);
    }
    
    public List<Integer> getFileNum(){
        return this.arrFileNum;
    }
    public void addLogInfo(String info){
        this.arrLogInfo.add(info);
    }
    public List<String> getLogInfo(){
        return this.arrLogInfo;
    }
    
    public void addDumpPath(String dump){
        this.arrDumpPath.add(dump);
    }
    public List<String> getDumpPath(){
        return this.arrDumpPath;
    }
    
    public void printList(){
        if(this.arrDump!=null){
            int i=this.arrDump.size();
            while(--i>=0){
                System.out.println(i);
                System.out.println(this.arrDump.get(i));
            }
        }
        if(this.arrDumpPath!=null){
            int i=this.arrDumpPath.size();
            while(--i>=0){
                System.out.println(i);
                System.out.println(this.arrDumpPath.get(i));
            }
        }
    }
    
    public void clear(){
        this.arrDump.clear();
        this.arrDumpPath.clear();
        this.arrLogInfo.clear();
        this.arrFileNum.clear();
    }
}
