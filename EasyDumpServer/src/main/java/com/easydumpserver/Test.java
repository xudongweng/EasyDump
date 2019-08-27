/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver;

import com.easydumpserver.mysql.helper.MySQLHelper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;



/**
 *
 * @author sheriff
 */
public class Test {
    public static void main(String[] args){
        
        //System.out.println(Test.class.getResource(""));
        Locale myLocale = Locale.getDefault(Locale.Category.FORMAT);
        ResourceBundle rb = ResourceBundle.getBundle("config",myLocale);
        //System.out.println(rb.getString("mysql.server"));
        MySQLHelper mysqlcom=new MySQLHelper();
        mysqlcom.setURL(rb.getString("mysql.server"),rb.getString("mysql.port"),rb.getString("mysql.user"), rb.getString("mysql.password"));
        List list=mysqlcom.queryAll("select * from easydump.databases");
        Map<String, Object> aa=(Map)list.get(0);
        System.out.println(aa.get("ip"));
    }
}
