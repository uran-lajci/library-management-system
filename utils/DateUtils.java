package org.kodelabs.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class DateUtils {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String DATE_FORMAT_TEMPLATE = "MM-dd-yyyy hh:mm:ss a";
    private static final String DATE_FORMAT_EXTENDED_TEMPLATE = "MMM dd yyyy, hh:mm a";
    private static final String DATE_FORMAT_ROTATION = "MM/dd/YYYY";
    private static final String TIME_FORMAT = "hh:mma";

    private static final SimpleDateFormat DEFAULT_SIMPLE_DATE_FORMAT =
            new SimpleDateFormat(DATE_FORMAT);

    private static final SimpleDateFormat DEFAULT_DATE_FORMAT_EXTENDED_TEMPLATE =
            new SimpleDateFormat(DATE_FORMAT_EXTENDED_TEMPLATE);

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_FORMAT);

    private static final DateTimeFormatter DEFAULT_TEMPLATE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_FORMAT_TEMPLATE);
    private static final SimpleDateFormat DEFAULT_ROTATION_DATE_FORMATTER =
            new SimpleDateFormat(DATE_FORMAT_ROTATION);
    private static final SimpleDateFormat DEFAULT_TIME_FORMATTER = new SimpleDateFormat(TIME_FORMAT);

    public static String getDefaultDateFormat() {
        return DATE_FORMAT;
    }

    public static SimpleDateFormat getDefaultSimpleDateFormat() {
        return DEFAULT_SIMPLE_DATE_FORMAT;
    }

    public static DateTimeFormatter getDefaultDateTimeFormatter() {
        return DEFAULT_DATE_TIME_FORMATTER;
    }

    public static SimpleDateFormat getDefaultTimeFormatter() {
        return DEFAULT_TIME_FORMATTER;
    }

    public static DateTimeFormatter getDateFormatTemplate() {
        return DEFAULT_TEMPLATE_DATE_FORMATTER;
    }

    public static SimpleDateFormat getRotationSimpleDateFormat() {
        return DEFAULT_ROTATION_DATE_FORMATTER;
    }

    public static Date parseDate(String stringDate) {
        try {
            return DEFAULT_SIMPLE_DATE_FORMAT.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }

    public static LocalDateTime parseLocalDateTime(String date) {
        try {
            return Instant.parse(date).atOffset(ZoneOffset.UTC).toLocalDateTime();
        } catch (Exception e) {
            e.printStackTrace();
            return Instant.now().atOffset(ZoneOffset.UTC).toLocalDateTime();
        }
    }

    public static String parseDate(Date date) {
        return DEFAULT_SIMPLE_DATE_FORMAT.format(date);
    }

    public static String parseToExtendedDateDefault(Date date) {
        return DEFAULT_DATE_FORMAT_EXTENDED_TEMPLATE.format(date);
    }

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    /**
     * @param from first date
     * @param to second date
     * @return number of days between two dates, ignoring hours
     */
    public static long daysBetween(Date from, Date to) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(from);
        LocalDate fromDate =
                LocalDate.of(
                        fromCalendar.get(Calendar.YEAR),
                        fromCalendar.get(Calendar.MONTH) + 1,
                        fromCalendar.get(Calendar.DATE));
        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(to);
        LocalDate toDate =
                LocalDate.of(
                        toCalendar.get(Calendar.YEAR),
                        toCalendar.get(Calendar.MONTH) + 1,
                        toCalendar.get(Calendar.DATE));
        return DAYS.between(fromDate, toDate) + 1;
    }

    public static long daysBetween(LocalDate from, LocalDate to) {
        return DAYS.between(from, to) + 1;
    }

    public static int monthsBetween(LocalDateTime from, LocalDateTime to) {
        return (int) ChronoUnit.MONTHS.between(from.withDayOfMonth(1), to.withDayOfMonth(1));
    }

    public static long daysBetween(ZonedDateTime from, ZonedDateTime to) {
        // return to.getDayOfYear() - from.getDayOfYear() + 1;
        return DAYS.between(from, to) + 1;
    }

    public static String formattedTime(long millis) {
        try {
            String format = "";
            Duration duration = Duration.ofMillis(millis);
            int hours = duration.toHoursPart();
            int minutes = duration.toMinutesPart();
            List<Integer> params = new ArrayList<>();
            if (hours > 0) {
                format = "%2dh ";
                params.add(hours);
            }
            if (minutes > 0) {
                format += "%2dmin";
                params.add(minutes);
            }
            return String.format(format, params.toArray());
        } catch (Exception e) {
            return millis + "";
        }
    }
}
