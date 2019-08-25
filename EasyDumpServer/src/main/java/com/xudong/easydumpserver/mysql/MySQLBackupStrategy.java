/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xudong.easydumpserver.mysql;

/**
 *
 * @author sheriff
 */
public class MySQLBackupStrategy {
    private final String charset=" --default-character-set=";
    
    public String getLockDB(String code){
        return this.charset+code+" --flush-logs -R --lock-all-tables";
    }
    public String getUnlockDB(String code){
        return this.charset+code;
    }
    public String getOnlyStruct(String code){
        return this.charset+code+" --opt";
    }
    public String getOnlyData(String code){
        return this.charset+code+" -t";
    }
}
