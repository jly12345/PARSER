package com.symbio.epb.bigfile.utils;

import com.symbio.epb.bigfile.model.enums.SheetNameType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Auther: lingyun.jiang
 * @Date: 2019/8/16 11:17
 * @Description:
 */
public class TitleUtil {
    private static List<List<Object>> createRosterData(List<List<String>> siteRosterData) {
        List<List<Object>> datas = new ArrayList<>();
        for (int i = 0; i < siteRosterData.size(); i++) {
            List<String> rosterData = siteRosterData.get(i);
            List<Object> data = new ArrayList<>();
            data.add(rosterData.get(0)); //HRID
            data.add(rosterData.get(0)); //ATTUID
            data.add("");
            data.add(rosterData.get(2)); //Last Name
            data.add(rosterData.get(1));//FIRST NAME
            data.add("");//
            data.add("TEAM "+ rosterData.get(5));
            data.add(""); //MANAGER NAME
            data.add(rosterData.get(3)); //EMP_TYPE_FUNC
            datas.add(data);
        }

        return datas;
    }


    public static List<List<String>> createHead(List<String> headList) {
        List<List<String>> head = new ArrayList<>();
        for (int i = 0; i < headList.size(); i++) {
            List<String> headCoulumn1 = new ArrayList<>();
            headCoulumn1.add(headList.get(i));
            head.add(headCoulumn1);
        }
        return head;

    }

    public static List<List<Object>> createData(SheetNameType nameType, List<List<String>> siteData,List<String> kpiNames) {
        if(nameType == SheetNameType.HIERARCHY ){
            return createRosterData(siteData);
        }else if(nameType == SheetNameType.MOST){
            return createMostData(siteData,kpiNames);
        }else if(nameType == SheetNameType.ODREPEATS){
            return createData(siteData);
        }else if(nameType == SheetNameType.ADJ){
            return createData(siteData);
        }else if(nameType == SheetNameType.UG){
            return createData(siteData);
        }
        return null;
    }

    private static List<List<Object>> createData(List<List<String>> siteData) {
        return siteData.stream().map(d -> new ArrayList<Object>(d)).collect(Collectors.toList());
    }



    private static List<List<Object>> createMostData(List<List<String>> siteData, List<String> kpiNames) {
        Map<String,List<Object>> datas = new HashMap<>();
        for(List<String> orgData:siteData){
            List<Object> rowData;
            if(datas.containsKey(orgData.get(1))){
                rowData =  datas.get(orgData.get(1));
            }else {
                rowData = new ArrayList<>();
                rowData.add(orgData.get(1));
                datas.put(orgData.get(1),rowData);
            }
            for(String kpiName:kpiNames){
                if(kpiName.equals(orgData.get(2))){
                    rowData.add(orgData.get(6));
                    break;
                }
            }
        }

        List<List<Object>> result = new ArrayList<>();
        for(List<Object> one: datas.values()){
            result.add(one);
        }
        return  result;
    }

    public static int compareAndGetIndex(List<String> orginTitle, String ele, DateTimeFormatter fmt) {
        for(int i=0;i<orginTitle.size();i++){
            if(Pattern.matches("^[0-9]*$",orginTitle.get(i))){
                String eleParse ;
                try {
                    Date d = DateUtil.getJavaDate(Double.parseDouble(orginTitle.get(i)));
                    LocalDate localDate = LocalDateUtil.transformDateToLocalDate(d);
                    eleParse = localDate.format(fmt);
                } catch (NumberFormatException e) {
                    return -1;
                }
                if(eleParse.equals(ele)){
                    return i;
                }
            }
        }
        return -1;
    }
}
