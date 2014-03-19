package org.motechproject.mtraining.util;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ISODateTimeUtilTest {

    @Test
    public void shouldParseDateTimeInRequiredFormat() {
        assertNotNull(ISODateTimeUtil.nowInTimeZoneUTC());
        DateTime parsedDate = ISODateTimeUtil.parse("2011-01-19T18:30:01.000Z");
        assertNotNull(parsedDate);

        DateTime parsedDateInUTC = ISODateTimeUtil.parseWithTimeZoneUTC("2014-03-21T15:29:29.403+05:30");
        assertNotNull(parsedDateInUTC);
        assertThat(parsedDateInUTC.getZone(), Is.is(DateTimeZone.UTC));
        assertThat(parsedDateInUTC.getHourOfDay(), Is.is(9));

        assertThat(parsedDate.getYear(), Is.is(2011));
        DateTime dateTime = new DateTime(2011, 1, 19, 18, 30, 1, 0, DateTimeZone.UTC);
        String dateAsString = ISODateTimeUtil.asStringInTimeZoneUTC(dateTime);
        assertThat(dateAsString, Is.is("2011-01-19T18:30:01.000Z"));
    }

    @Test
    public void shouldNotChangeTimeIfTimeIsAlreadyInUTC() {
        DateTime parsedDateInUTC = ISODateTimeUtil.parseWithTimeZoneUTC("2011-01-19T18:30:01.000Z");
        assertNotNull(parsedDateInUTC);
        assertThat(parsedDateInUTC.getHourOfDay(), Is.is(18));
        assertThat(parsedDateInUTC.getMinuteOfHour(), Is.is(30));
    }

    @Test
    public void shouldValidateIfDateIsNotInISOFormatOrIsInvalidDate() {
        assertTrue(ISODateTimeUtil.validate("2011-01-19T18:30:01.000Z"));
        assertFalse(ISODateTimeUtil.validate("2011-01-19"));
        assertFalse(ISODateTimeUtil.validate("2011-31-39T18:30:01.000Z"));
    }

}
