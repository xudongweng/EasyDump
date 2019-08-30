/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mysql.helper;

/**
 *
 * @author sheriff
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class MySQLHelper {
    private final String driver="com.mysql.cj.jdbc.Driver";
    private final String urlhead="jdbc:mysql://";
    private final String urltail="?serverTimezone=UTC";
    private String url;
    private String user;
    private String password;
    private Logger log;
    
    public MySQLHelper(){
        this.log=Logger.getLogger(MySQLHelper.class);
    }
    
    public void setURL(String server,String port,String user,String password){
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
            }
        } catch(ClassNotFoundException e) {
            log.error(e.toString());
        } catch(SQLException e) {
            log.error(e.toString());
        }catch (Exception e) {
            log.error(e.toString());
        }
        return -1;
    }
    
    public List getAllDB(){
        try{
            Class.forName(this.driver);
            Connection conn = DriverManager.getConnection(this.url,this.user,this.password);
            
            if(!conn.isClosed()){
                List dblist=new ArrayList();
                Statement stmt = conn.createStatement(); //创建语句对象，用以执行sql语言
                ResultSet rs = stmt.executeQuery("show databases"); 
                while(rs.next()){
                    dblist.add(rs.getString(1));
                }
                rs.close();
                stmt.close();
                conn.close();
                return dblist;
            }
        } catch(ClassNotFoundException e) {
            log.error(e.toString());
        } catch(SQLException e) {
            log.error(e.toString());
        }catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }
    
    public List getAllTables(String db){
        try{
            Class.forName(this.driver);
            Connection conn = DriverManager.getConnection(this.url,this.user,this.password);
            
            if(!conn.isClosed()){
                List dblist=new ArrayList();
                Statement stmt = conn.createStatement(); //创建语句对象，用以执行sql语言
                ResultSet rs = stmt.executeQuery("show tables from "+db); 
                while(rs.next()){
                    dblist.add(rs.getString(1));
                }
                rs.close();
                stmt.close();
                conn.close();
                return dblist;
            }
        } catch(ClassNotFoundException e) {
            log.error(e.toString());
        } catch(SQLException e) {
            log.error(e.toString());
        }catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }
    
    //查询后获取列表
    public List queryAll(String sql) {
        try {
            Class.forName(this.driver);
            Connection conn = DriverManager.getConnection(this.url,this.user,this.password);
            if(!conn.isClosed()){
                List<Map<String, Object>> list = new ArrayList<>();
                Statement stmt = conn.createStatement(); //创建语句对象，用以执行sql语言
                ResultSet rs = stmt.executeQuery(sql);
                ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
                int columnCount = md.getColumnCount();   //获得列数 
                while (rs.next()) {
                    Map<String,Object> rowData = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                            rowData.put(md.getColumnName(i), rs.getObject(i));
                            //System.out.println(md.getColumnName(i));
                    }
                    list.add(rowData);
                }
                rs.close();
                stmt.close();
                conn.close();
                return list;
            }
        } catch(ClassNotFoundException e) {
            log.error(e.toString());
        } catch(SQLException e) {
            log.error(e.toString());
        }catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }
    
    //对表的第一列进行MAP
    public Map<String, String> querySingleColumnMap(String sql) {
        try {
            Class.forName(this.driver);
            Connection conn = DriverManager.getConnection(this.url,this.user,this.password);
            if(!conn.isClosed()){
                Map<String, String> rowMap = new HashMap<>();
                Statement stmt = conn.createStatement(); //创建语句对象，用以执行sql语言
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    rowMap.put(rs.getString(1),rs.getString(1));
                }
                rs.close();
                stmt.close();
                conn.close();
                return rowMap;
            }
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
