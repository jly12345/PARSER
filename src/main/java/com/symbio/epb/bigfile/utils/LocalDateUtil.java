package com.symbio.epb.bigfile.utils;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Auther: lingyun.jiang
 * @Date: 2019/1/9 14:17
 * @Description:
 */
public class LocalDateUtil {

    private static DateTimeFormatter yMdhms = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDateTime getBeginOfToday(){
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));
        LocalDateTime beginOfDay = now.with(LocalTime.MIN);
        return beginOfDay;
    }

    public static LocalDateTime getEndOfToday(){
        LocalDateTime  now = LocalDateTime.now(ZoneId.of("America/New_York"));
        LocalDateTime endOfDay = now.with(LocalTime.MAX);

        return endOfDay;

    }

    public static LocalDateTime getFirstDayOfWeek(){
        ZoneId zone =ZoneId.of("America/New_York");
        LocalDateTime localDateTime = LocalDateTime.now(zone);
        LocalDateTime firstDay = localDateTime.with(DayOfWeek.MONDAY);
        return firstDay.with(LocalTime.MIN);

    }

    public static LocalDateTime getEndDayOfWeek() {
        ZoneId zone =ZoneId.of("America/New_York");
        LocalDateTime localDateTime = LocalDateTime.now(zone);
        LocalDateTime endDay = localDateTime.with(DayOfWeek.SUNDAY);
        return endDay;
    }

    public static Date get6WeekBeforeDate() {
        LocalDateTime now = LocalDateTime.now();
        now = now.minusWeeks(5).with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        ZoneId zoneId = ZoneId.of("America/New_York");
        ZonedDateTime zdt = now.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    public static Date getCurentDate() {
        ZoneId zoneId = ZoneId.of("America/New_York");
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zdt = now.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    /**
     * Get the date of a particular week for a period of time
     * @param start
     * @param end
     * @param dayOfWeek
     * @return
     */
    public static List<String> getWeekDateInRange(Date start, Date end, DayOfWeek dayOfWeek) {
        LocalDate startTime = transformDateToLocalDate(start);
        LocalDate endTime =transformDateToLocalDate(end);
        List<String> array = new ArrayList<>();
        long days = endTime.toEpochDay() - startTime.toEpochDay();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd", Locale.US);
        for(int i=0;i<=days/7;i++){
            LocalDate date = startTime.with(dayOfWeek);
            LocalDate localDate = date.plusWeeks(i);
//            array.add(localDate.format(DateTimeFormatter.ofPattern(DateUtils.DATE_PATTERN)));
            array.add(localDate.format(fmt));
        }

        return  array;

    }

    public static Date transformLocalDateToDate(LocalDate dateTime){
        ZonedDateTime zdt = dateTime.atStartOfDay(ZoneId.of("America/New_York"));
        Date date = Date.from(zdt.toInstant());
        return date;
    }


    public static LocalDateTime transformDateToLocalDateTime(Date date){
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    public static LocalDate transformDateToLocalDate(Date date){
        return transformDateToLocalDateTime(date).toLocalDate();
    }

    public static int getDayCountOfMonth(LocalDate localDate) {
        return localDate.getDayOfMonth();
    }

    /**
     *  Sunday = 1 in US and = 7 in France
     */
    public static Date getCurrentWeekSunday() {
        ZoneId zoneId = ZoneId.of("America/New_York");
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zdt = now.atZone(zoneId);
        zdt = zdt.with(WeekFields.of(Locale.US).dayOfWeek(), 1).with(LocalTime.MIN);
        Date date = Date.from(zdt.toInstant());
        return  date;
    }

    public static String getLastMonthOfToday() {
        ZoneId zoneId = ZoneId.of("America/New_York");
        LocalDate now = LocalDate.now(zoneId);
        now =now.minusMonths(1);
        return now.format(ymd);
    }

//    public static Calendar getFirstDayOfMonth() {
//        Calendar cal = new GregorianCalendar(getDefaultTz());
//        cal.setTimeZone(getDefaultTz());
//
//        int firstDay = getDayOfFiscalMonth();
//        if (cal.get(Calendar.DATE) < firstDay) {
//            cal.add(Calendar.MONTH, -1);
//        }
//        cal.set(Calendar.DATE, firstDay == 1 ? cal.getActualMinimum(Calendar.DATE) : firstDay);
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//
//        return cal;
//    }
//    public static TimeZone getDefaultTz() {
//        TimeZone timeZoneNY = TimeZone.getTimeZone("America/New_York");
//        return timeZoneNY;
//    }
//
//    public static Calendar getEndDayOfWeek2() {
//        Calendar cal = new GregorianCalendar(getDefaultTz());
//        cal.setTimeZone(getDefaultTz());
//
//        cal.setFirstDayOfWeek(Calendar.MONDAY);
//        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//
//        return cal;
//    }

//    public static void main(String[] args)   {
//
////        System.out.println(LocalDateUtil.transformLocalDateToDate(LocalDate.parse("2018-11-11")));
//        System.out.println(LocalDateUtil.getCurrentWeekSunday("2019-05-09 00:00:00"));
//    }



}
