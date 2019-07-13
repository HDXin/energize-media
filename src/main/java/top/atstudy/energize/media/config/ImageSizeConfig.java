package top.atstudy.energize.media.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageSizeConfig {

    @Value("${image.size}")
    private String imageSize;

    private List<ImageSize> imageSizeList = new ArrayList<>();

    public String getImageSize() {
        return imageSize;
    }
    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public List<ImageSize> getImageSizeList() {
        if (StringUtils.isNotBlank(imageSize)) {
            this.imageSizeList.addAll(Arrays.stream(imageSize.split(",")).map(ImageSize::new).collect(Collectors.toList()));
        }
        return imageSizeList;
    }

    public void setImageSizeList(List<ImageSize> imageSizeList) {
        this.imageSizeList = imageSizeList;
    }
}
