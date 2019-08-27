package com.symbio.epb.bigfilet.test;

import java.util.regex.Pattern;

/**
 * @Auther: lingyun.jiang
 * @Date: 2019/8/9 19:08
 * @Description:
 */
public class Test {

    @org.junit.Test
    public void test(){
//        System.out.println(String.format("******** end parse %s-----%s second", "a.xml",(10000-200)/1000));
//
//        String fileDateStr ="20190802";
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
//        LocalDate fileDate = LocalDate.parse(fileDateStr,df);
//        System.out.println(fileDate);
//        ParseStatus[] types = new ParseStatus[]{ParseStatus.FAILD,ParseStatus.COMPLETED};
//        List<String> typeNames = Arrays.stream(types).map(ParseStatus::name).collect(Collectors.toList());
//        System.out.println(typeNames);

        String ss= "1, Agent";
        System.out.println(Pattern.matches("[\\w,]+",ss));

    }
}
