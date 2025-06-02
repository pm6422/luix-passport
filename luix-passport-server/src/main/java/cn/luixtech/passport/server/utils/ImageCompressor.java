package cn.luixtech.passport.server.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageCompressor {
    // 压缩配置
    private static final int   MAX_WIDTH  = 300;
    private static final int   MAX_HEIGHT = 300;
    private static final float QUALITY    = 0.5f;

    public static byte[] compressImage(MultipartFile file) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(inputStream);

            // 计算新尺寸，保持宽高比
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int newWidth = originalWidth;
            int newHeight = originalHeight;

            if (originalWidth > MAX_WIDTH || originalHeight > MAX_HEIGHT) {
                double ratio = Math.min(
                        (double) MAX_WIDTH / originalWidth,
                        (double) MAX_HEIGHT / originalHeight
                );
                newWidth = (int) (originalWidth * ratio);
                newHeight = (int) (originalHeight * ratio);
            }

            // 执行压缩
            Thumbnails.of(originalImage)
                    .size(newWidth, newHeight)
                    .outputFormat(getFormatName(file.getContentType()))
                    .outputQuality(QUALITY)
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
    }

    private static String getFormatName(String contentType) {
        return contentType != null && contentType.equals("image/png") ? "png" : "jpg";
    }
}