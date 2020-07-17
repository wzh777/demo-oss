package com.hong.service;

import com.hong.entity.dto.UploadDto;
import com.hong.util.AliyunOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 吴志鸿
 * @date 2020/7/15
 * @description
 */
@Service
@Slf4j
public class AliyunOSSServiceImp implements AliyunOSSService {

    /**
     * 初始化日志文件
     */
    private static Logger logger = LoggerFactory.getLogger(AliyunOSSServiceImp.class);

    @Autowired
    AliyunOSSUtil aliyunOSSUtil;

    @Override
    public UploadDto upLoad(MultipartFile file) {

        return aliyunOSSUtil.upLoad(file);
    }

    @Override
    public UploadDto MultipartUpload(MultipartFile file) {
        return aliyunOSSUtil.multipartUpload(file);
    }

    @Override
    public void downLoad(String objectName) {
        aliyunOSSUtil.downLoad(objectName);
    }

    @Override
    public List<String> query() {
        return aliyunOSSUtil.list();
    }
}
