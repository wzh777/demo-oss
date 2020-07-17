package com.hong.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.hong.config.OssConfig;
import com.hong.entity.dto.UploadDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * @author 吴志鸿
 * @date 2020/7/15
 * @description 阿里云OSS连接工具
 */
@Slf4j
@Component
public class AliyunOSSUtil {

    /**
     * bucketName的名字
     */
    private static String bucketName = OssConfig.bucketName;

    /**
     * 访问地域节点
     */
    private static String endpoint = OssConfig.endpoint;

    /**
     * 访问密钥
     */
    private static String accessKeyId = OssConfig.accessKeyId;

    /**
     * 访问密钥密码
     */
    private static String accessKeySecret = OssConfig.accessKeySecret;

    @Autowired
    UploadByPart uploadByPart;

    /**
     * 初始化日志文件
     */
    private static Logger logger = LoggerFactory.getLogger(AliyunOSSUtil.class);


    /**
     * 普通的上传方法
     *
     * @param file 文件地址
     */
    public UploadDto upLoad(MultipartFile file) {
        //图片访问路径
        String url = null;

        logger.info("文件上传中..........");

        //记录程序开始时间
        long startTime = System.currentTimeMillis();

        // 判断文件是否为空
        if (file == null) {
            return null;
        }

        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        UploadDto uploadDto = new UploadDto();
        try {
            // 首先判断容器是否存在,如果不存在创建容器
            if (!ossClient.doesBucketExist(bucketName)) {
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }
            // 设置文件路径和名称
            String fileUrl = UUID.randomUUID().toString().replace("-", "")
                    + "-" + file.getOriginalFilename();
            if ("image/jpg".equals(getContentType(file.getOriginalFilename()))) {
                url = "https://" + bucketName + "." + endpoint + "/" + fileUrl;
            } else {
                url = "非图片，不可预览。文件路径为：" + fileUrl;
            }

            // 创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            InputStream inputStream = file.getInputStream();
            //设置文件上传格式
            objectMetadata.setContentType(getContentType(file.getOriginalFilename()));
            // 上传文件
            PutObjectResult result = ossClient.putObject(bucketName, fileUrl, inputStream, objectMetadata);
            // 设置权限(公开读)
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            if (result != null) {
                long endTime = System.currentTimeMillis();
                logger.info("文件上传成功");
                uploadDto.setName(fileUrl);
                uploadDto.setUrl(url);
                uploadDto.setCostTime(endTime - startTime + "ms");
            } else {
                logger.info("文件上传失败");
            }
        } catch (OSSException oe) {
            logger.info("出错原因是{}", oe.getMessage());
        } catch (IOException e) {
            logger.info("出错原因是{}", e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return uploadDto;
    }

    /**
     * 普通的下载方法
     *
     * @param objectName 文件名
     */
    public void downLoad(String objectName) {
        String[] split = objectName.split("/");
        String filePath = "D:\\bosssoftware\\aliyuntest\\" + split[split.length - 1];
        File file = new File(filePath);

        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            logger.info("文件开始下载");
            ObjectMetadata object = ossClient.getObject(new GetObjectRequest(bucketName, objectName), file);
            if (object != null) {
                logger.info("文件下载成功");
            }
        } catch (OSSException oe) {
            logger.info("下载文件出错", oe);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 获取文件
     */
    public List<String> list() {
        // 设置最大个数。
        final int maxKeys = 200;
        ArrayList<String> list = new ArrayList<>();

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 列举文件。
            ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName).withMaxKeys(maxKeys));
            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                list.add(objectSummary.getKey());
            }

        } catch (OSSException oe) {
            logger.info("下载文件出错", oe);
        } finally {
            ossClient.shutdown();
        }
        return list;
    }

    /**
     * 获取文件类型
     *
     * @param fileName 文件名
     */
    public static String getContentType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
        if (".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if (".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
            return "image/jpg";
        }
        if (".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if (".txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if (".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if (".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if (".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if (".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        return "image/jpg";
    }


    /**
     * 多线程加分片上传
     *
     * @param file 文件地址
     */
    public UploadDto multipartUpload(MultipartFile file) {
        UploadDto uploadDto = new UploadDto();

        //图片访问路径
        String url = null;

        logger.info("文件上传中..........");

        //记录程序开始时间
        long startTime = System.currentTimeMillis();

        // 判断文件是否为空
        if (file == null) {
            return null;
        }

        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 首先判断容器是否存在,如果不存在创建容器
            if (!ossClient.doesBucketExist(bucketName)) {
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }
            // 设置文件路径和名称
            String fileUrl = UUID.randomUUID().toString().replace("-", "")
                    + "-" + file.getOriginalFilename();
            if ("image/jpg".equals(getContentType(file.getOriginalFilename()))) {
                url = "https://" + bucketName + "." + endpoint + "/" + fileUrl;
            } else {
                url = "非图片，不可预览。文件路径为：" + fileUrl;
            }

            // key是文件名 By berton
            String uploadId = uploadByPart.claimUploadId(ossClient,bucketName, fileUrl);
            //设置每块的大小，10M
            long partSize = 15 * 1024 * 1024L;
            //获取文件大小
            long fileSize = file.getSize();
            //计算分块数量
            int partCount = (int) (fileSize / partSize);
            if (fileSize % partSize != 0) {
                partCount++;
            }

            // 分块 号码的范围是1~10000。如果超出这个范围，OSS将返回InvalidArgument的错误码。
            if (partCount > 10000) {
                throw new RuntimeException("文件过大(分块大小不能超过10000)");
            } else {
                logger.info("一共分了 {}块",partCount);
            }

            ArrayList<Future<String>> futureList = new ArrayList<>();
            for (int i = 0; i < partCount; i++) {
                // 起始point
                long startPos = i * partSize;
                // 判断当前partSize的长度 是否最后一块
                long curPartSize = (i + 1 == partCount) ? (fileSize - startPos) : partSize;
                futureList.add(uploadByPart.quickUpLoad(ossClient,file, startPos, curPartSize, i + 1, uploadId, fileUrl, bucketName));
            }

            //用于判断线程是否全部结束，全部结束才执行下一个代码
            while (true) {
                if (futureList.isEmpty()) {
                    break;
                }
                Iterator<Future<String>> iterator = futureList.iterator();
                while (iterator.hasNext()){
                    if (iterator.next().isDone()){
                        iterator.remove();
                    }
                }
            }

            CompleteMultipartUploadResult result = uploadByPart.completeMultipartUpload(ossClient,uploadId, fileUrl, bucketName);

            if (result != null) {
                long endTime = System.currentTimeMillis();
                logger.info("文件上传成功");
                uploadDto.setName(fileUrl);
                uploadDto.setUrl(url);
                uploadDto.setCostTime(endTime - startTime + "ms");
            } else {
                logger.info("文件上传失败");
            }
        } catch (OSSException oe) {
            logger.info("上传文件出错", oe);
        } finally {
            ossClient.shutdown();
        }
        return uploadDto;
    }
}
