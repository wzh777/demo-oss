package com.hong.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 吴志鸿
 * @date 2020/7/15
 * @description
 */
@Data
@NoArgsConstructor
public class UploadDto {
    /**
     * 上传成功文件的名字
     */
    private String name;

    /**
     * 上传成功访问文件的url
     */
    private String url;

    /**
     * 上传成功花费的时间
     */
    private String costTime;

    /**
     * 上传是否成功标识
     */
    boolean success = true;

    /**
     * 错误代码信息
     */
    private String errorMsg;



    public UploadDto(boolean success, String errorMsg) {
        this.success = success;
        this.errorMsg = errorMsg;
    }
}
