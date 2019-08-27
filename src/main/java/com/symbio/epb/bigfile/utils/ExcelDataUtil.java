package com.symbio.epb.bigfile.utils;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Font;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.TableStyle;
import com.symbio.epb.bigfile.model.enums.Constant;
import com.symbio.epb.bigfile.model.enums.SheetNameType;
import com.symbio.epb.bigfile.pojo.DomainLobSite;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @Auther: lingyun.jiang
 * @Date: 2019/8/16 16:37
 * @Description:
 */
@Component
public class ExcelDataUtil {






    public static TableStyle createTableStyle() {
        TableStyle tableStyle = new TableStyle();
        Font headFont = new Font();
        headFont.setBold(true);
        headFont.setFontHeightInPoints((short)10);
        headFont.setFontName("Consolas");
        tableStyle.setTableHeadFont(headFont);

        Font contentFont = new Font();
        contentFont.setFontHeightInPoints((short)10);
        contentFont.setFontName("Consolas");
        tableStyle.setTableContentFont(contentFont);
        tableStyle.setTableContentBackGroundColor(IndexedColors.WHITE);
        return tableStyle;
    }

    /**
     *
     * @param sitePerAdjData
     * @param domainLobSite
     * @param sheetType
     * @param headList
     * @param fileDate
     */
    public static void saveExcel(List<List<String>> sitePerAdjData, DomainLobSite domainLobSite, SheetNameType sheetType, List<String> headList,String fileDate,String splitFilePath) {
        String lobName = domainLobSite.getLobSite().split(Constant.UNDERLINE)[0].toUpperCase();
        String domainName = domainLobSite.getDomain();
        String dirPath = splitFilePath + File.separator + fileDate + File.separator + domainName + File.separator + lobName + File.separator;
        MyFileUtil.mkDirs(new File(dirPath));
        String filePath = dirPath + sheetType.getFileKey() + Constant.UNDERLINE + domainLobSite.getLobSite() + Constant.UNDERLINE + fileDate + Constant.EXCELSUFFIX;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            Sheet sheet1 = new Sheet(1, 1);
            sheet1.setTableStyle(createTableStyle());
            sheet1.setSheetName(sheetType.getTypeName());
            ExcelWriter writer = EasyExcelFactory.getWriter(fos);
            sheet1.setAutoWidth(Boolean.TRUE);
            sheet1.setHead(TitleUtil.createHead(headList));
            writer.write1(TitleUtil.createData(sheetType, sitePerAdjData, headList), sheet1);
            writer.finish();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }
}
