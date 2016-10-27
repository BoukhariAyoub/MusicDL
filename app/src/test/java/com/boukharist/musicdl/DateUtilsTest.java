package com.boukharist.musicdl;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by Administrateur on 11-Oct-16.
 */

public class DateUtilsTest {

    public DateUtilsTest() {
    }

    @Test
    public void dateUtilsFormat_isCorrect() throws Exception {
        long epoc = 1446885450; //7th Nov 2015
        Date date = DateUtils.epocSecondsToDate(epoc);
        assertEquals("Date time in millis is wrong",
                epoc * 1000, date.getTime());


        String day = DateUtils.dateToDayDateString(date, true);
        assertEquals("Day is wrong", "SAT", day);
    }
}
