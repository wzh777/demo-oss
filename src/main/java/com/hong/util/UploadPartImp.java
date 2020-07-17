package com.hong.util;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author 吴志鸿
 * @date 2020/7/16
 * @description
 */
@Component
public class UploadPartImp implements UploadByPart {

    // 新建一个List保存每个分块上传后的ETag和PartNumber
    protected static List<PartETag> partETags = Collections.synchronizedList(new ArrayList<>());

    /**
     * 初始化日志文件
     */
    private static Logger logger = LoggerFactory.getLogger(UploadPartImp.class);
    /**
     * 分片上传核心代码。
     *
     * @param ossClient  ossClient实例
     * @param localFile  文件地址
     * @param startPos   片起始位置
     * @param partSize   片大小
     * @param partNumber 片标识
     * @param uploadId   片id
     * @param key        文件名字
     * @param bucketName 阿里云上bucket名字
     */
    @Async
    @Override
    public Future<String> quickUpLoad(OSSClient ossClient,MultipartFile localFile, long startPos,
                                      long partSize, int partNumber, String uploadId, String key,
                                      String bucketName) {

        InputStream instream = null;
        try {
            // 获取文件流
            instream = localFile.getInputStream();
            // 跳到每个分块的开头
            long skip = instream.skip(startPos);
            if( skip != -1){
                // 创建UploadPartRequest，上传分块
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(key);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(instream);
                uploadPartRequest.setPartSize(partSize);
                uploadPartRequest.setPartNumber(partNumber);

                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                synchronized (partETags) {
                    // 将返回的PartETag保存到List中。
                    partETags.add(uploadPartResult.getPartETag());
                }
            }else {
                logger.info("分片不存在");
            }

        } catch (IOException e) {
            logger.info("IO异常",e);
        } finally {
            if (instream != null) {
                try {
                    // 关闭文件流
                    instream.close();
                } catch (IOException e) {
                    logger.info("IO异常",e);
                }
            }
        }
        return new AsyncResult<>("true");
    }

    /**
     * 将文件分块进行升序排序并执行文件上传。
     *
     * @param uploadId   片id
     * @param key        文件名字
     * @param bucketName 阿里云上bucket名字
     */
    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(OSSClient ossClient,String uploadId, String key, String bucketName) {
        // 将文件分块按照升序排序
        Collections.sort(partETags, new Comparator<PartETag>() {
            @Override
            public int compare(PartETag p1, PartETag p2) {
                return p1.getPartNumber() - p2.getPartNumber();
            }
        });

        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName,
                key, uploadId, partETags);
        // 完成分块上传
        return ossClient.completeMultipartUpload(completeMultipartUploadRequest);
    }


    /**
     * 初始化分块上传事件并生成uploadID，用来作为区分分块上传事件的唯一标识
     *
     * @param bucketName 阿里云ossbucket名字
     * @param key 文件名
     */
    @Override
    public String claimUploadId(OSSClient ossClient,String bucketName, String key) {
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
        InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
        return result.getUploadId();
    }

}
