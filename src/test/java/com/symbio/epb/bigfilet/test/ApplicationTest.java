package com.symbio.epb.bigfilet.test;

import com.symbio.epb.bigfile.Application;
import com.symbio.epb.bigfile.service.BigFileService;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {
    protected static Logger logger = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private  BigFileService parserHandler;

    private static String file_pattern ="att_ppro_masterfile_\\d{8}.xlsx";

    @Test
    public void splitMutilFile(){
        String parentPath ="D:\\business\\sprint91\\master\\PPro Template-0119.xlsx";
        File pFile = new File(parentPath);
        if(pFile.isDirectory()){
            File[] files = pFile.listFiles();
            List<File> collect = Arrays.stream(files).filter(f -> Pattern.matches(file_pattern,f.getName())).collect(Collectors.toList());
            for(File f: collect){
                long beginTime = System.currentTimeMillis();
                logger.info(String.format("******** begin parse %s", f.getName()));
                InputStream is = null;
                try {
                    is = new FileInputStream(f);
                    String fileDate = f.getName().split("_")[3];
                    fileDate = fileDate.substring(0, fileDate.indexOf(".xlsx"));
                    parserHandler.process(is,fileDate);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(is);
                }
                long endTime = System.currentTimeMillis();
                logger.info(String.format("******** end parse %s-----%s second", f.getName(),(endTime-beginTime)/1000));
            }
        }

    }


}
