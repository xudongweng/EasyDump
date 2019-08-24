/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xudong.easydumpserver;

/**
 *
 * @author sheriff
 */
public class MySQLBackupStrategy {
    
    public String getLockDB(String code){
        return " --default-character-set="+code+" --flush-logs -R --lock-all-tables";
    }
    public String getUnlockDB(String code){
        return " --default-character-set="+code;
    }
    public String getOnlyStruct(String code){
        return " --default-character-set="+code+" --opt";
    }
    public String getOnlyData(String code){
        return " --default-character-set="+code+" -t";
    }
}
