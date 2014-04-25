package org.motechproject.mtraining.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mtraining.builder.TestBookmarkBuilder;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BookmarkTest {

    @Test
    public void shouldIndicateIfBookmarkWasModifiedBeforeOrAfterGivenDate() {
        DateTime currentTime = ISODateTimeUtil.nowInTimeZoneUTC();
        DateTime oneHourAfterCurrentTime = currentTime.plusHours(1);
        DateTime oneHourBeforeCurrentTime = currentTime.minusHours(1);

        Bookmark bookmarkModifiedNow = new TestBookmarkBuilder().modifiedOn(currentTime).build();

        assertTrue(bookmarkModifiedNow.wasModifiedAfter(oneHourBeforeCurrentTime));
        assertFalse(bookmarkModifiedNow.wasModifiedAfter(currentTime));
        assertFalse(bookmarkModifiedNow.wasModifiedAfter(oneHourAfterCurrentTime));
    }
}
