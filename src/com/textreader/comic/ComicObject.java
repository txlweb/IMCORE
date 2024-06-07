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
            InputStream is = new ByteArrayInputStream(tmp.getBytes());
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
