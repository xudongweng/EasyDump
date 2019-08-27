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
    private final List<String> arrDump=new ArrayList<>();//mysqldump字符串集合
    public void addDump(String dump){
        this.arrDump.add(dump);
    }
    public List<String> getArrDump(){
        return this.arrDump;
    }
    public void clear(){
        this.arrDump.clear();
    }
}
