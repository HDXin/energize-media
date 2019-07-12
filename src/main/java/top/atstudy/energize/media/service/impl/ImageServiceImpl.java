package top.atstudy.energize.media.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.atstudy.energize.media.config.ImageSize;
import top.atstudy.energize.media.config.ImageSizeConfig;
import top.atstudy.energize.media.config.StringUtils;
import top.atstudy.energize.media.service.ImageService;
import top.atstudy.framework.exception.APIException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: hdxin
 * Date: 2018-07-10
 * Time: 19:00
 */
@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Value("${filestorage.root}")
    private String fileRoot;

    private static final String IMAGE_FOLDER = "/image";
    private static final String ORIGIN = "origin";

    /**
     * 原图尺寸
     */
    public static final String DEFAULT_IMAGE_SIZE = "0x0";
    private static final Pattern IMAGE_SIZE_PATTERN = Pattern.compile("\\dx\\d");

    @Autowired
    private DefaultImageCompressorImpl defaultImageCompressor;

    @Autowired
    private ImageSizeConfig imageSizeConfig;

    @Override
    public List<String> uploadImages(boolean compress, MultipartFile... files) throws
            IOException {

        List<String> pathList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String buildPath = buildImagePath();
            pathList.add(buildPath);

            String suffix = StringUtils.getFileSuffix(file.getOriginalFilename());
            createFIle(file, buildPath + ORIGIN + suffix);

            logger.info("Created file: {} -> {}", ORIGIN + suffix, buildPath);
            //压缩
            File srcFile = new File(fileRoot + IMAGE_FOLDER + buildPath + ORIGIN + suffix);
            File targetFile = new File(fileRoot + IMAGE_FOLDER + buildPath + DEFAULT_IMAGE_SIZE + suffix);
            if (compress) {
                defaultImageCompressor.compressImage(srcFile, targetFile);
            } else {
                FileInputStream fileInputStream = new FileInputStream(srcFile);
                FileOutputStream fileOutputStream = new FileOutputStream(targetFile);

                IOUtils.copy(fileInputStream, fileOutputStream);
                fileOutputStream.flush();
                IOUtils.closeQuietly(fileOutputStream);
                IOUtils.closeQuietly(fileInputStream);

            }

            //添加文件记录
            createImageRecord(buildPath, file);
        }

        return pathList;
    }

    @Override
    public Map<String, Object> read(Long fid) throws IOException, APIException {
        return null;
    }

    @Override
    public Map<String, Object> read(String path) throws IOException {
        Map<String, Object> readMap = new HashMap<>();

        File file = new File(fileRoot + IMAGE_FOLDER, path);
        if (file.isFile()) {
            if (file.canRead()) {
                FileInputStream fileInputStream = new FileInputStream(file);

                readMap.put("inputStream", fileInputStream);
                readMap.put("imageSuffix", getSuffixByName(file.getName()));
                return readMap;
            } else {
                throw new IOException("file cannot be read: " + path);
            }
        }

        // 若没有指定格式,则返回原图
        String fileName = file.getName().toLowerCase();
        final String size = IMAGE_SIZE_PATTERN.matcher(fileName).find() ? fileName : DEFAULT_IMAGE_SIZE;
        final File dir = file.isDirectory() ? file : file.getParentFile();

        file = this.loadSizedFile(dir, size);
        if (file != null) {
            FileInputStream fileInputStream = new FileInputStream(file);

            readMap.put("inputStream", fileInputStream);
            readMap.put("imageSuffix", getSuffixByName(file.getName()));
            return readMap;
        }

        return readMap;
    }

    private File loadSizedFile(File dir, String size) throws IOException {
        // 检查是否存在目录
        if (!dir.isDirectory()) {
            return null;
        }

        // 检查所需尺寸图是否存在
        File file = searchFile(dir, size);
        if (file != null) {
            return file;
        }

        // 加载默认图
        File defaultImageFile = loadDefaultImageFile(dir);
        if (defaultImageFile == null) {
            return null;
        }

        if (size.equals(DEFAULT_IMAGE_SIZE)) {
            return defaultImageFile;
        }

        // 仅仅生成支持的尺寸
        ImageSize targetImageSize = new ImageSize(size);

        List<ImageSize> imageSizes = imageSizeConfig.getImageSizeList();
        for (ImageSize imageSize : imageSizes) {
            if (imageSize.equals(targetImageSize)) {
                String suffix = getSuffix(defaultImageFile.getName());
                File targetFile = new File(dir, targetImageSize.size + suffix);
                resize(defaultImageFile, targetFile, targetImageSize.width, targetImageSize.height);

                return targetFile;
            }
        }

        this.logger.info("Not found sized image, using default.");

        // 默认返回默认图
        return defaultImageFile;
    }


    /**
     * 强制压缩/放大图片到固定的大小
     *
     * @param srcFile
     * @param targetFile    源图片
     * @param targetWidth   int 新宽度
     * @param targetHeight, int 新高度
     * @throws IOException
     */
    private void resize(File srcFile, File targetFile, int targetWidth, int targetHeight) throws IOException {
        // 若target是0x0，应该使用原图
        if (targetWidth == 0 && targetHeight == 0) {
            return;
        }

        FileInputStream input = new FileInputStream(srcFile);
        BufferedImage srcImage = ImageIO.read(input);
        IOUtils.closeQuietly(input);

        int srcWidth = srcImage.getWidth();
        int srcHeight = srcImage.getHeight();

        // 若宽为0，参考原高等比例缩放;
        if (targetWidth == 0) {
            targetWidth = (int) (1L * targetHeight * srcWidth / srcHeight);
        }
        // 若高为0，参考原宽等比例缩放;
        else if (targetHeight == 0) {
            targetHeight = (int) (1L * targetWidth * srcHeight / srcWidth);
        }

        // 等比例压缩；若目标尺寸比较原图大，则压缩为与目标尺寸等比例的小图
        double rate1 = ((double) srcWidth) / srcHeight;
        double rate2 = ((double) targetWidth) / targetHeight;

        int width = srcWidth, height = srcHeight;
        if (rate1 > rate2) {
            width = (int) (1L * srcHeight * targetWidth / targetHeight);
        } else {
            height = (int) (1L * srcWidth * targetHeight / targetWidth);
        }

        // 先从原图截取与目标尺寸等比例最大图，然后再压缩
        int x = (srcWidth - width) / 2, y = (srcHeight - height) / 2;
        srcImage = srcImage.getSubimage(x, y, width, height);

        // 如目标尺寸比截图大，则采用截图尺寸
        if (targetWidth > width) {
            targetWidth = width;
            targetHeight = height;
        }

        writeImage(srcImage, targetFile, targetWidth, targetHeight);
    }


    private void writeImage(BufferedImage srcImage, File targetFile, int targetWidth, int targetHeight)
            throws IOException {
        //构建图片对象
        BufferedImage image = new BufferedImage(targetWidth, targetHeight, srcImage.getType());
        //绘制缩放后的图
        Image scaledInstance = srcImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        image.getGraphics().drawImage(scaledInstance, 0, 0, targetWidth, targetHeight, Color.WHITE, null);
        //输出到文件
        ImageIO.write(image, "jpg", targetFile);

        logger.info("Created image: {}", targetFile);
    }

    private File searchFile(File dir, final String searchName) throws FileNotFoundException {
        FilenameFilter filter = (dir1, name) -> name.startsWith(searchName);

        File[] files = dir.listFiles(filter);
        if (!ArrayUtils.isEmpty(files)) {
            File file = files[0];
            if (file.isFile() && file.canRead()) {
                return file;
            }
        }

        return null;
    }

    /**
     * 加载默认图，若不存在，则使用原图生成。
     *
     * @param dir
     * @return 默认图或null
     * @throws IOException
     */
    private File loadDefaultImageFile(File dir) throws IOException {
        File targetFile = searchFile(dir, DEFAULT_IMAGE_SIZE);
        if (targetFile == null) {
            File srcFile = searchFile(dir, ORIGIN);
            if (srcFile == null) {
                return null;
            }

            targetFile = new File(dir, DEFAULT_IMAGE_SIZE + getSuffix(srcFile.getName()));
            defaultImageCompressor.compressImage(srcFile, targetFile);
        }

        return targetFile;
    }

    private void createImageRecord(String buildPath, MultipartFile file) {
//        FileDTO fileDTO = new FileDTO();
//        fileDTO.setOperator(sessionUser, true);
//        fileDTO.setFileName(file.getOriginalFilename());
//        fileDTO.setFilePath(buildPath);
//        fileDTO.setFileSize(file.getSize());
//        fileDTO.setFileSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
//        fileDTO.setFileType(EnumFileType.IMAGE);
//        fileDTOMapper.insertSelective(fileDTO);
    }

    //写入文件
    private void createFIle(MultipartFile file, String buildPath) throws IOException {
        byte[] fileBytes = file.getBytes();
        Path path = Paths.get(fileRoot + IMAGE_FOLDER, buildPath);
        Files.write(path, fileBytes, StandardOpenOption.CREATE);
    }

    private String buildImagePath() throws IOException {
        return StringUtils.buildImagePath(fileRoot + IMAGE_FOLDER, 0L);
    }

    private static String getSuffixByName(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String getSuffix(String fileName) {
        return StringUtils.getFileSuffix(fileName);
    }

}
