/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver;

import com.easydumpserver.mysql.controller.MySQLDumpStringController;
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
        MySQLDumpStringController a=new MySQLDumpStringController();
        a.getDumpBaseInfo();
        a.getDumpString();
        
    }
}
