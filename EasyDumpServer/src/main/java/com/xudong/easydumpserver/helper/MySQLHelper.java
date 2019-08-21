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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.RowSet;
import org.apache.log4j.Logger;

public class MySQLHelper {
    private String driver="com.mysql.cj.jdbc.Driver";
    private String urlhead="jdbc:mysql://";
    private String urltail="?serverTimezone=UTC";
    private String url;
    private String user;
    private String password;
    private Logger log;
    
    public MySQLHelper(){
        this.log=Logger.getLogger(MySQLHelper.class);
    }
    
    public void setURL(String user,String password,String server,String port){
        this.url=this.urlhead+server+":"+port+this.urltail;
        this.user=user;
        this.password=password;
    }
    
    public byte getConnection(){
        try{
            Class.forName(this.driver);
            Connection conn = DriverManager.getConnection(this.url,this.user,this.password);
            if(!conn.isClosed()){
                conn.close();
                return 1;
            }else
                return -1;
        } catch(ClassNotFoundException e) {
            log.error(e.toString());
        } catch(SQLException e) {
            log.error(e.toString());
        }catch (Exception e) {
            log.error(e.toString());
        }
        return -1;
    }
    
    public RowSet getAllDB(){
        try{
            Class.forName(this.driver);
            Connection conn = DriverManager.getConnection(this.url,this.user,this.password);
            if(!conn.isClosed()){
                Statement stmt = conn.createStatement(); //创建语句对象，用以执行sql语言
                ResultSet rs = stmt.executeQuery("show databases"); 

                rs.close();
                stmt.close();
                conn.close();
                return null;
            }else
                return null;
            
        } catch(ClassNotFoundException e) {
            log.error(e.toString());
        } catch(SQLException e) {
            log.error(e.toString());
        }catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }
}
