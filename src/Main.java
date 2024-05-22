import com.textreader.comic.ComicMake;
import com.textreader.comic.ComicObject;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String directoryPath = "278849"; // 替换为你的目录路径

        try {
            List<String> filePaths = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile) // 过滤出文件，排除目录
                    .map(Path::toAbsolutePath) // 转换为绝对路径
                    .map(Path::toString) // 转换为字符串
                    .collect(Collectors.toList()); // 收集到列表中

            // 打印所有文件的绝对路径
            filePaths.forEach(System.out::println);
            ComicMake.make("R.CEIP",filePaths,"test.jpg","a","b","c",new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ComicObject o = new ComicObject();
//        o.open("R.CEIP");
//        //o.get_image_data(o.GetIndex().get(0).get(0));
//
//        //o.append_img("e262fc6a280d8eeec2f7923c68828163.jpg");
//        ComicObject oo = new ComicObject();
//        oo.open("R.CEIP");
//        Path filePath = Paths.get("test.jpg");
//        try {
//            // 将byte[]写入文件
//            Files.write(filePath, oo.get_image_data(1));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}