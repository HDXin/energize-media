package top.atstudy.energize.media.controller.image;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import top.atstudy.energize.media.config.EnumImageType;
import top.atstudy.energize.media.service.ImageService;
import top.atstudy.framework.controller.RestPrototypeController;
import top.atstudy.framework.exception.APIException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huangdexin @ harley
 * @email huangdexin@kuaicto.com
 * @date 2019/7/11 10:00
 */
@RestPrototypeController
@RequestMapping("/image")
public class ImageController {
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageService imageService;

    /**
     * 文件上传
     * @param request
     * @return
     * @throws IOException
     * @throws APIException
     */
    @PostMapping("/single")
    public String uploadSingleImage(HttpServletRequest request) throws IOException, APIException {
        MultipartFile[] files = parseMultiFiles(request);
        if (files == null || files.length == 0) {
            return null;
        }

        List<String> paths = imageService.uploadImages(checkImageCompress(request), files[0]);
        return paths.size() != 0 ? paths.get(0) : null;
    }

    /**
     * 文件上传：批量上传
     * @param request
     * @return
     * @throws IOException
     * @throws APIException
     */
    @PostMapping("/multi")
    public List<String> uploadMultiImages(HttpServletRequest request) throws IOException, APIException {
        MultipartFile[] files = parseMultiFiles(request);
        String fileNames = Arrays.stream(files).map(MultipartFile::getOriginalFilename)
                .filter(StringUtils::isNotBlank).collect(Collectors.joining(" , "));

        if (StringUtils.isBlank(fileNames)) {
            return null;
        }

        return imageService.uploadImages(checkImageCompress(request), files);
    }

    /**
     * 文件上传
     * @param request
     * @param response
     * @throws Exception
     */
    @GetMapping("/download/**")
    public void getImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getServletPath();
        String[] images = path.split("image");
        String pathStr = "";
        if (images.length > 1) {
            pathStr = images[1];
        }
        if (StringUtils.isEmpty(pathStr) || pathStr.equals("/")) {
            throw new IllegalArgumentException();
        }

        pathStr = pathStr.substring(9);
        pathStr = pathStr.replaceAll("..//", "");
        Map<String, Object> readMap = this.imageService.read(pathStr);
        if (readMap == null | readMap.size() == 0) {
            return;
        }

        responseImage(readMap, response);
    }

    private void responseImage(Map<String, Object> readMap, HttpServletResponse response) throws IOException {
        InputStream input = (InputStream) readMap.get("inputStream");
        String fileType = readMap.get("imageSuffix").toString();
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType(EnumImageType.typeOf(fileType.toLowerCase()).type);
        byte[] by = new byte[1024];
        int length;
        while ((length = input.read(by)) != -1) {
            outputStream.write(by, 0, length);
        }
        outputStream.flush();
        IOUtils.closeQuietly(outputStream);
        IOUtils.closeQuietly(input);
    }



    private MultipartFile[] parseMultiFiles(HttpServletRequest httpServletRequest) {
        StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest) httpServletRequest;
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();

        MultipartFile[] files = new MultipartFile[multiFileMap.size()];
        Iterator<Map.Entry<String, List<MultipartFile>>> iterator = multiFileMap.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, List<MultipartFile>> next = iterator.next();
            files[i++] = next.getValue().get(0);
        }
        return files;
    }

    private boolean checkImageCompress(HttpServletRequest request){
        String notCompress = request.getParameter("notCompress");
        logger.info(">>> 图片上传是否压缩：{}", notCompress);
        return Boolean.parseBoolean(notCompress);
    }



}
