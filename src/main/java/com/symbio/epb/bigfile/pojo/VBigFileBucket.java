package com.symbio.epb.bigfile.pojo;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * The<code>Class  VBigFileBucket </code>
 *
 * @author benju.xie
 * @since 2018/8/6
 */

public class VBigFileBucket implements Serializable {

    private static final long serialVersionUID = 2346014019190422856L;

    MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
