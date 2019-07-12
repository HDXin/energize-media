package top.atstudy.energize.media.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.atstudy.energize.media.service.ImageCompressor;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.Iterator;

@Component
public class DefaultImageCompressorImpl implements ImageCompressor {

    @Value("${image.quality}")
    private float quality;

    public void compressImage(File srcFile, File targetFile) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcFile);
            BufferedImage srcImage = ImageIO.read(fis);

            //核心对象操作对象
            Metadata metadata = null;
            try {
                InputStream is = new FileInputStream(srcFile);
                metadata = ImageMetadataReader.readMetadata(is);
                //获取所有不同类型的Directory，如ExifSubIFDDirectory, ExifInteropDirectory, ExifThumbnailDirectory等，这些类均为ExifDirectoryBase extends Directory子类
                //分别遍历每一个Directory，根据Directory的Tags就可以读取到相应的信息
                Iterable<Directory> iterable = metadata.getDirectories();
                for (Iterator<Directory> iter = iterable.iterator(); iter.hasNext(); ) {
                    Directory dr = iter.next();
                    Collection<Tag> tags = dr.getTags();
                    for (Tag tag : tags) {
                        if (tag.getTagName().equals("Orientation")) {
                            if (tag.getDescription().startsWith("Right")) {
                                srcImage = rotateImage(srcImage, 90);
                                break;
                            }
                        }
                    }
                }
            } catch (ImageProcessingException e) {
                e.printStackTrace();
            }


            fos = new FileOutputStream(targetFile);
            compressImage(srcImage, fos, this.quality);
        } finally {
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(fis);
        }
    }

    private void compressImage(BufferedImage srcImage, OutputStream outputStream, float quality) throws IOException {

        //构建图片对象
        final int imageType = srcImage.isAlphaPremultiplied() ? BufferedImage.TRANSLUCENT : BufferedImage.TYPE_INT_RGB;
        BufferedImage bufferedImage = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), imageType);
        //绘制缩放后的图
        Image scaledInstance = srcImage.getScaledInstance(srcImage.getWidth(), srcImage.getHeight(), Image.SCALE_SMOOTH);
        bufferedImage.getGraphics().drawImage(scaledInstance, 0, 0, srcImage.getWidth(), srcImage.getHeight(), Color.WHITE, null);

        Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter imageWriter = iterator.next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        // 压缩设置
        if (imageWriteParam.canWriteCompressed()) {
            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            imageWriteParam.setCompressionQuality(quality);
        }
        ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(outputStream);
        imageWriter.setOutput(imageOutputStream);

        IIOImage iioimage = new IIOImage(bufferedImage, null, null);
        imageWriter.write(null, iioimage, imageWriteParam);
        imageOutputStream.flush();
    }

    public static BufferedImage rotateImage(final BufferedImage bufferedimage,
                                            final int degree) {
        int w = bufferedimage.getWidth();
        int h = bufferedimage.getHeight();
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(w, h, type))
                .createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
        graphics2d.drawImage(bufferedimage, 0, 0, null);
        graphics2d.dispose();
        return img;
    }


}
