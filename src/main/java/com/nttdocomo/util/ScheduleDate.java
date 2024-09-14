package com.nttdocomo.util;

import java.util.Calendar;
import java.util.TimeZone;

public class ScheduleDate {
    public static final int ONETIME = 0x01;
    public static final int DAILY = 0x02;
    public static final int WEEKLY = 0x04;
    public static final int MONTHLY = 0x08;
    public static final int YEARLY = 0x10;

    private final int type;

    private int year;
    private int month;
    private int dayOfMonth;
    private int dayOfWeek;
    private int hourOfDay;
    private int minute;

    public ScheduleDate(int type) {
        if (type != ONETIME && type != DAILY && type != WEEKLY && type != MONTHLY && type != YEARLY) {
            throw new IllegalArgumentException("invalid ScheduleDate type");
        }

        this.type = type;
    }

    public ScheduleDate(int type, TimeZone zone) {
        this(type);
    }

    public int getType() {
        return this.type;
    }

    public int get(int field) {
        switch (field) {
            case Calendar.YEAR:
                return year;
            case Calendar.MONTH:
                return month;
            case Calendar.DAY_OF_MONTH:
                return dayOfMonth;
            case Calendar.DAY_OF_WEEK:
                return dayOfWeek;
            case Calendar.HOUR_OF_DAY:
                return hourOfDay;
            case Calendar.MINUTE:
                return minute;
        }
        throw new IllegalArgumentException("invalid field");
    }

    public void set(int field, int value) {
        // TODO: check valid arguments
        switch (field) {
            case Calendar.YEAR:
                year = value;
            case Calendar.MONTH:
                month = value;
            case Calendar.DAY_OF_MONTH:
                dayOfMonth = value;
            case Calendar.DAY_OF_WEEK:
                dayOfWeek = value;
            case Calendar.HOUR_OF_DAY:
                hourOfDay = value;
            case Calendar.MINUTE:
                minute = value;
            default:
                throw new IllegalArgumentException("invalid field");
        }
    }
}
