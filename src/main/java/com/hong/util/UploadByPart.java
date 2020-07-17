package com.hong.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.Future;

/**
 * @author 吴志鸿
 * @date 2020/7/16
 * @description
 */
public interface UploadByPart {

    /**
     * 分片上传核心代码。
     *
     * @param localFile  文件地址
     * @param startPos   片起始位置
     * @param partSize   片大小
     * @param partNumber 片标识
     * @param uploadId   片id
     * @param key        文件名字
     * @param bucketName 阿里云上bucket名字
     */
    Future<String> quickUpLoad(OSSClient ossClient,MultipartFile localFile, long startPos, long partSize, int partNumber, String uploadId, String key, String bucketName);

    /**
     * 将文件分块进行升序排序并执行文件上传。
     *
     * @param uploadId   片id
     * @param key        文件名字
     * @param bucketName 阿里云上bucket名字
     */
    CompleteMultipartUploadResult completeMultipartUpload(OSSClient ossClient,String uploadId,String key, String bucketName);

    /**
     * 初始化分块上传事件并生成uploadID，用来作为区分分块上传事件的唯一标识
     *
     * @param bucketName 阿里云ossbucket名字
     * @param key 文件名
     */
    String claimUploadId(OSSClient ossClient,String bucketName, String key);
}
