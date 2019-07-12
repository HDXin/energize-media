package top.atstudy.energize.media.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.atstudy.energize.base.enums.http.BadRequest;
import top.atstudy.energize.media.config.LogoConfig;
import top.atstudy.energize.media.config.StringUtils;
import top.atstudy.energize.media.service.QrcodeService;
import top.atstudy.framework.exception.APIException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: hdxin
 * Date: 2018-07-11
 * Time: 15:15
 */
@Service
public class QrcodeServiceImpl implements QrcodeService {
    private static final Logger logger = LoggerFactory.getLogger(QrcodeServiceImpl.class);

    @Autowired
    private LogoConfig logoConfig;

    @Value("${filestorage.root}")
    private String fileRoot;

    @Value("${qrcode.width}")
    private Integer qrCodeWidth;

    private static final String IMAGE_FOLDER = "/image";
    private static final String ORIGIN = "origin";

    //用于设置图案的颜色
    private static final int BLACK = 0xFF000000;
    //用于背景色
    private static final int WHITE = 0xFFFFFFFF;

    @Override
    public String writeQrCodeFile(String content) throws APIException, IOException, WriterException {
        final BitMatrix bitMatrix = encodeQrCode(content);
        String prefix = fileRoot + IMAGE_FOLDER;
        String imagePath = buildImagePath() + ORIGIN + ".jpg";
        writeToFile(bitMatrix, new File(prefix + imagePath));
        logger.info(" ===>> qrcode rul: {}", imagePath);
        return imagePath;
    }

    @Override
    public void writeQrCodeOutputStream(String content, OutputStream outputStream) throws IOException, APIException, WriterException {
        final BitMatrix bitMatrix = encodeQrCode(content);
        writeToStream(bitMatrix, outputStream);
    }

    private String buildImagePath() throws IOException {
        return StringUtils.buildImagePath(fileRoot + IMAGE_FOLDER, null);
    }

    private BitMatrix encodeQrCode(String content) throws WriterException, IOException, APIException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        // 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);

        return new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, qrCodeWidth,
                qrCodeWidth, hints);

//        writeToFile(bitMatrix, new File(path));
    }

    private static BufferedImage toBufferdImage(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedImage.setRGB(i, j, (bitMatrix.get(i, j) ? BLACK : WHITE));
            }
        }
        return bufferedImage;
    }

    private void writeToFile(BitMatrix bitMatrix, File file) throws IOException, APIException {
        BufferedImage bufferedImage = toBufferdImage(bitMatrix);
        bufferedImage = logoConfig.logoMatrix(bufferedImage);

        final boolean create = ImageIO.write(bufferedImage, "jpg", file);
        if (!create) {
            throw new APIException(BadRequest.QR_CODE_CREATE_FAIL);
        }
    }

    private void writeToStream(BitMatrix bitMatrix, OutputStream outputStream) throws IOException, APIException {
        BufferedImage bufferedImage = toBufferdImage(bitMatrix);
        bufferedImage = logoConfig.logoMatrix(bufferedImage);

        final boolean create = ImageIO.write(bufferedImage, "jpg", outputStream);
        if (!create) {
            throw new APIException(BadRequest.QR_CODE_CREATE_FAIL);
        }
    }
}
