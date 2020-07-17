package com.hong.service;

import com.hong.entity.dto.UploadDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * @author 吴志鸿
 * @date 2020/7/15
 * @description
 */
public interface AliyunOSSService {
    UploadDto upLoad(MultipartFile file);

    UploadDto MultipartUpload(MultipartFile file);

    void downLoad(String objectName);

    List<String> query();
}
