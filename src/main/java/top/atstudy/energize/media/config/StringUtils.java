package top.atstudy.energize.media.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Spector on 2017/6/26.
 */
public class StringUtils {

    /**
     * 创建文件目录
     *
     * @param preFolder
     * @param name
     * @param userId
     * @return
     * @throws IOException
     */
    public static String buildFilePath(String preFolder, String name, Long userId) throws IOException {
        String suffix = "";
        final Date date = new Date();
        if (name != null) {
            int i = name.lastIndexOf('.');
            if (i >= 0) {
                suffix = name.substring(i);
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("/yyyyMMdd/HHmmssSSS/");
        String prePath = (null == userId ? "" : "/" + userId) + format.format(date);
        Files.createDirectories(Paths.get(preFolder + prePath));
        return prePath + UUID.randomUUID().toString() + suffix;
    }

    /**
     * 创建文件目录
     *
     * @param preFolder
     * @param userId
     * @return
     * @throws IOException
     */
    public static String buildImagePath(String preFolder, Long userId) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("/yyyyMMdd/HHmmss/SSS/");
        String prePath = (null == userId ? "" : "/" + userId) + format.format(new Date());
        Files.createDirectories(Paths.get(preFolder + prePath));
        return prePath;
    }

    public static String getFileSuffix(String name) {
        String suffix = "";
        if (name != null) {
            int i = name.lastIndexOf('.');
            if (i >= 0) {
                suffix = name.substring(i);
            }
        }
        return suffix;
    }
}
