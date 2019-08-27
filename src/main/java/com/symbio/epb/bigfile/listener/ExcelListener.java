package com.symbio.epb.bigfile.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.symbio.epb.bigfile.model.enums.Constant;
import com.symbio.epb.bigfile.model.enums.SheetNameType;
import com.symbio.epb.bigfile.pojo.DomainLobSite;
import com.symbio.epb.bigfile.utils.ExcelDataUtil;
import com.symbio.epb.bigfile.utils.MyStringUtils;
import com.symbio.epb.bigfile.utils.TitleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExcelListener extends AnalysisEventListener {

    public static Logger logger = LoggerFactory.getLogger(ExcelListener.class);
    List<List<String>> rosterSourceData = new ArrayList<>();
    private List<List<String>> perMostSourceData = new ArrayList<>();
    private List<List<String>> perOdrepeatsSourceData = new ArrayList<>();
    private List<List<String>> perAdjSourceData = new ArrayList<>();
    private List<List<String>> perUgSourceData = new ArrayList<>();

    private List<DomainLobSite> domainlobsiteList;
    private String fileDate;

    private String splitFilePath;

    public ExcelListener(List<DomainLobSite> domainlobsiteList, String fileDate, String splitFilePath) {
        this.domainlobsiteList = domainlobsiteList;
        this.fileDate = fileDate;
        this.splitFilePath = splitFilePath;
    }

    @Override
    public void invoke(Object object, AnalysisContext context) {
        if (StringUtils.isEmpty(((List<String>) object).get(0))) {
            return;
        }
        Sheet currentSheet = context.getCurrentSheet();
        SheetNameType sheetNameType = SheetNameType.getTypeByName(currentSheet.getSheetName());

        if (sheetNameType == SheetNameType.HIERARCHY) {
            rosterSourceData.add((List<String>) object);
        } else if (sheetNameType == SheetNameType.MOST) {
            perMostSourceData.add((List<String>) object);
        } else if (sheetNameType == SheetNameType.ODREPEATS) {
            perOdrepeatsSourceData.add((List<String>) object);
        } else if (sheetNameType == SheetNameType.ADJ) {
            perAdjSourceData.add((List<String>) object);
        } else if (sheetNameType == SheetNameType.UG) {
            perUgSourceData.add((List<String>) object);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Sheet currentSheet = context.getCurrentSheet();
        try {
            fixData(SheetNameType.getTypeByName(currentSheet.getSheetName()));
        } catch (Exception e) {
            logger.error("parse failed", e);
        }
    }

    private void fixData(SheetNameType type) {
        if (type == SheetNameType.HIERARCHY) {
            rosterSourceData = rosterSourceData.stream().filter(d -> d.size() == 8).collect(Collectors.toList());
            rosterSourceData = rosterSourceData.stream().filter(d -> {
                boolean col0 = !StringUtils.isEmpty(d.get(0)) && !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(0)));
                boolean col4 = col0 && !StringUtils.isEmpty(d.get(4)) && !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(4)));
                boolean col5 = col4 && !StringUtils.isEmpty(d.get(5)) && !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(5)));
                boolean col6 = col5 && !StringUtils.isEmpty(d.get(6)) && !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(6)));
                return col6;
            }).collect(Collectors.toList());

            for (DomainLobSite domainLobSite : domainlobsiteList) {
                List<List<String>> siteRosterData = rosterSourceData.stream().filter(d -> MyStringUtils.toTrim(d.get(6)).equals(domainLobSite.getSite())).filter(Constant.distinctByKey(o -> o.get(0))).collect(Collectors.toList());
                if (siteRosterData.size() > 0) {
                    createRosterFile(domainLobSite, siteRosterData);
                }
            }
            rosterSourceData.clear();
        } else if (type == SheetNameType.MOST) {
            perMostSourceData = perMostSourceData.stream().filter(d -> {
                boolean col0 = !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(0))) && !StringUtils.isEmpty(d.get(0));
                boolean col1 = col0 && !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(1))) && !StringUtils.isEmpty(d.get(1));
                boolean col2 = col1 && !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(2))) && !StringUtils.isEmpty(d.get(2));
                return col2;
            }).collect(Collectors.toList());

            for (DomainLobSite domainLobSite : domainlobsiteList) {
                List<List<String>> siteMostData = perMostSourceData.stream().filter(d -> MyStringUtils.toTrim(d.get(4)).equals(domainLobSite.getSite())).collect(Collectors.toList());
                if (siteMostData.size() > 0) {
                    createMost(siteMostData, domainLobSite);
                }
            }
            perMostSourceData.clear();
        } else if (type == SheetNameType.ODREPEATS) {
            perOdrepeatsSourceData = perOdrepeatsSourceData.stream().filter(d -> {
                boolean col0 = !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(0))) && !StringUtils.isEmpty(d.get(0));
                boolean col6 = col0 && !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(6))) && !StringUtils.isEmpty(d.get(6));
                boolean col7 = col6 && !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(7))) && !StringUtils.isEmpty(d.get(6));
                return col7;
            }).collect(Collectors.toList());

            for (DomainLobSite domainLobSite : domainlobsiteList) {
                List<List<String>> siteODData = perOdrepeatsSourceData.stream().filter(d -> MyStringUtils.toTrim(d.get(6)).equals(domainLobSite.getSite())).collect(Collectors.toList());
                if (siteODData.size() > 0) {
                    createODREPEATS(siteODData,domainLobSite);
                }
            }
            perOdrepeatsSourceData.clear();
        } else if (type == SheetNameType.ADJ) {
            perAdjSourceData = perAdjSourceData.stream().filter(d -> !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(0)))).collect(Collectors.toList());
            for (DomainLobSite domainLobSite : domainlobsiteList) {
                List<List<String>> siteAdjData = perAdjSourceData.stream().filter(d -> MyStringUtils.toTrim(d.get(1)).equals(domainLobSite.getSite())).collect(Collectors.toList());
                if (siteAdjData.size() > 0) {
                    createAdj(siteAdjData, domainLobSite);
                }
            }
            perAdjSourceData.clear();
        } else if (type == SheetNameType.UG) {
            perUgSourceData = perUgSourceData.stream().filter(d -> !Pattern.matches(Constant.NORMAL_REG, MyStringUtils.toTrim(d.get(0)))).collect(Collectors.toList());
            for (DomainLobSite domainLobSite : domainlobsiteList) {
                List<List<String>> siteUgData = perUgSourceData.stream().filter(d -> MyStringUtils.toTrim(d.get(1)).equals(domainLobSite.getSite())).collect(Collectors.toList());
                if (siteUgData.size() > 0) {
                    createUg(siteUgData, domainLobSite);
                }
            }
            perUgSourceData.clear();
        }

    }

    private void createODREPEATS(List<List<String>> siteODData, DomainLobSite domainLobSite) {
        List<String> kpiName = Arrays.asList("ATTUID", "Calls","1&Done_7Day","1&Done Rt","7D Repeat Rt");

        siteODData = siteODData.stream().filter(Constant.distinctByKey(o -> o.get(0))).map(d -> {
            List<String> sub = new ArrayList<>();
            sub.add(d.get(0));
            sub.add(d.get(2));
            sub.add(d.get(3));
            sub.add(d.get(4));
            sub.add(d.get(5));
            return sub;
        }).collect(Collectors.toList());
        ExcelDataUtil.saveExcel(siteODData, domainLobSite, SheetNameType.ODREPEATS, kpiName, fileDate, splitFilePath);
    }


    private void createUg(List<List<String>> sitePerUgData, DomainLobSite domainLobSite) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
        String timestamp = LocalDate.parse(fileDate, fmt).format(fmt);
        List<String> orginTitle = perUgSourceData.get(0);
        int tagetIndex = TitleUtil.compareAndGetIndex(orginTitle, timestamp, fmt);

        if (tagetIndex == -1) {
            return;
        }
        sitePerUgData = sitePerUgData.stream().filter(Constant.distinctByKey(o -> o.get(0))).map(d -> {
            List<String> sub = new ArrayList<>();
            sub.add(d.get(0));
            sub.add(d.get(tagetIndex));
            return sub;
        }).collect(Collectors.toList());

        List<String> kpiName = Arrays.asList("ATTUID", "Upgrade");


        ExcelDataUtil.saveExcel(sitePerUgData, domainLobSite, SheetNameType.UG, kpiName, fileDate, splitFilePath);
    }

    private void createAdj(List<List<String>> sitePerAdjData, DomainLobSite domainLobSite) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
        String timestamp = LocalDate.parse(fileDate, fmt).format(fmt);
        List<String> orginTitle = perAdjSourceData.get(0);
        int tagetIndex = TitleUtil.compareAndGetIndex(orginTitle, timestamp, fmt);

        if (tagetIndex == -1) {
            return;
        }
        sitePerAdjData = sitePerAdjData.stream().filter(Constant.distinctByKey(o -> o.get(0))).map(d -> {
            List<String> sub = new ArrayList<>();
            sub.add(d.get(0));
            sub.add(d.get(tagetIndex));
            return sub;
        }).collect(Collectors.toList());

        List<String> kpiName = Arrays.asList("ATTUID", "Adjustments");

        ExcelDataUtil.saveExcel(sitePerAdjData, domainLobSite, SheetNameType.ADJ, kpiName, fileDate, splitFilePath);

    }

    private void createMost(List<List<String>> siteMostData, DomainLobSite domainLobSite) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter fmt2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String timestamp = LocalDate.parse(fileDate, fmt).format(fmt2) + " Actual";
        List<String> mostOrginTitle = perMostSourceData.get(0);
        int tagetIndex = mostOrginTitle.indexOf(timestamp);

        if(tagetIndex==-1){
            return;
        }

        List<String> kpiName = siteMostData.stream().map(d -> d.get(2)).distinct().collect(Collectors.toList());

        siteMostData = siteMostData.stream().map(d -> {
            List<String> sub = d.subList(0, 6);
            sub.add(d.get(tagetIndex));
            return sub;
        }).collect(Collectors.toList());

        kpiName.add(0, "ATTUID");

        ExcelDataUtil.saveExcel(siteMostData, domainLobSite, SheetNameType.MOST, kpiName, fileDate, splitFilePath);
    }

    private void createRosterFile(DomainLobSite domainLobSite, List<List<String>> siteRosterData) {
        List<String> kpiName = Arrays.asList("HRID", "ATTUID", "", "LAST NAME", "FIRST NAME", "", "TEAM NAME", "MANAGER NAME", "EMP_TYPE_FUNC");
        ExcelDataUtil.saveExcel(siteRosterData, domainLobSite, SheetNameType.HIERARCHY, kpiName, fileDate, splitFilePath);
    }


}