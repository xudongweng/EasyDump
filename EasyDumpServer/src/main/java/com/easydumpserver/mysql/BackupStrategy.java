/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mysql;

/**
 *
 * @author sheriff
 */
public class BackupStrategy {
    private final String character=" --default-character-set=";
    //备份全库锁库
    public String getLockDB(String code){
        return this.character+code+" --flush-logs -R --lock-all-tables";
    }
    //备份全库
    public String getUnlockDB(String code){
        return this.character+code+" -R --single-transaction";
    }
    //备份结构
    public String getOnlyStruct(String code){
        return this.character+code+" --opt";
    }
    //备份数据，不包含结构
    public String getOnlyData(String code){
        return this.character+code+" -t";
    }
    //分表备份
    public String getSplitTables(String code){
        return this.character+code+" --single-transaction";
    }
}
