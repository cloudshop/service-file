package com.eyun.file.utils;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.eyun.file.config.OssProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class OssUtil {
    private static final Logger logger = LoggerFactory.getLogger(OssUtil.class);

    private  OSSClient ossClient=null;

    private  Bucket bucket=null;
    @Autowired
    private OssProperties ossProperties;
    @Autowired
    RedisTemplate redisTemplate;

    public String getBucketName(){
        return ossProperties.getBucket_name();
    }

    public String getEndpoint(){
        return ossProperties.getEndpoint();
    }

    public String getAccess_id(){
        return ossProperties.getAccess_id();
    }

    public String getAccess_key(){
        return ossProperties.getAccess_key();
    }
    /*初始化OssClient(默认私有读写)*/
    public  OSSClient createOSSClient(){
        try {
            if (ossClient==null){
                // 创建ClientConfiguration实例，按照您的需要修改默认参数
                ClientConfiguration conf = new ClientConfiguration();
                ossClient = new OSSClient(getEndpoint(), getAccess_id(), getAccess_key());
            }
            return ossClient;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*初始化Bucket(默认私有读写)*/
    public Bucket createBcket(){
        try{
            if (bucket==null){
                OSSClient ossClient =createOSSClient();
                bucket=ossClient.createBucket(getBucketName());
            }
            return bucket;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*单个文件上传 上传后返回URL
    * */
    public Map ossUpload(MultipartFile file) {
        try {
            // 上传aliyun
            OSSClient ossClient =createOSSClient();
            if (ossClient==null){
                return null;
            }
            Map<String,String> resultMap=new HashMap<String,String>();
            String bucketName=getBucketName();
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            String imageName =file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
            String fileName=uuid+imageName;
            resultMap.put("fileName",fileName);
            logger.info("fileName::"+fileName);
            PutObjectResult result=ossClient.putObject(bucketName, fileName, new ByteArrayInputStream(file.getBytes()));
            String fileId=result.getETag();
            // 设置URL过期时间为100年 3600l* 1000*24*365*100
            Date expirations = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 10);// url超时时间
            URL url = ossClient.generatePresignedUrl(bucketName, fileName, expirations);
            String key="img_"+uuid;
            redisTemplate.boundValueOps(key).set(url.toString());
            resultMap.put("url",url.toString());
            logger.info("url::"+url);
            ossClient.shutdown();
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

     /*
     * 获取文件oss存储地址
     * @Param 文件名
     * return URL
     * */
    public  String getUrl(String fileName) {
        OSSClient ossClient=createOSSClient();
        if (ossClient==null){
            return null;
        }
        Date expirations = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 10);// url超时时间
        URL url = ossClient.generatePresignedUrl(getBucketName(), fileName, expirations);
        logger.info("url::"+url);
        return url.toString();
    }
    /*public static void main(String[]args)throws Exception{
        *//****************************初始化OssClient(默认私有读写)****************************************//*
        String bucket="gongrong";
        String endpoint="http://oss-cn-beijing.aliyuncs.com";
        String access_id="LTAIqb9rnlpi7ehS";
        String access_key="gcM7tjcAKAM5PfpqlRnMeG115PYQOC";
        String key=access_id+access_key;
        OSSClient ossClient = new OSSClient(endpoint,access_id, access_key);
        //logger.info("ObjectAcl"+ossClient.getObjectAcl(bucket,key));
        *//*********************************获取文件的全部元信息******************************//*
        *//*ObjectMetadata metadata = ossClient.getObjectMetadata(bucket, key);
        logger.info(metadata.getETag());
        logger.info(metadata.getContentDisposition());
        logger.info(metadata.getRawExpiresValue());
        logger.info(metadata.getObjectType());*//*
        *//*******************************上传文件******************************************//*
        File file=new File("C:\\Users\\admin\\Desktop\\1521793359.png");
        String fileName=file.getName().substring(file.getName().indexOf("."));
        logger.info("fileName::"+fileName);
        *//*FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[2048];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        fis.close();
        bos.close();
        byte[]buffer = bos.toByteArray();*//*
        // 生成uuid作为文件名称
       *//* String uuid = UUID.randomUUID().toString().replaceAll("-","");
        logger.info("uuid::"+uuid);*//*
        *//*PutObjectResult result=ossClient.putObject(bucket, uuid+fileName, file);
        String fileId=result.getETag();
        logger.info("ETag::"+fileId);*//*
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        URL url = ossClient.generatePresignedUrl(bucket, "365d6df1f952497580cbbfa120f57a35.png", expiration);
        logger.info("url::"+url);
        *//*******************************获取所有文件*********************************************************//*
        // 构造ListObjectsRequest请求
        //ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket).withMaxKeys(100);
        ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest( bucket));
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
        logger.info("\t" + sums.size());
        List keyList=new ArrayList();
        if (sums.size()>0){
            for (OSSObjectSummary s : sums) {
                logger.info("\t" + s.getETag());
                logger.info("\t" + s.getKey());
                keyList.add(s.getKey());
            }
        }

        *//*******************************删除文件*********************************************************//*
        *//*DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucket).withKeys(keyList));
        keyList.clear();*//*
        ossClient.shutdown();
    }*/
}
