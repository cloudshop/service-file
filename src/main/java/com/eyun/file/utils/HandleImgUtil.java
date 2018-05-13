package com.eyun.file.utils;


import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

public class HandleImgUtil {

    /**
     * @Description: 图片压缩处理
     * @param inputPath
     * @param outPath
     * @param width
     * @param height
     * @param suffix 图片名后缀
     * @param quality
     * @return void
     * @throws
     * @author zhusen
     * @date 2015-6-5
     */
    public static void compress(String inputPath, String outPath, int width, int height, String suffix, float quality) {
        try {
            Thumbnails.of(inputPath)
            /*
             * forceSize,size和scale必须且只能调用一个
             */
            // .forceSize(400, 400) //生成的图片一定为400*400
            /*
             * 若图片横比200小，高比300小，不变 若图片横比200小，高比300大，高缩小到300，图片比例不变
             * 若图片横比200大，高比300小，横缩小到200，图片比例不变
             * 若图片横比200大，高比300大，图片按比例缩小，横为200或高为300
             */
            .size(width, height) // 按尺寸修改图片
            .outputFormat(suffix) // 生成图片的格式为png
            .outputQuality(quality) // 生成质量为80%
            // .scale(0.5f) //缩小50%
            // 输出到桌面5文件
            .toFile(outPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String handleImg(int width, int height, double targetWidth, double targetHeight, String inputPath, String outPutPath, String suffix) {
        if (BigFileFiler.accept(inputPath) || width > targetWidth || height > targetHeight) {
            double sx = targetWidth / width;
            double sy = targetHeight / height;
            if (sx < sy) {
                width = (int) (width * sy);
                height = (int) (height * sy);
            } else {
                width = (int) (width * sx);
                height = (int) (height * sx);
            }
            float quality = 1;
            if (BigFileFiler.accept(inputPath)) {
                long fileSize = BigFileFiler.getFileSize(inputPath);
                quality = (float) (80000.00 / fileSize);
                if (quality < 0.8) {
                    quality = 0.8f;
                }
            }
            HandleImgUtil.compress(inputPath, outPutPath.substring(0, outPutPath.lastIndexOf(".")), width, height, suffix, quality);
        } else {
			try {
				FileCopyUtils.copy(new File(inputPath), new File(outPutPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return inputPath;
    }
}
