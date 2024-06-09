package com.textreader.comic;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ComicObject {
    private String file = "";
    private List<ComicPage> index = new ArrayList<>();
    private ComicINFO INFO = new ComicINFO();
    private long data_frame_start_as = -1;
    public byte[] get_image_data(int data_id){//id从1开始
        if(data_id<0) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            int i = -1;
            while ((line = reader.readLine()) != null) {
                if(line.contains("-LIST-ENDL")) i++;
                if(i>=0) i++;
                //System.out.println(line);
                if(i == data_id+1) return Base64.getDecoder().decode(line);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public List<ComicPage> GetIndex(){
        return this.index;
    }
    public ComicINFO GetInfo(){
        return this.INFO;
    }

    /**
     * @param FileName 要打开的文件名
     * @return 是否成功
     */
    public boolean open(String FileName){
        file = FileName;
        boolean start = false;
        int size = 0;
        int id = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = reader.readLine()) != null) {
                id++;
                if(id == 1) INFO.setTittle(line);
                if(id == 2) INFO.setAuthor(line);
                if(id == 3) INFO.setProfile(line);
                if(id == 4) INFO.setIcon(Base64.getDecoder().decode(line));
                if (line.equals("-INFO-ENDL")){
                    start = true;
                }
                if (line.equals("-LIST-ENDL")) break;
                if(start){
                    if (line.charAt(0) == '[') {
                        String[] d = line.substring(1).split("\\|");
                        if(d.length>=2){
                            ComicPage v = new ComicPage();
                            v.setTitle(d[0]);
                            int s = Integer.parseInt(d[1]);
                            for (int k = 0; k < s; k++) {
                                v.push(size+k);
                            }
                            size+=s;
                            this.index.add(v);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return !start;
    }
}
