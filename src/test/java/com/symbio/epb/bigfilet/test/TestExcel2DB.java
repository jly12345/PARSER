package com.symbio.epb.bigfilet.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.symbio.epb.bigfile.Application;
import com.symbio.epb.bigfile.utils.MyStringUtils;
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class TestExcel2DB {
	@Test
    public void testDemo() throws InterruptedException, IOException {
		InputStream is = null;
		XSSFWorkbook xwb = null;
		OutputStream os = null;
		try {
			is = new FileInputStream("D:\\test\\domain_lob_site.xlsx");
			xwb = new XSSFWorkbook(is);
			XSSFSheet sheet = xwb.getSheetAt(0);
			os = new FileOutputStream(new File("D:\\test\\toDB.txt"));
			XSSFRow row;
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
              row = sheet.getRow(i);
              String site = null;
              String lob = null;
              String domain = null;
              String supportSplit = null;
              StringBuilder str = new StringBuilder("INSERT INTO domain_lob_site (site,lob_site,domain,lob) VALUES (");
              for (int j = 0; j <= 4; j++) {
                  if (j == 0) {
                      site = row.getCell(j).toString();
                      str.append("'").append(site).append("',");
                  } else if (j == 1) {
                      lob = row.getCell(j).toString();
                      str.append("'").append(lob).append("',");
                  } else if (j == 2){
                      domain = row.getCell(j).toString();
                      str.append("'").append(domain).append("',");
	              } else if (j == 3){
	            	  lob = row.getCell(j).toString();
	            	  str.append("'").append(lob).append("');\n");
	              } else if (j == 4) {
	            	  supportSplit = row.getCell(j).toString();
				  }
              }
              if ("FALSE".equals(supportSplit)) {
            	  continue;
              }
              os.write(str.toString().getBytes());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (os!=null) {
				os.close();
			}
			if (xwb!=null) {
				xwb.close();
			}
			if (is!=null) {
				is.close();
			}
		}
	}
	@Test
	public void testDemo2() throws InterruptedException, IOException {
		InputStream is = null;
		XSSFWorkbook xwb = null;
		OutputStream os = null;
		try {
			is = new FileInputStream("D:\\test\\domain_lob_site.xlsx");
			xwb = new XSSFWorkbook(is);
			XSSFSheet sheet = xwb.getSheetAt(0);
			os = new FileOutputStream(new File("D:\\test\\to.txt"));
			XSSFRow row;
			Set<String> set = new TreeSet<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
//				String site = null;
				String lob = null;
//				String domain = null;
				for (int j = row.getFirstCellNum(); j < 4; j++) {
				 if (j == 1) {
						lob = row.getCell(j).toString().split("_")[0];
//						lob = MyStringUtils.toUpperCaseFirstOne(lob)+"Listener       " + lob;
//						str.append(lob).append("\n");
						set.add(lob);
					}
				}
			}
			for (String string : set) {
				StringBuilder str = new StringBuilder("");
				string = MyStringUtils.toUpperCaseFirstOne(string)+"       " + string;
				str.append(string).append("\n");
				os.write(str.toString().getBytes());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (os!=null) {
				os.close();
			}
			if (xwb!=null) {
				xwb.close();
			}
			if (is!=null) {
				is.close();
			}
		}
	}
}
