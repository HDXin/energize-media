package top.atstudy.energize.media.controller.image;

import com.google.zxing.WriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.atstudy.energize.media.service.QrcodeService;
import top.atstudy.framework.controller.RestPrototypeController;
import top.atstudy.framework.exception.APIException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author huangdexin @ harley
 * @email huangdexin@kuaicto.com
 * @date 2019/7/12 9:54
 */
@RestPrototypeController
@RequestMapping("/qrcode")
public class QrcodeController {
    private static final Logger logger = LoggerFactory.getLogger(QrcodeController.class);

    @Autowired
    private QrcodeService qrcodeService;

    @GetMapping("/file")
    public String generalQcCodeImage(@RequestParam String content) throws APIException, IOException, WriterException {
        return qrcodeService.writeQrCodeFile(content);
    }

    @GetMapping("/stream")
    public void generalQcCodeStream(HttpServletResponse response, @RequestParam String content) throws APIException, IOException, WriterException {
        qrcodeService.writeQrCodeOutputStream(content, response.getOutputStream());
    }

}
