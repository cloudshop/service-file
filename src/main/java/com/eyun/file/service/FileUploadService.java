package com.eyun.file.service;

import com.eyun.file.config.FileProperties;
import com.eyun.file.config.ImageProperties;
import com.eyun.file.utils.HandleImgUtil;
import com.eyun.file.utils.OssUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.newdun.file.config.OssProperties;
//import com.newdun.file.utils.OssTools;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class FileUploadService {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private FileProperties fileProperties;
    @Autowired
    private ImageProperties imageProperties;
    @Autowired
    private OssUtil ossUtil;

    String pathTemp = "temp/";
    String pathImage = "image/";

    public Optional<File> downLoadFile(String path) {
        File file = null;
        InputStream inStream = null;
        FileOutputStream os = null;

        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            inStream = conn.getInputStream();

            if (inStream != null) {
                File outputPath = new File(fileProperties.getPathOutput());
                if (!outputPath.exists()) {
                    outputPath.mkdirs();
                }
                file = new File(fileProperties.getPathOutput() + System.currentTimeMillis()
                    + path.substring(path.lastIndexOf(".")));
                byte[] buffer = new byte[4096];
                int read = 0;
                os = new FileOutputStream(file);
                while ((read = inStream.read(buffer)) > 0) {
                    os.write(buffer, 0, read);
                }
                return Optional.of(file);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Optional<String> ossUpload(MultipartFile file) {
        // if (file == null) {
        // return null;
        // }
        // try {
        // final OSSClient client = OssTools.getClient();
        // OssTools.ensureBucket(client, ossProperties.getBucket_name());
        // OssTools.setBucketPublicReadable(client,
        // ossProperties.getBucket_name());
        //
        // Date date = new Date();
        // String dateStr = new SimpleDateFormat("yyyyMM/dd").format(date);
        // StringBuffer keyBuffer = new
        // StringBuffer(fileProperties.getStatics());
        // keyBuffer.append(dateStr);
        //
        // final String key = keyBuffer.toString();
        // final String fileName = date.getTime() + ".jpg";
        // File outputPath = new File(appFileTemp);
        // if (!outputPath.exists()) {
        // outputPath.mkdirs();
        // }
        //
        // final String tempSavePath = appFileTemp + "/" + fileName;
        // byte[] bytes = file.getBytes();
        // BufferedOutputStream stream = new BufferedOutputStream(new
        // FileOutputStream(new File(tempSavePath)));
        // stream.write(bytes);
        // stream.close();
        //
        // final File tempFile = new File(tempSavePath);
        // if (tempFile.exists()) {
        // String tempOriginFilePath = appFileTemp + "/" + "Origin";
        // File tempOriginFolder = new File(tempOriginFilePath);
        // if (!tempOriginFolder.exists()) {
        // tempOriginFolder.mkdirs();
        // }
        //
        // double targetOriginWidth = 600.00;
        // double targetOriginHeight = 400.00;
        // int OriginWidth = 0;
        // int OriginHeight = 0;
        // ImageInputStream iis = null;
        // try {
        // Iterator<ImageReader> readers =
        // ImageIO.getImageReadersByFormatName("jpg");
        // ImageReader reader = (ImageReader) readers.next();
        // iis = ImageIO.createImageInputStream(file);
        // reader.setInput(iis, true);
        // OriginWidth = reader.getWidth(0);
        // OriginHeight = reader.getHeight(0);
        // } catch (Exception ex) {
        // try {
        // BufferedImage bi = ImageIO.read(tempFile);
        // OriginWidth = bi.getWidth();
        // OriginHeight = bi.getHeight();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // } finally {
        // if (iis != null) {
        // iis.close();
        // }
        // }
        //
        // final int width = OriginWidth;
        // final int height = OriginHeight;
        // String OriginFilePath = tempOriginFilePath + "/" + fileName;
        // OriginFilePath = HandleImgUtil.handleImg(width, height,
        // targetOriginWidth, targetOriginHeight,
        // tempSavePath, OriginFilePath, "jpg");
        // final File OriginFile = new File(OriginFilePath);
        // OssTools.uploadFile(client, key + "/" + fileName, OriginFile,
        // ossProperties.getBucket_name());
        //
        // taskExecutor.execute(new Runnable() {
        // public void run() {
        // double targetWidth = 480.00;
        // double targetHeight = 320.00;
        // File smallFile = null;
        // File nailFile = null;
        // try {
        // String tempSmallFilePath = appFileTemp + "/" + "small";
        // File tempSmallFolder = new File(tempSmallFilePath);
        // if (!tempSmallFolder.exists()) {
        // tempSmallFolder.mkdirs();
        // }
        // String smallFilePath = tempSmallFilePath + "/" + fileName;
        // smallFilePath = HandleImgUtil.handleImg(width, height, targetWidth,
        // targetHeight,
        // tempSavePath, smallFilePath, "jpg");
        // smallFile = new File(smallFilePath);
        // OssTools.uploadFile(client, "small/" + key + "/" + fileName,
        // smallFile,
        // ossProperties.getBucket_name());
        //
        // String tempNailFilePath = appFileTemp + "/" + "nail";
        // File tempNailFolder = new File(tempNailFilePath);
        // if (!tempNailFolder.exists()) {
        // tempNailFolder.mkdirs();
        // }
        // String nailFilePath = tempNailFilePath + "/" + fileName;
        // nailFilePath = HandleImgUtil.handleImg(width, height, targetWidth /
        // 2, targetHeight / 2,
        // tempSavePath, nailFilePath, "jpg");
        // nailFile = new File(nailFilePath);
        // OssTools.uploadFile(client, "nail/" + key + "/" + fileName, nailFile,
        // ossProperties.getBucket_name());
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // } finally {
        // if (tempFile != null && tempFile.exists()) {
        // tempFile.delete();
        // }
        // if (OriginFile != null && OriginFile.exists()) {
        // OriginFile.delete();
        // }
        // if (smallFile != null && smallFile.exists()) {
        // smallFile.delete();
        // }
        // if (nailFile != null && nailFile.exists()) {
        // nailFile.delete();
        // }
        // }
        // }
        // });
        // StringBuffer filePath = new StringBuffer("/");
        // filePath.append(key.toString());
        // filePath.append("/");
        // filePath.append(fileName);
        //
        // return Optional.of(filePath.toString());
        // }
        // } catch (Exception e) {
        //
        // }
        return null;
    }

    public Optional<String> uploadToLocal(MultipartFile file) {
        if (file == null) {
            return null;
        }
        // created path & file name according the date and thread id.
        try {
            Date date = new Date();
            String pathRelation = new SimpleDateFormat("yyyy/MM/dd/").format(date);

            final String fileName = String.valueOf(date.getTime());

            int normalSize = imageProperties.getImageOriginHeight() * imageProperties.getImageOriginWidth();
            final String pathOriginFull = fileProperties.getPathOutput() + pathImage + pathRelation;
            File fileTempFull = new File(pathOriginFull);
            if (!fileTempFull.exists()) {
                fileTempFull.mkdirs();
            }

            String pathTempFull;
            if (normalSize <= 0) {
                pathTempFull = pathOriginFull;
            } else {
                pathTempFull = pathTemp + pathImage + pathRelation;
                fileTempFull = new File(pathTempFull);
                if (!fileTempFull.exists()) {
                    fileTempFull.mkdirs();
                }
            }

            byte[] bytes = file.getBytes();
            BufferedOutputStream stream = new BufferedOutputStream(
                new FileOutputStream(new File(pathTempFull + fileName + ".jpg")));
            stream.write(bytes);
            stream.close();

            final File fileTemp = new File(pathTempFull + fileName + ".jpg");
            taskExecutor.execute(new Runnable() {
                public void run() {
                    // 获取图像大小
                    int imageWidth = 0;
                    int imageHeight = 0;
                    ImageInputStream iis = null;
                    try {
                        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
                        ImageReader reader = (ImageReader) readers.next();
                        iis = ImageIO.createImageInputStream(file);
                        reader.setInput(iis, true);
                        imageWidth = reader.getWidth(0);
                        imageHeight = reader.getHeight(0);
                    } catch (Exception ex) {
                        try {
                            BufferedImage bi = ImageIO.read(fileTemp);
                            imageWidth = bi.getWidth();
                            imageHeight = bi.getHeight();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } finally {
                        if (iis != null) {
                            try {
                                iis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (normalSize > 0) { // 对原图进行尺寸压缩

                        double targetOriginWidth = imageProperties.getImageOriginWidth();
                        double targetOriginHeight = imageProperties.getImageOriginHeight();
                        HandleImgUtil.handleImg(imageWidth, imageHeight, targetOriginWidth, targetOriginHeight,
                            pathTempFull + fileName + ".jpg", pathOriginFull + fileName + ".jpg", "jpg");
                    }

                    // small
                    if (!StringUtils.isEmpty(imageProperties.getImageSmallPath())) {
                        String pathSmallFull = fileProperties.getPathOutput() + pathImage + pathRelation;
                        File fileTempFull = new File(pathSmallFull);
                        if (!fileTempFull.exists()) {
                            fileTempFull.mkdirs();
                        }

                        HandleImgUtil.handleImg(imageWidth, imageHeight, imageProperties.getImageSmallWidth(),
                            imageProperties.getImageSmallHeight(), pathTempFull + fileName + ".jpg",
                            pathSmallFull + fileName + "." + imageProperties.getImageSmallPath() + ".jpg", "jpg");
                    }

                    // nail
                    if (!StringUtils.isEmpty(imageProperties.getImageNailPath())) {
                        String pathNailFull = fileProperties.getPathOutput() + pathImage + pathRelation;
                        File fileTempFull = new File(pathNailFull);
                        if (!fileTempFull.exists()) {
                            fileTempFull.mkdirs();
                        }

                        HandleImgUtil.handleImg(imageWidth, imageHeight, imageProperties.getImageNailWidth(),
                            imageProperties.getImageNailHeight(), pathTempFull + fileName + ".jpg",
                            pathNailFull + fileName + "." + imageProperties.getImageNailPath() + ".jpg", "jpg");
                    }

                    // delete the temp file
                    if (normalSize > 0) {
                        File fileTemp = new File(pathTempFull + fileName);
                        fileTemp.delete();
                    }
                }
            });

            return Optional.of(pathImage + pathRelation + fileName + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> ossFileUpload(MultipartFile[] file) throws Exception {
        List<String> list = new ArrayList<>();
        for (MultipartFile multipartFile : file) {
            String url = ossUtil.ossUpload(multipartFile);
            list.add(url);
        }
        return list;
    }

    public String ossUploadApk(MultipartFile file) throws Throwable {
        return ossUtil.ossUploadApk(file);
    }

    public String getImgUrl(String fileName) {
        return ossUtil.getUrl(fileName);
    }
    public String ossDownLoadFile(String fileName)throws Throwable{
        return ossUtil.downLoadFile(fileName);
    }
}
