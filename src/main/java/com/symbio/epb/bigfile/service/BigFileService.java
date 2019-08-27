package com.symbio.epb.bigfile.service;

import java.io.InputStream;

/**
 * @Auther: lingyun.jiang
 * @Date: 2019/8/23 17:25
 * @Description:
 */
public interface BigFileService {

    void process(InputStream inputStream, String fileDate);
}
