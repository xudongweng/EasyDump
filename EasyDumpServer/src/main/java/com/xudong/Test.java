/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xudong;

import com.xudong.easydumpserver.helper.MySQLHelper;

import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;



/**
 *
 * @author sheriff
 */
public class Test {
    public static void main(String[] args){
        
        Logger log=Logger.getLogger(Test.class);
        log.trace("我是logger1，trace");
        log.debug("我是logger1，debug");
        log.info("我是logger1，info");
        log.warn("我是logger1，warn");
        log.error("我是logger1，error");
        log.fatal("我是logger1，fatal");
        
        System.out.println(Test.class.getResource(""));
        Locale myLocale = Locale.getDefault(Locale.Category.FORMAT);
        ResourceBundle rb = ResourceBundle.getBundle("config",myLocale);
        System.out.println(rb.getString("mysql.server"));
        MySQLHelper mysqlcom=new MySQLHelper();
        mysqlcom.setURL(rb.getString("mysql.server"),rb.getString("mysql.port"),rb.getString("mysql.user"), rb.getString("mysql.password"));
        System.out.println(mysqlcom.getConnection());
    }
}
