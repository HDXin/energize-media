package top.atstudy.energize.media.service;


import com.google.zxing.WriterException;
import top.atstudy.framework.exception.APIException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: hdxin
 * Date: 2018-07-11
 * Time: 15:15
 */
public interface QrcodeService {

    /**
     * 生成二维码到文件
     *
     * @param content
     * @return
     */
    String writeQrCodeFile(String content) throws APIException, IOException, WriterException;

    /**
     * 生成二维码到流
     *
     * @param content
     * @param outputStream
     */
    void writeQrCodeOutputStream(String content, OutputStream outputStream) throws IOException, APIException, WriterException;
}
