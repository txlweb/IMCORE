package com.textreader.comic;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ComicObject {
    private String file = "";

    private List<ComicPage> index = new ArrayList<>();
    private String info = "";
    private ComicINFO INFO = new ComicINFO();
    private String list = "";
    private long data_frame_start_as = -1;
    private boolean Data_Resolved = false;
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
    public boolean open(String FileName){
        //bin
        // --
        // |-info
        // |-list
        // |-data
        file = FileName;
        try(InputStream bin = new FileInputStream(file)){
            int len;
            byte[] code = new byte[1];
            byte[] b_l = new byte[1024];
            int i = 0;
            len = bin.read(code,0,1);
            data_frame_start_as = 0;
            while(len > 0){
                b_l[i] = code[0];
                i++;
                if(i>=1024){
                    i=0;
                    String bf_data = new String(b_l, StandardCharsets.UTF_8);
                    data_rfash(bf_data);
                    if(Data_Resolved) break;//防止在知道数据段起始位置后再加1024
                    data_frame_start_as += 1024;
                }
                len = bin.read(code,0,1);
                if(Data_Resolved) break;
            }
            data_rfash(new String(b_l, StandardCharsets.UTF_8));
            bin.close();
            //解析info段和list段
            String[] pre_info = info.split("\r\n");
            if(pre_info.length>=3) {
                INFO.setTittle(pre_info[0]);
                INFO.setAuthor(pre_info[1]);
                INFO.setProfile(pre_info[2]);
                INFO.setIcon(Base64.getDecoder().decode(pre_info[3]));
            }
            //list
            //TODO:解析list~
            pre_info = list.split("\r");

            String pre_last = "";
            //前处理，去除换行
            for (int j = 0; j < pre_info.length; j++) {
                pre_info[j] = pre_info[j].replace("\r","");
                pre_info[j] = pre_info[j].replace("\n","");
            }
            for (String value : pre_info) {
                if(!value.isEmpty()){
                    if (value.charAt(0) == '[') {//章节信息
                        pre_last = pre_last + value.substring(1) + "\r";
                    }
                }
            }
            pre_info = pre_last.split("\r");
            int lsize = 0;
            for (String string : pre_info) {
                if (string.split("\\|").length >= 2) {
                    ComicPage t = new ComicPage();
                    int s = Integer.parseInt(string.split("\\|")[1]);
                    t.setTitle(string.split("\\|")[0]);
                    for (int k = 0; k < s; k++) {
                        t.push(k + lsize);
                    }
                    index.add(t);
                    lsize += s;
                }
            }
        } catch (FileNotFoundException e) {
            //throw new RuntimeException(e);
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
    private void data_rfash(String v1){
        //System.out.println(Arrays.toString(v1.split("[INFOENDL]")));
        //读取数据段（INFO）
        if(v1.split("-INFO-ENDL").length>1 && info.isEmpty()){
            this.info = v1.split("-INFO-ENDL")[0];
            //System.out.println(this.info);
            this.list = "-LIST-START\r";
        }
        //buffer读取list
        if(!this.list.isEmpty()){
            if (v1.split("-INFO-ENDL").length>1){
                //A:在同一段，需要截断
                this.list = this.list + v1.split("-INFO-ENDL")[1].split("-LIST-ENDL")[0];
            }else {
                //不在同一段，仅
                this.list = this.list + v1.split("-INFO-ENDL")[1];
            }
            //System.out.println(this.list);
        }
        if(v1.split("-LIST-ENDL").length>1 && !this.list.isEmpty()){
            //System.out.println(this.list);
            this.list = this.list + "-LIST-END\r";
            data_frame_start_as += findBytes(v1.getBytes(),"-LIST-END".getBytes())+"-LIST-END".getBytes().length;
            Data_Resolved = true;
        }

    }
    private int findBytes(byte[] source, byte[] target) {
        if (source == null || target == null || source.length < target.length) {
            return -1;
        }
        for (int i = 0; i <= source.length - target.length; i++) {
            int j;
            for (j = 0; j < target.length; j++) {
                if (source[i + j] != target[j]) {
                    break;
                }
            }
            if (j == target.length) {
                return i;
            }
        }
        return -1;
    }
    public static String fileToBase64(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private boolean append_img(String FileName){
        try(
                ByteOutputStream tmp = new ByteOutputStream();
                FileInputStream in = new FileInputStream(file);
                ) {
            System.out.println(data_frame_start_as);
            // 读取到指定位置，只要索引部分
            int bs  = -"-LIST-ENDL".getBytes().length;
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) != -1) {
                tmp.write(buffer, 0, bytesRead);
            }
            while ((bytesRead = in.read(buffer)) != -1) {
                tmp.write(buffer, 0, bytesRead);
            }
            tmp.write((fileToBase64(FileName)+"\r").getBytes());
            FileOutputStream fos = new FileOutputStream(file+".txt");
            InputStream is = new ByteArrayInputStream(tmp.toByteArray());
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
