/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xudong.easydumpserver.helper;

/**
 *
 * @author sheriff
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLHelper {
    private final static String driver="com.mysql.cj.jdbc.Driver";
    private final static String urlhead="jdbc:mysql://";
    private final static String urltail="?serverTimezone=UTC";
    
    public static byte getConnection(String user,String password,String server,String port){
        try{
            Class.forName(driver);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306?serverTimezone=UTC","root","123456");
            if(!conn.isClosed()){
                conn.close();
                return 1;
            }else
                return 0;
        } catch(ClassNotFoundException e) {   
            e.printStackTrace();   
        } catch(SQLException e) {
            e.printStackTrace();  
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
