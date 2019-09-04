/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mysql.controller.thread;

/**
 *
 * @author sheriff
 */
public class BackupThread extends Thread{
    private String dummp="";
    private String dumpPath="";
    public BackupThread(String dump,String dumpPath){
        this.dummp=dump;
        this.dumpPath=dumpPath;
    }
    @Override
     public void run() {
     
     }
}
