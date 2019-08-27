package com.symbio.epb.bigfile.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import com.symbio.epb.bigfile.pojo.VSiteFileBucket;
/**
 * The<code>Class  SiteFileUploadValidator </code>
 *
 * @author benju.xie
 * @since 2018/9/4
 */

@Component
public class SiteFileUploadValidator implements Validator {
    public static Logger logger = LoggerFactory.getLogger(SiteFileUploadValidator.class);
    public static final String VALIDATION_BIG_FILE_EMPTY = "validation.file.empty";
    public static final String VALIDATION_BIG_FILE_FORMAT_ERRO = "validation.file.format.error";
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof VSiteFileBucket) {
            VSiteFileBucket bucket = (VSiteFileBucket) target;
            MultipartFile file = bucket.getFile();
            if (!checkEmptyFile(errors, file))
                return;
            if(!checkFileFormat(errors,file))
                return;
        }
    }

    private boolean checkEmptyFile(Errors errors, MultipartFile file) {
        /** Check empty file **/
        if (file == null || file.isEmpty()) {
            logger.warn("Validation failed due to file is empty .");
            errors.reject(VALIDATION_BIG_FILE_EMPTY, VALIDATION_BIG_FILE_EMPTY);
            return false;
        }
        return true;
    }

    private boolean checkFileFormat(Errors errors, MultipartFile file){
        String fileName = file.getOriginalFilename();
        String[] array = fileName.split("_");
        try {
            array[3].substring(0, array[3].indexOf(".xlsx"));
        }catch (Exception e){
            errors.reject(VALIDATION_BIG_FILE_FORMAT_ERRO, VALIDATION_BIG_FILE_FORMAT_ERRO);
            return false;
        }
        return true;
    }
}