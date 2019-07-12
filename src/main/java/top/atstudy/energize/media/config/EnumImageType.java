package top.atstudy.energize.media.config;

/**
 * Created by Spector on 2017/6/26.
 */
public enum EnumImageType {
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png"),
    TIF("tif", "image/tiff"),
    TIFF("tiff", "image/tiff"),
    ICO("ico", "image/x-icon"),
    BMP("bmp", "image/bmp"),
    GIF("gif", "image/gif");

    public String suffix;
    public String type;

    EnumImageType(String suffix, String type) {
        this.suffix = suffix;
        this.type = type;
    }

    public static EnumImageType typeOf(String suffix) {
        EnumImageType[] values = EnumImageType.values();
        for (EnumImageType imageType : values) {
            if (imageType.suffix.equals(suffix)) {
                return imageType;
            }
        }

        throw new IllegalArgumentException("Invalid ImageType suffix: " + suffix);
    }
}
