package com.eyun.file.utils;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.eyun.file.config.OssProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Component
public class OssUtil {
    private static final Logger logger = LoggerFactory.getLogger(OssUtil.class);

    private OSSClient ossClient = null;

    private Bucket bucket = null;
    @Autowired
    private OssProperties ossProperties;
    /*@Autowired
    RedisTemplate redisTemplate;*/

    public String getBucketName() {
        return ossProperties.getBucket_name();
    }

    public String getEndpoint() {
        return ossProperties.getEndpoint();
    }

    public String getAccess_id() {
        return ossProperties.getAccess_id();
    }

    public String getAccess_key() {
        return ossProperties.getAccess_key();
    }

    /*初始化OssClient(默认私有读写)*/
    public OSSClient createOSSClient() {
        try {
            if (ossClient == null) {
                // 创建ClientConfiguration实例，按照您的需要修改默认参数
                ClientConfiguration conf = new ClientConfiguration();
                conf.setMaxConnections(1024);//允许打开的最大HTTP连接数
                conf.setSocketTimeout(30000);//Socket层传输数据的超时时间（单位：毫秒）
                conf.setConnectionTimeout(5000);//建立连接的超时时间（单位：毫秒）
                conf.setIdleConnectionTime(50000);//如果空闲时间超过此参数的设定值，则关闭连接（单位：毫秒）
                ossClient = new OSSClient(getEndpoint(), getAccess_id(), getAccess_key(), conf);
            }
            return ossClient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*初始化Bucket(默认私有读写)*/
    public Bucket createBcket() {
        try {
            if (bucket == null) {
                OSSClient ossClient = createOSSClient();
                bucket = ossClient.createBucket(getBucketName());
            }
            return bucket;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*单个文件上传 上传后返回URL
     * */
    public String ossUpload(MultipartFile file) throws Exception {
        // 上传aliyun
        OSSClient ossClient = createOSSClient();
        if (ossClient == null) {
            return null;
        }
        String bucketName = getBucketName();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String imageName = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
        String fileName = uuid + imageName;
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(file.getSize());
        PutObjectResult result = ossClient.putObject(bucketName, fileName, new ByteArrayInputStream(file.getBytes()), meta);
        String eTag = result.getETag();//唯一MD5数字签名
        logger.info("ETag::" + eTag);
        // 设置URL过期时间为100年 3600l* 1000*24*365*100
        Date expirations = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 10);// url超时时间
        URL url = ossClient.generatePresignedUrl(bucketName, fileName, expirations);
        //ossClient.shutdown();
           /* String key="img_"+uuid;
            redisTemplate.boundValueOps(key).set(url.toString());*/
        logger.info("url::" + url);
        return url.toString();
    }

    /*apk上传(分片上传)*/
    public String ossUploadApk(MultipartFile file) throws Throwable {
        OSSClient ossClient = createOSSClient();
        if (ossClient == null) {
            return null;
        }
        String bucketName = getBucketName();
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, file.getOriginalFilename());
        InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
        String uploadId = result.getUploadId();
        List<PartETag> partETags = new ArrayList<PartETag>();
        final long partSize = 3 * 1024 * 1024L;   // 3MB
        long fileLength = file.getSize();
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize ==0) {
            partCount++;
        }
        for (int i = 0; i < partCount; i++) {
            long startPos = i * partSize;
            long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
            InputStream instream = file.getInputStream();
            //跳过已经上传的分片。
            instream.skip(startPos);
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucketName);
            uploadPartRequest.setKey(file.getOriginalFilename());
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(instream);
            // 设置分片大小。除了最后一块Part没有大小限制，其他的Part最小为100KB。
            uploadPartRequest.setPartSize(curPartSize);
            // 设置分片号。每一个上传的Part都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
            uploadPartRequest.setPartNumber(i + 1);
            //每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
            UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
            //每次上传Part之后，OSS的返回结果会包含一个PartETag对象，它是上传块的ETag与块编号（PartNumber）的组合。在后续完成分片上传的步骤中会用到它，因此我们需要将其保存起来，在步骤3中使用。
            partETags.add(uploadPartResult.getPartETag());
        }
        Collections.sort(partETags, new Comparator<PartETag>() {
            public int compare(PartETag p1, PartETag p2) {
                return p1.getPartNumber() - p2.getPartNumber();
            }
        });
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
            new CompleteMultipartUploadRequest(bucketName, file.getOriginalFilename(), uploadId, partETags);
        ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        String url=this.getUrl(file.getOriginalFilename());
        return url;
    }

    /*
     * 获取文件oss存储地址
     * @Param 文件名
     * return URL
     * */
    public String getUrl(String fileName) {
        OSSClient ossClient = createOSSClient();
        if (ossClient == null) {
            return null;
        }
        Date expirations = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 10);// url超时时间
        URL url = ossClient.generatePresignedUrl(getBucketName(), fileName, expirations);
        logger.info("url::" + url);
        return url.toString();
    }

    public String downLoadFile(String fileName)throws Throwable{
        OSSClient ossClient = createOSSClient();
        if (ossClient == null) {
            return null;
        }
        DownloadFileRequest downloadFileRequest = new DownloadFileRequest(getBucketName(), fileName);
        File file=new File("E:\\testfile\\"+fileName);
        downloadFileRequest.setDownloadFile(file.getPath());
        downloadFileRequest.setPartSize(1 * 1024 * 1024);
        downloadFileRequest.setTaskNum(10);
        downloadFileRequest.setEnableCheckpoint(true);
        DownloadFileResult downloadRes = ossClient.downloadFile(downloadFileRequest);
        String eTag=downloadRes.getObjectMetadata().getETag();
        logger.info("ETag:"+eTag);
        return eTag;
    }
}
