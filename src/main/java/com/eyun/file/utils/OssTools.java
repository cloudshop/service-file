package com.eyun.file.utils;

public class OssTools {
//	@Autowired
//	private OssProperties ossProperties;
//
//    private static OSSClient client = null;
//	private static Logger logger = Logger.getLogger(OssTools.class);
//
//    private OssTools() {
//
//    }
//
//    synchronized public static OSSClient getClient() {
//    	if (client == null) {
////    		client = new OSSClient(ossProperties.get, FileSystemConstant.ACCESS_ID, FileSystemConstant.ACCESS_KEY);
//    	}
//        return client;
//    }
//
//    // 动态创建目录
//    public static void createFolder(OSSClient client, String folderName, String bucketName) throws Exception {
//
//        ObjectMetadata objectMeta = new ObjectMetadata();
//
//        byte[] buffer = new byte[0];
//        ByteArrayInputStream in = new ByteArrayInputStream(buffer);
//        objectMeta.setContentLength(0);
//
//        client.putObject(bucketName, folderName, in, objectMeta);
//        in.close();
//    }
//
//    // 创建Bucket（如果不存在）.
//    public static void ensureBucket(OSSClient client, String bucketName) throws OSSException, ClientException {
//
//        try {
//            // 创建bucket
//            if (client.doesBucketExist(bucketName) == false) {
//                client.createBucket(bucketName);
//            }
//        } catch (ServiceException e) {
//
//            throw e;
//        }
//    }
//
//    // 删除一个Bucket和其中的Objects
//    public static void deleteBucket(OSSClient client, String bucketName) throws OSSException, ClientException {
//
//        ObjectListing ObjectListing = client.listObjects(bucketName);
//        List<OSSObjectSummary> listDeletes = ObjectListing.getObjectSummaries();
//        for (int i = 0; i < listDeletes.size(); i++) {
//            String objectName = listDeletes.get(i).getKey();
//            // 如果不为空，先删除bucket下的文件
//            client.deleteObject(bucketName, objectName);
//        }
//        client.deleteBucket(bucketName);
//    }
//
//    // 把Bucket设置为所有人可读
//    public static void setBucketPublicReadable(OSSClient client, String bucketName) throws OSSException, ClientException {
//        // 创建bucket
//        client.createBucket(bucketName);
//
//        // 设置bucket的访问权限，public-read-write权限
//        client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
//    }
//
//    // 上传文件
//    public static void uploadFile(OSSClient client, String key, File file, String bucketName) throws OSSException, ClientException, IOException {
//        ObjectMetadata objectMeta = new ObjectMetadata();
//        objectMeta.setContentLength(file.length());
//
//        InputStream input = new FileInputStream(file);
//        logger.info("uploadFile bucketName：" + bucketName + " key :" + key);
//        PutObjectResult ret = client.putObject(bucketName, key, input, objectMeta);
//        logger.info("uploadFile return：" + ret.toString());
//        input.close();
//    }
//
//    // 下载文件
//    public static void downloadFile(OSSClient client, String key, String filename, String bucketName) throws OSSException, ClientException {
//        client.getObject(new GetObjectRequest(bucketName, key), new File(filename));
//    }
//
//    //删除文件
//    public static void deleteObject(OSSClient client, String bucketName,String key) throws OSSException, ClientException {
//
//       client.deleteObject(bucketName, key);
//    }
//
//
//
}
