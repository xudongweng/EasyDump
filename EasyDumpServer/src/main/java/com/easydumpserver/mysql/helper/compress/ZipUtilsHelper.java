/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.mysql.helper.compress;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class ZipUtilsHelper {
    private Logger log;
    public ZipUtilsHelper(){
        this.log=Logger.getLogger(ZipUtilsHelper.class);
    }
    
    public void zipStreamCompress(InputStream in,String destPath,String filename){
        try{
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(destPath+filename+".zip"));
        ZipEntry e = new ZipEntry(filename);
        zout.putNextEntry(e);
        byte b[] = new byte[1024];
        int len = 0;  
        while ((len = in.read(b)) != -1) {
            //System.out.println(len);
            zout.write(b, 0, len);  
        }  

        zout.closeEntry();  
        zout.flush();  
        zout.close();  

        in.close();
        }catch(FileNotFoundException e){
            log.error(e.toString());
        }catch(IOException e){
            log.error(e.toString());
        }catch(Exception e){
            log.error(e.toString());
        }
    }
}
