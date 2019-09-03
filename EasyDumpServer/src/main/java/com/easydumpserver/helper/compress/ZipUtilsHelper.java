/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easydumpserver.helper.compress;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class ZipUtilsHelper {
    private Logger log=null;
    private int buffsize=1024*4;
    
    public ZipUtilsHelper(){
        this.log=Logger.getLogger(ZipUtilsHelper.class);
    }
    
    public void zipStreamCompress(InputStream in,String destPath,String filename,String zipname){
         try{
             ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(destPath+zipname+".zip"));
             ZipEntry e = new ZipEntry(filename);
             zout.putNextEntry(e);
             byte buff[] = new byte[buffsize];
             int len = 0;  
             while ((len = in.read(buff)) != -1) {
                 zout.write(buff, 0, len);
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
    
    public void gzipStreamCompress(InputStream in,String destPath,String filename,String zipname){
         try{
             GZIPOutputStream gos=new GZIPOutputStream(new FileOutputStream(destPath+zipname+".gz"));
             byte buff[] = new byte[buffsize];
             int len = 0;  
             while ((len = in.read(buff)) != -1) {
                 //System.out.println(len);
                 gos.write(buff, 0, len);
             }  
             gos.finish();
             gos.flush();
             gos.close();
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
