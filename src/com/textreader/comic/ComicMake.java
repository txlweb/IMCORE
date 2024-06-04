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

    /**
     * @param save_to 保存位置
     * @param zip_file 压缩包文件(必修直接是图片.不然不能扫描)
     * @param run_path 如果在Android中必须设定为程序内部/外部路径,win下不必要
     * @return 是否成功
     */
    public static boolean auto_make(String save_to, String zip_file,String run_path){
        File tmp;
        if(run_path != null){
            tmp = new File(run_path+"/tmp");
        }else {
            tmp = new File("tmp");
        }
        if(tmp.isDirectory()) deleteFileByIO(tmp.getPath());
        System.out.println(tmp.getPath());
        tmp.mkdir();
        try {
            Unzipper.unzip(zip_file, tmp.getPath());
            List<String> fls = Dir_sync(tmp.getPath());
            make(save_to,fls, fls.get(0),"auto make file","idlike","imcore je *power by idlike",new ArrayList<>());
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    public static void deleteFileByIO(String filePath) {
        File file = new File(filePath);
        File[] list = file.listFiles();
        if (list != null) {
            for (File temp : list) {
                deleteFileByIO(temp.getAbsolutePath());
            }
        }
        file.delete();
    }
    /**
     * @param save_to 保存位置
     * @param img_dir 图片列表(顺序)
     * @param ic_img_dir 图标
     * @param title 标题
     * @param author 作者
     * @param profile 简介
     * @param chapter 章节列表(new Array()为生成一整个)
     * @return 是否成功
     */
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
