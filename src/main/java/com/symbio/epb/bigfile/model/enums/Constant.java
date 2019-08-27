package com.symbio.epb.bigfile.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Constant {

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
    public static List<String> needUgLobSite= Arrays.asList("convismservice_concentrixangelescityph01","sitelbsasbgis_sitelbaguiocityph01","sitelbsasbgis_tarlaccityph01");

    public static String NORMAL_REG= "((#)?N/A)|[\\-]";

    public static String EXCELSUFFIX=".xlsx";
    public static String UNDERLINE="_";



}
