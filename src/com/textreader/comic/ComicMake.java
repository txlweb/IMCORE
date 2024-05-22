package com.textreader.comic;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class ComicMake {
    public static String fileToBase64(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        return Base64.getEncoder().encodeToString(fileContent);
    }
    public static List<String> Dir_sync(String dir){
        try {
            return Files.walk(Paths.get(dir))
                    .filter(Files::isRegularFile)
                    .map(Path::toAbsolutePath)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    //chapter [NAME|IMG_SIZE (if null,create all in one chapter)
    public static boolean make(String save_to, List<String> img_dir, String ic_img_dir, String title, String author, String profile, List<String> chapter){
        try{
            File save_file = new File(save_to);
            if (!save_file.createNewFile()) return false;
            FileOutputStream out = new FileOutputStream(save_file);
            //写入信息
            out.write(title.getBytes());
            out.write("\r".getBytes());
            out.write(author.getBytes());
            out.write("\r".getBytes());
            out.write(profile.getBytes());
            out.write("\r".getBytes());
            //图标
            out.write(fileToBase64(ic_img_dir).getBytes());
            out.write("\r".getBytes());
            out.write("-INFO-ENDL".getBytes());
            out.write("\r".getBytes());
            //目录
            if(chapter.isEmpty()){
                out.write(("[MAIN|"+img_dir.size()).getBytes());
                out.write("\r".getBytes());
            }else {
                for (String s : chapter){
                    if(s.charAt(0) == '['){
                        out.write(s.getBytes());
                        out.write("\r".getBytes());
                    }else{
                        out.write(("["+s).getBytes());
                        out.write("\r".getBytes());
                    }
                }
            }
            out.write("-LIST-ENDL".getBytes());
            out.write("\r".getBytes());
            //资源文件
            for (String s : img_dir) {
                out.write(fileToBase64(s).getBytes());
                out.write("\r".getBytes());
            }
            //关闭文件
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
