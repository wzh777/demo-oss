package com.hong.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hong.entity.dto.UploadDto;
import com.hong.service.AliyunOSSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author 吴志鸿
 * @date 2020/7/15
 * @description
 */
@Slf4j
@RestController
@RequestMapping(value = "/aliyun", produces = "application/json; charset=utf-8")
public class AliyunOSSController {

    @Autowired
    AliyunOSSService aliyunOSSService;

    @PostMapping("/upload")
    public UploadDto upload(@RequestParam(value = "file") MultipartFile file) {
        return aliyunOSSService.upLoad(file);
    }

    @PostMapping("/upload2")
    public UploadDto upload2(@RequestParam(value = "file") MultipartFile file) {
        return aliyunOSSService.MultipartUpload(file);
    }

    @PostMapping("/download")
    public String download(@RequestBody String name) {
        JSONObject jsonObject = JSONObject.parseObject(name);
        aliyunOSSService.downLoad(jsonObject.getString("name"));
        return "1";
    }

    @GetMapping("/list")
    public String query() {
        return JSON.toJSONString(aliyunOSSService.query());
    }
}
