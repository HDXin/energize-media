package top.atstudy.energize.media.service;

import java.io.File;
import java.io.IOException;

public interface ImageCompressor {

    public abstract void compressImage(File srcFile, File targetFile) throws IOException;

}