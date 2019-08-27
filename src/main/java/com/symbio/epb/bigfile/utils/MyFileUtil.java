package com.symbio.epb.bigfile.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

/**
 * The<code>Class  FileUtils </code>
 *
 * @author benju.xie
 * @since 2018/9/4
 */

public class MyFileUtil {

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separator + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + File.separator + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static  void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File fileDir = new File(filePath);
            fileDir.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void mkDirs(File file) {
        if (file.getParentFile().exists()) {
            file.mkdir();
        } else {
            mkDirs(file.getParentFile());
            file.mkdir();
        }
    }
    
    /**
     * 方法执行完成后sourceFile会被关闭
     */
	public static void copyFile(File sourceFile, String toFileDir, String toFileName) throws IOException {
		if (sourceFile == null || toFileDir == null || toFileName == null) {
			return;
		}
		mkDirs(new File(toFileDir));
		String toFilePath = toFileDir + toFileName;
		FileInputStream fis = null;
		fis = new FileInputStream(sourceFile);
		FileUtils.copyInputStreamToFile(fis, new File(toFilePath));
	}
	
	/**
	 * 方法执行完成后sourceFile会被关闭
	 */
	public static void copyFile(InputStream sourceFile, String toFileDir, String toFileName) throws IOException {
		if (sourceFile == null || toFileDir == null || toFileName == null) {
			return;
		}
		mkDirs(new File(toFileDir));
		String toFilePath = toFileDir + toFileName;
		FileUtils.copyInputStreamToFile(sourceFile, new File(toFilePath));
	}
}
